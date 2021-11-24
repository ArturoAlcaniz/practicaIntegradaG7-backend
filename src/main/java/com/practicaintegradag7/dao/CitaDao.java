package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasNotAvailableException;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.VacunacionDateException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CitaRepository;

/**
 * Se encarga de la lógica de las citas de vacunación
 * Su funcionalidad será realizar la creación de las citas
 */
@Service
public class CitaDao {
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private CupoDao cupoDao;
	
	@Autowired
	private CentroDao centroDao;
	
	/**
	 * Método que se encarga de crear citas
	 * @param usuario al que se le asignará/relacionará las citas
	 * @return una lista con las dos citas de vacunación
	 * @throws CitasUsuarioNotAvailable excepción que se lanzará en el caso de que el usuario tenga el máximo de citas.
	 * @throws CitasCupoNotAvailable excepción que se lanzará en el caso de que no haya cupos disponibles.
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no encuentre el centro.
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no encuentre cupos disponibles.
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que no encuentre citas asociadas a ese usuario.
	 */
	
	public List<Cita> createCitas(Usuario u) throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CentroNotFoundException, CupoNotFoundException, CitaNotFoundException {
		List<Cita> citasUsuario = getCitasByEmail(u.getEmail());
		List<Cita> citas = new ArrayList<>();

		Centro centro = centroDao.buscarCentroByNombre(u.getCentro());
		
		if(citasUsuario.size()>1) throw new CitasUsuarioNotAvailable();
		
		if(citasUsuario.size() == 1) {
			LocalDateTime fecha = findFechaAvailableAfter(centro, citasUsuario.get(0).getFecha().plusDays(21));
			Cita cita = new Cita(u.getEmail(), fecha, u.getCentro(), (short) 2);
			citas.add(citaRepository.save(cita));
			
			Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(fecha, centro.getNombre());
			restarCitaCupo(cupo);
		} else {
			LocalDateTime fecha1 = findFechaAvailable(centro);
			LocalDateTime fecha2 = findFechaAvailableAfter(centro, fecha1.plusDays(21));
			Cupo cupo1 = cupoDao.getCupoByInicialDateAndCentro(fecha1, centro.getNombre());
			Cupo cupo2 = cupoDao.getCupoByInicialDateAndCentro(fecha2, centro.getNombre());
			String centroNombre = u.getCentro();
			Cita cita1 = new Cita(u.getEmail(), fecha1, centroNombre, (short) 1);
			Cita cita2 = new Cita(u.getEmail(), fecha2, centroNombre, (short) 2);
			citaRepository.save(cita1);
			citaRepository.save(cita2);
			citas.add(cita1);
			citas.add(cita2);
			restarCitaCupo(cupo1);
			restarCitaCupo(cupo2);
		}
		return citas;
	}
	/**
	 * Método encargado de buscar una fecha disponible después de la fecha dada, dado un centro y una fecha
	 * @param centro en el cual se buscarán fechas disponibles de vacunación
	 * @param fecha de la primera vacuna
	 * @return fecha disponible
	 * @throws CitasCupoNotAvailable excepción que se lanzará en el caso de que no haya cupos disponibles.
	 */
	private LocalDateTime findFechaAvailableAfter(Centro centro, LocalDateTime fecha) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailableAfter(centro, fecha);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
	}
	
	/**
	 * Método que te devuelven las citas relacionadas con el email del usuario
	 * @param email del usuario, al que se le devolverán las citas
	 * @return si el email tiene citas, se devolverá una lista con todas las citas, si no tiene citas se lanzará una excepción
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que el usuario no tenga citas.
	 */
	public List<Cita> getCitasByEmail(String email) throws CitaNotFoundException {
		Optional<List<Cita>> citas = citaRepository.findByEmail(email);
		if (citas.isPresent()) return citas.get();
		else throw new CitaNotFoundException("Este usuario no tiene citas");
	}
	
	/**
	 * Método en el que se devuelven todas las citas de la base de datos
	 * @return devuelve una lista con todas las citas que haya en la base de datos
	 */
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	/**
	 * Método en el que te devuelve la primera fecha disponible
	 * @param centro en el cual se buscará la fecha
	 * @return devuelve la fecha para la vacuna
	 * @throws CitasCupoNotAvailable excepción que se lanzará en el caso de que no haya cupos disponibles.
	 */
	private LocalDateTime findFechaAvailable(Centro centro) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailable(centro);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
	}
	
	/**
	 * Método para crear la cita en la base de datos
	 * @param se le pasa la cita que se desea guardar en la base de datos.
	 */
	public void createCita (Cita cita) {
		citaRepository.save(cita);
	}
	/**
	 * Método para eliminar la cita de la base de datos
	 * @param cita, se le pasará la cita que se quiera borrar de la base de datos.
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no encuentre el centro.
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentren cupos.
	 * @throws CupoExistException excepción que se lanzará en el caso de que el cupo ya exista.
	 */
	public void deleteCita (Cita cita) throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
		
		if (cita.getNcita() == 1) {
			Optional<Cita> opt = citaRepository.findByEmailAndNcita(cita.getEmail(),Short.valueOf("2"));
			if (opt.isPresent()) {
				Cita citaSegunda = opt.get();
				Cita citaSegundaPasaPrimera = new Cita(citaSegunda.getEmail(), citaSegunda.getFecha(), citaSegunda.getCentroNombre(), Short.valueOf("1"));
				deleteCita(citaSegunda);
				saveCita(citaSegundaPasaPrimera);
			}
		}
		
		sumarCitaCupo(cupo);
		citaRepository.deleteByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	/**
	 * Método para eliminar la cita que ya se ha usado, por ejemplo si tienes las dos citas para ambas dosis, cuando ya has usado la primera, se querrá eliminar.
	 * @param cita que se quiere eliminar
	 */
	public void deleteCitaUsada (Cita cita) {
		citaRepository.deleteByEmailAndFechaAndNcita(cita.getEmail(), cita.getFecha(), cita.getNcita());
	}
	
	/**
	 * Método para guardar la cita en la base de datos
	 * @param cita que se guardará en la tabla de la base de datos
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no encuentre el centro.
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentren los cupones.
	 */
	public void saveCita(Cita cita) throws CentroNotFoundException, CupoNotFoundException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
		citaRepository.save(cita);
		restarCitaCupo(cupo);
	}
	
	/**
	 * Método para eliminar la cita
	 * @param cita la cual quieres eliminar
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentre el cupo disponible
	 */
	public void deleteCitaModificar(Cita cita) throws CentroNotFoundException, CupoNotFoundException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
		sumarCitaCupo(cupo);
		citaRepository.deleteByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	/**
	 * Método para encontrar una cita por fecha e email
	 * @param cita la cual quieres encontrar
	 * @return cita que te devuelve la base de datos
	 */
	public Cita findCitaByEmailAndFecha(Cita cita) {
		return citaRepository.findByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	/**
	 * Método para encontrar las citas dado un email
	 * @param email
	 * @param ncita
	 * @return cita encontrada en la base de datos
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que no se encuentren citas con ese email
	 */
	public Cita findByEmailAndNcita(String email, Short ncita) throws CitaNotFoundException {
		Optional<Cita> opt = citaRepository.findByEmailAndNcita(email, ncita);
		if(opt.isPresent()) return opt.get();
		else throw new CitaNotFoundException("La cita con email "+email+" y ncita "+ncita+" no existe");
		
	}

	/**
	 * Método para modificar las citas de la base de datos, si la modificación que quieres hacer es válida se realizará la modificación.
	 * @param citaAntigua, datos de la cita que quieres modificar
	 * @param citaNueva, datos modificados de la nueva cita
	 * @return boolean true o false, para saber si se ha modificado o no la cita
	 * @throws CitaNotModifiedException excepción que se lanzará en el caso de que no se pueda modificar la cita
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentre el cupo.
	 */
	public boolean modifyCita(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException {
		
		boolean modified = false;
		
		if (validarModificacion(citaAntigua, citaNueva)) {
		
			Centro centro = centroDao.buscarCentroByNombre(citaAntigua.getCentroNombre());
			Cupo cupoAntiguo = cupoDao.getCupoByInicialDateAndCentro(citaAntigua.getFecha(), centro.getNombre());
			Cupo cupoNuevo = cupoDao.getCupoByInicialDateAndCentro(citaNueva.getFecha(), centro.getNombre());
			
			sumarCitaCupo(cupoAntiguo);
			restarCitaCupo(cupoNuevo);
			
			deleteCitaModificar(citaAntigua);
			createCita(citaNueva);
			
			modified = true;
		
		}
		
		return modified;
	}
	
	/**
	 * Método para eliminar todas las citas de la base de datos
	 */
	public void deleteAllCitas() {
		citaRepository.deleteAll();
	}
	
	/**
	 * Método para sumar citas a un cupo (franja horaria)
	 * @param cupoAntiguo se le pasa el cupo al que se le quiera sumar las citas
	 * @throws CupoNotFoundException excepción que se lanzará cuando no se encuentre el cupo
	 */
	public void sumarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException {
		cupoAntiguo.setCitas(cupoAntiguo.getNumeroCitas()+1);
		cupoDao.updateCupo(cupoAntiguo);
	}
	
	/**
	 * Método para restar citas a un cupo (franja horaria)
	 * @param cupoAntiguo se le pasará el cupo al cual se le quiera quitar citas
	 * @throws CupoNotFoundException excepción que se lanzará cuando no se encuentre el cupo
	 */
	public void restarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException {
		cupoAntiguo.setCitas(cupoAntiguo.getNumeroCitas()-1);
		cupoDao.updateCupo(cupoAntiguo);
	}
	
	/**
	 * Método para validar la modificación, dependerá de la cita que se quiera modificar, si es la primera la única restricción es que no sea después de la 2 cita para la siguiente dosis
	 * y si es para la modificación de la segunda cita la única restricción es que no puede ponerse hasta que pasen como mínimo 21 días desde la primera vacuna.
	 * @param citaAntigua, la cual quieres modificar
	 * @param citaNueva, la cual tiene los nuevos datos para la modificación
	 * @return devuelve un boolean para saber si se ha podido validar.
	 * @throws CitaNotModifiedException excepción que se lanzará en el caso de que no se pueda modificar la cita
	 */
	public boolean validarModificacion(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException {
		
		if (citaAntigua.getFecha().equals(citaNueva.getFecha())) {
			throw new CitaNotModifiedException("Debe insertar una fecha distinta a la antigua");
		}
		
		Optional<Cita> opt = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("2"));
		
		if (citaAntigua.getNcita() == 1) {
			if(opt.isPresent() && !citaNueva.getFecha().isBefore(opt.get().getFecha())) {
				throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior a la segunda ("+opt.get().getFecha()+")");
			}
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 10, 23, 59))) {
				throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior al 10-1-2022");
			}
			return true;
		}
		
		Optional<Cita> opt2 = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("1"));

		if(citaAntigua.getNcita()==2){
			if(opt2.isPresent() && !citaNueva.getFecha().isAfter(opt2.get().getFecha().plusDays(21))) {
				throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser anterior a "
						+ "21 dias despues de la primera ("+opt2.get().getFecha()+")");
			}
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 31, 23, 59))) {
				throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser posterior al 31-1-2022");
			}
		}
			
		return true;
	}
	
	/**
	 * Método para vacunar a un usuario
	 * @param cita, se le pasará la cita del usuario a vacunar
	 * @throws VacunacionDateException excepción que se lanzará en el caso de que no coincida la fecha de la cita con la fecha actual
	 * @throws UsuarioNotFoundException excepción que se lanzará en el caso de que no se encuentre el usuario el la base de datos
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro
	 * @throws CitasNotAvailableException excepción que se lanzará en el caso de que no haya citas disponibles.
	 */
	public void vacunar(Cita cita) throws VacunacionDateException, UsuarioNotFoundException, CentroNotFoundException, CitasNotAvailableException {
		LocalDateTime fechaActual = LocalDateTime.now();
		if(fechaActual.getYear() != cita.getFecha().getYear() || fechaActual.getMonth() != cita.getFecha().getMonth()
			|| fechaActual.getDayOfMonth() != cita.getFecha().getDayOfMonth()) {
			throw new VacunacionDateException();
		}
		Usuario usuarioVacunar = usuarioDao.getUsuarioByEmail(cita.getEmail());
		Centro centro = centroDao.buscarCentroByNombre(usuarioVacunar.getCentro());
		if(centro.getVacunas() == 0) {
			throw new CitasNotAvailableException();
		}
		
		if(cita.getNcita() == 1) {
			usuarioVacunar.setPrimeraDosis(true);
		} else {
			usuarioVacunar.setSegundaDosis(true);
		}
		usuarioDao.save(usuarioVacunar);
		
	}
	
	/**
	 * Método para buscar las citas por fecha y nombre
	 * @param fechaMin, la fecha inicial (desde)
	 * @param fechaMax, la fecha final (hasta)
	 * @param centro, donde se buscaran las citas en ese intervalo de fechas
	 * @return lista con todas las citas de ese intervalo de tiempo.
	 */
	public List<Cita> findByFechaAndCentroNombre (LocalDateTime fechaMin, LocalDateTime fechaMax, String centro) {
		return citaRepository.findByFechaAndCentroNombre(fechaMin,fechaMax, centro);
	}
}
