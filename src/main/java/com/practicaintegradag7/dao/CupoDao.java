package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.repos.CentroRepository;
import com.practicaintegradag7.repos.CupoRepository;

/**
 * Se encarga de la lógica de la configuración
 * Su funcionalidad será realizar la generación automática de los cupos
 */
@Service
public class CupoDao {

	@Autowired
	private CupoRepository cupoRepository;
	@Autowired
	private CentroRepository centroRepository;
	@Autowired
	private CentroDao centroDao;
	
	/**
	 * Método que se encarga de insertar un cupo
	 * @param cupo el cupo indicará la franja horaria y la cantidad de citas en esa franja
	 * @return el cupo insertado
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que el centro no exista
	 * @throws CupoExistException excepción que se lanzará en el caso de que el cupo ya exista
	 */
	public Cupo saveCupo(Cupo cupo) throws CentroNotFoundException, CupoExistException {
		Optional<Centro> opt = centroRepository.findByNombre(cupo.getCentro());
		if (opt.isPresent()) {
			Optional<Cupo> cupoExistente = cupoRepository.findByFechaInicioAndCentro(cupo.getFechaInicio(), cupo.getCentro());
			if (!cupoExistente.isPresent()) {
				return cupoRepository.save(cupo);
			} else throw new CupoExistException("El cupo que intentas crear ya existe");
		} else throw new CentroNotFoundException("El centro "+cupo.getCentro()+"no existe.");
	}
	
	/**
	 * Método que se encargará de obtener un cupo por la clave ID
	 * @param id una de las claves de cupo
	 * @return el cupo que ha encontrado con esa clave ID
	 * @throws CupoNotFoundException excepción que se lanzará en caso de que no exista un cupo con ese ID 
	 */
	public Cupo getCupoById (String id) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+id+" no existe!");
	}
	
	/**
	 * Método que se encarga de obtener un cupo por la fecha inicio y el centro
	 * @param fechaInicio es la fecha inicial de la franja del cupo
	 * @param centro es el centro en el cual se tiene ese cupo
	 * @return el cupo que obtiene con esa fecha y ese centro
	 * @throws CupoNotFoundException excepción que se lanzará en caso de que no exista un cupo con esa fecha y ese centro
	 */
	public Cupo getCupoByInicialDateAndCentro (LocalDateTime fechaInicio, String centro) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findByFechaInicioAndCentro(fechaInicio, centro);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo del centro "+centro+" con fecha "+fechaInicio+" no existe!!");
	}
	
	/**
	 * Método que obtiene todos los cupos guardados
	 * @return todos los cupos
	 */
	public List<Cupo> getAllCupos() {
		return cupoRepository.findAll();
	}
	
	/**
	 * Método que se encarga de obtener los cupos por centro
	 * @param centro el centro que el cual nos interesa obtener sus cupos
	 * @return una lista de los cupos de ese centro
	 */
	public List<Cupo> getAllCuposByCentro(Centro centro) {
		return cupoRepository.findCuposWithCentro(centro.getNombre());
	}
	
	/**
	 * Método que se encargara de modificar un cupo
	 * @param cupo el cupo a modificar (Sin alterar las claves que lo identifican)
	 * @return el cupo ya modificado
	 * @throws CupoNotFoundException excepción que se lanzara cuando el cupo que se quiere modificar no exista
	 */
	public Cupo updateCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) return cupoRepository.save(cupo);
		else throw new CupoNotFoundException("Cupo para modificar no encontrado");
	}
	
	/**
	 * Método que se encargara de eliminar un cupo
	 * @param cupo el cupo que nos interesa eliminar
	 * @throws CupoNotFoundException excepción que se lanzara en caso de que ese cupo no exista
	 */
	public void deleteCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt;
		opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) cupoRepository.delete(opt.get());
		else {
			opt = cupoRepository.findByFechaInicioAndCentro(cupo.getFechaInicio(), cupo.getCentro());
			if(opt.isPresent()) cupoRepository.delete(opt.get());
			else throw new CupoNotFoundException("Cupo para borrar no encontrado");
		}
	}
	
	/**
	 * Método que se encargará de generar los cupos
	 * @param configuracion la configuración que se ha guardado previamente
	 */
	public void autogenerarFranjas(Configuration configuracion) {
		cupoRepository.deleteAll();
		LocalDateTime fechaInicial = LocalDateTime.now().withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
		LocalDateTime fechaMax = LocalDateTime.parse("2022-01-31T23:59");
		int dif;
		if(configuracion.getHoraInicio().equals(configuracion.getHoraFin())) {
			dif = 24 * 3600;
		} else {
			dif = configuracion.getHoraFin().toSecondOfDay() - configuracion.getHoraInicio().toSecondOfDay();
		}
		int minutesDif = (dif / 60)/configuracion.getFranjasPorDia();
		List<Centro> centros = centroDao.getAllCitas();
		List<Cupo> cupos = new ArrayList<>();
		for(int i=0; i<centros.size(); i++) {
			while(fechaMax.compareTo(fechaInicial)>0) {
				LocalDateTime fechaFin = fechaInicial.plusMinutes(minutesDif);
				Cupo cupo = new Cupo(fechaInicial, fechaFin, configuracion.getCitasPorFranja(), centros.get(i).getNombre());
				cupos.add(cupo);
				if(((fechaFin.getHour()*3600)+fechaFin.getMinute()*60)<(configuracion.getHoraFin().getHour()*3600+configuracion.getHoraFin().getMinute()*60)) {
					fechaInicial = fechaFin;
				}else {
					fechaInicial = fechaInicial.withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
				}
				
			}
			fechaInicial = LocalDateTime.now().withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
		}
		cupoRepository.saveAll(cupos);
	}
	
	/**
	 * Método para generar cupos cuando se crea un centro
	 * @param configuracion la configuración ya guardada
	 * @param centro el centro para el cual se quiere crear los cupos
	 */
	public void autogenerarFranjaParaCentro(Configuration configuracion, Centro centro) {
		cupoRepository.deleteAll();
		LocalDateTime fechaInicial = LocalDateTime.now().withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
		LocalDateTime fechaMax = LocalDateTime.parse("2022-01-31T23:59");
		int dif;
		if(configuracion.getHoraInicio().equals(configuracion.getHoraFin())) {
			dif = 24 * 3600;
		} else {
			dif = configuracion.getHoraFin().toSecondOfDay() - configuracion.getHoraInicio().toSecondOfDay();
		}
		int minutesDif = (dif / 60)/configuracion.getFranjasPorDia();
		List<Cupo> cupos = new ArrayList<>();
		while(fechaMax.compareTo(fechaInicial)>0) {
			LocalDateTime fechaFin = fechaInicial.plusMinutes(minutesDif);
			Cupo cupo = new Cupo(fechaInicial, fechaFin, configuracion.getCitasPorFranja(), centro.getNombre());
			cupos.add(cupo);
			if(((fechaFin.getHour()*3600)+fechaFin.getMinute()*60)<(configuracion.getHoraFin().getHour()*3600+configuracion.getHoraFin().getMinute()*60)) {
				fechaInicial = fechaFin;
			}else {
				fechaInicial = fechaInicial.withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
			}
			
		}
		cupoRepository.saveAll(cupos);
	}
	
	/**
	 * Método que se encarga de eliminar todos los cupos
	 */
	public void deleteAllCupos() {
		cupoRepository.deleteAll();
	}
	
	/**
	 * Método que obtiene los cupos disponibles (que tienen plazas aun para vacunar)
	 * @param centro el centro del cual se quiere obtener estos cupos
	 * @return la lista de los cupos disponibles
	 */
	public List<Cupo> getAllCuposAvailable(Centro centro) {
		return cupoRepository.findCuposWithNcitasMoreThan(0, centro.getNombre());
	}

	/**
	 * Método para obtener todos los cupos a partir de un centro y una fecha minima
	 * @param centro el centro al cual pertenecen esos cupos
	 * @param fechaMinima la fecha minima a partir de la cual nos interesa obtener esos cupos
	 * @return la lista de los cupos disponibles dada una fecha minima y un centro
	 */
	public List<Cupo> getAllCuposAvailableAfter(Centro centro, LocalDateTime fechaMinima) {
		return cupoRepository.findCuposWithCitasMoreThan(0, centro.getNombre(), fechaMinima);
	}
	
	/**
	 * Método que obtiene todos los cupos disponibles dado un centro y un dia
	 * @param centro el centro al cual pertenecen los cupos que nos interesa obtener
	 * @param fecha el dia que tienen los cupos que queremos obtener
	 * @return la lista de cupos disponibles dada una fecha y un centro
	 */
	public List<Cupo> getAllCuposAvailableInADay(Centro centro, LocalDateTime fecha) {
		return cupoRepository.findCuposWithCitasMoreThanAndFechaInicioGreaterThanEqualAndFechaInicioLessThan(0, centro.getNombre(), fecha, fecha.plusDays(1));
	}

	/**
	 * Método que se encarga de eliminar todos los cupos de un centro
	 * @param nombreCentro el nombre del centro del que se quieren eliminar los cupos
	 * @throws CupoNotFoundException excepción que se lanzará en caso de que el cupo que se quiere eliminar ya no exista
	 * @throws CentroNotFoundException excepción que se lanzará en caso de que el centro no exista
	 */
	public void deleteAllCuposByCentro(String nombreCentro) throws CupoNotFoundException, CentroNotFoundException {
				
		List <Cupo> cupos = cupoRepository.findCuposWithCentro(centroDao.buscarCentroByNombre(nombreCentro).getNombre());
		
		for (Cupo cupo : cupos) {
			deleteCupo(cupo);
		}
		
	}
}
