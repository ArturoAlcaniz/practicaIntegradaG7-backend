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
		Optional<Centro> opt = centroRepository.findByNombre(cupo.getCentro().getNombre());
		if (opt.isPresent()) {
			Optional<Cupo> cupoExistente = cupoRepository.findByFechaInicioAndCentro(cupo.getFechaInicio(), cupo.getCentro());
			if (!cupoExistente.isPresent()) {
				return cupoRepository.save(cupo);
			} else throw new CupoExistException("El cupo que intentas crear ya existe");
		} else throw new CentroNotFoundException("El centro "+cupo.getCentro().getNombre()+"no existe.");
	}
	
	public Cupo getCupoById (String id) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+id+" no existe!");
	}
	
	public Cupo getCupoByInicialDateAndCentro (LocalDateTime fechaInicio, Centro centro) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findByFechaInicioAndCentro(fechaInicio, centro);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo del centro "+centro.getNombre()+" con fecha "+fechaInicio+" no existe!!");
	}
	
	public List<Cupo> getAllCupos() {
		return cupoRepository.findAll();
	}
	
	public Cupo updateCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) return cupoRepository.save(cupo);
		else throw new CupoNotFoundException("Cupo para modificar no encontrado");
	}
	
	public void deleteCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) cupoRepository.delete(cupo);
		else throw new CupoNotFoundException("Cupo para borrar no encontrado");
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
				Cupo cupo = new Cupo(fechaInicial, fechaFin, configuracion.getCitasPorFranja(), centros.get(i));
				cupos.add(cupo);
				if(((fechaFin.getHour()*3600)+fechaFin.getMinute()*60)<(configuracion.getHoraFin().getHour()*3600+configuracion.getHoraFin().getMinute()*60)) {
					fechaInicial = fechaFin;
				}else {
					fechaInicial = fechaInicial.withHour(configuracion.getHoraInicio().getHour()).withMinute(configuracion.getHoraInicio().getMinute()).withSecond(0).withNano(0).plusDays(1);
				}
				
			}
			fechaInicial = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);
		}
		cupoRepository.saveAll(cupos);
	}
	
	public void deleteAll() {
		cupoRepository.deleteAll();
	}
	
	public List<Cupo> getAllCuposAvailable(Centro centro) {
		return cupoRepository.findCuposWithCitasMoreThan(0, centro);
	}

	public List<Cupo> getAllCuposAvailableAfter(Centro centro, LocalDateTime fechaMinima) {
		return cupoRepository.findCuposWithCitasMoreThan(0, centro, fechaMinima);
	}
}
