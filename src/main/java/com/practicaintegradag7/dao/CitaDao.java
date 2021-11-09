package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CitaRepository;

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
	
	public List<Cita> createCitas() throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CupoNotFoundException, CentroNotFoundException {
		Usuario usuario = findUsuarioAvailable();
		String dni = usuario.getDni();
		LocalDateTime fecha1 = findFechaAvailable(usuario.getCentro().getNombre());
		LocalDateTime fecha2 = fecha1.plusDays(21);
		String centroNombre = usuario.getCentro().getNombre();
		Cita cita1 = new Cita(dni, fecha1, centroNombre, (short) 1);
		checkViability(cita1);
		Cita cita2 = new Cita(dni, fecha2, centroNombre, (short) 2);
		checkViability(cita2);
		citaRepository.save(cita1);
		citaRepository.save(cita2);
		List<Cita> citas = new ArrayList<>();
		citas.add(cita1);
		citas.add(cita2);
		return citas;
	}
	private Cita checkViability(Cita cita) throws CitasCupoNotAvailable, CupoNotFoundException, CentroNotFoundException {
		List<Cita> present = citaRepository.findByFechaAndCentroNombre(cita.getFecha(), cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centroDao.buscarCentroByNombre(cita.getCentroNombre()));
		if(present.size() == cupo.getNumeroCitas()) {
			cita.setFecha(findFechaAvailableAfterGiven(cita.getCentroNombre(), cita.getFecha()));
		}
		return cita;
	}
	
	public List<Cita> getCitasByEmail(String email) {
		return citaRepository.findByEmail(email);
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	
	private Usuario findUsuarioAvailable() throws CitasUsuarioNotAvailable {
		List<Cita> citas = getAllCitas();
		Optional<Usuario> d = usuarioDao.getAllUsuarios().stream().filter(usuario -> 
			citas.stream().filter(cita -> usuario.getEmail().equals(cita.getEmail())).count()<=2).findFirst();
		if(!d.isPresent()) {
			throw new CitasUsuarioNotAvailable();
		}
		return d.get();	
	}
	
	private LocalDateTime findFechaAvailable(String centro) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCupos();
		Optional<Cupo> c = cupos.stream().filter(cupo ->
			cupo.getCentro().getNombre().equals(centro)).findFirst();
		if(!c.isPresent()) {
			throw new CitasCupoNotAvailable();
		}
		return c.get().getFechaInicio();
	}
	
	private LocalDateTime findFechaAvailableAfterGiven(String centro, LocalDateTime time) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCupos();
		Optional<Cupo> c = cupos.stream().filter(cupo ->
			cupo.getCentro().getNombre().equals(centro) && cupo.getFechaInicio().isAfter(time)).findFirst();
		if(!c.isPresent()) {
			throw new CitasCupoNotAvailable();
		}
		return c.get().getFechaInicio();
	}
	
	public void deleteCita(Cita cita) {
		citaRepository.deleteByEmail(cita.getEmail());
	}
}
