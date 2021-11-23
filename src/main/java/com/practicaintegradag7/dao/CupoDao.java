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

@Service
public class CupoDao {

	@Autowired
	private CupoRepository cupoRepository;
	@Autowired
	private CentroRepository centroRepository;
	@Autowired
	private CentroDao centroDao;
	
	public Cupo saveCupo(Cupo cupo) throws CentroNotFoundException, CupoExistException {
		Optional<Centro> opt = centroRepository.findByNombre(cupo.getCentro());
		if (opt.isPresent()) {
			Optional<Cupo> cupoExistente = cupoRepository.findByFechaInicioAndCentro(cupo.getFechaInicio(), cupo.getCentro());
			if (!cupoExistente.isPresent()) {
				return cupoRepository.save(cupo);
			} else throw new CupoExistException("El cupo que intentas crear ya existe");
		} else throw new CentroNotFoundException("El centro "+cupo.getCentro()+"no existe.");
	}
	
	public Cupo getCupoById (String id) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+id+" no existe!");
	}
	
	public Cupo getCupoByInicialDateAndCentro (LocalDateTime fechaInicio, String centro) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findByFechaInicioAndCentro(fechaInicio, centro);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo del centro "+centro+" con fecha "+fechaInicio+" no existe!!");
	}
	
	public List<Cupo> getAllCupos() {
		return cupoRepository.findAll();
	}
	
	public List<Cupo> getAllCuposByCentro(Centro centro) {
		return cupoRepository.findCuposWithCentro(centro.getNombre());
	}
	
	public Cupo updateCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) return cupoRepository.save(cupo);
		else throw new CupoNotFoundException("Cupo para modificar no encontrado");
	}
	
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
	
	public void autogenerarFranjas(Configuration configuracion) {
		cupoRepository.deleteAll();
		LocalDateTime fechaInicial = LocalDateTime.now().withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
		LocalDateTime fechaMax = LocalDateTime.parse("2022-01-31T23:59");
		int dif = configuracion.getHoraFin().toSecondOfDay() - configuracion.getHoraInicio().toSecondOfDay();
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
	
	public void autogenerarFranjaParaCentro(Configuration configuracion, Centro centro) {
		cupoRepository.deleteAll();
		LocalDateTime fechaInicial = LocalDateTime.now().withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
		LocalDateTime fechaMax = LocalDateTime.parse("2022-01-31T23:59");
		int dif = configuracion.getHoraFin().toSecondOfDay() - configuracion.getHoraInicio().toSecondOfDay();
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
	
	public void deleteAllCupos() {
		cupoRepository.deleteAll();
	}
	
	public List<Cupo> getAllCuposAvailable(Centro centro) {
		return cupoRepository.findCuposWithNcitasMoreThan(0, centro.getNombre());
	}

	public List<Cupo> getAllCuposAvailableAfter(Centro centro, LocalDateTime fechaMinima) {
		return cupoRepository.findCuposWithCitasMoreThan(0, centro.getNombre(), fechaMinima);
	}
	
	public List<Cupo> getAllCuposAvailableInADay(Centro centro, LocalDateTime fecha) {
		return cupoRepository.findCuposWithCitasMoreThanAndFechaInicioGreaterThanEqualAndFechaInicioLessThan(0, centro.getNombre(), fecha, fecha.plusDays(1));
	}

	public void deleteAllCuposByCentro(String nombreCentro) throws CupoNotFoundException, CentroNotFoundException {
				
		List <Cupo> cupos = cupoRepository.findCuposWithCentro(centroDao.buscarCentroByNombre(nombreCentro).getNombre());
		
		for (Cupo cupo : cupos) {
			deleteCupo(cupo);
		}
		
	}
}
