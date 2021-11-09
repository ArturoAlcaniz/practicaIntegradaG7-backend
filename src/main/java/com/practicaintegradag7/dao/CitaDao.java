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
import com.practicaintegradag7.model.Centro;
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
	
	public List<Cita> createCitas() throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CentroNotFoundException {
		Usuario usuario = findUsuarioAvailable();
		List<Cita> citasUsuario = citaRepository.findByEmail(usuario.getEmail());
		List<Cita> citas = new ArrayList<>();

		Centro centro = centroDao.buscarCentroByNombre(usuario.getCentro().getNombre());
		
		if(citasUsuario.size()>1) {
			throw new CitasUsuarioNotAvailable();
		}
		
		if(citasUsuario.size() == 1) {
			LocalDateTime fecha = findFechaAvailableAfter(centro, citasUsuario.get(0).getFecha());
			Cita cita = new Cita(usuario.getDni(), fecha, usuario.getCentro().getNombre(), (short) 2);
			citas.add(citaRepository.save(cita));
			return citas;
		}
		
		LocalDateTime fecha1 = findFechaAvailable(centro);
		LocalDateTime fecha2 = findFechaAvailableAfter(centro, fecha1.plusDays(21));
		String centroNombre = usuario.getCentro().getNombre();
		Cita cita1 = new Cita(usuario.getDni(), fecha1, centroNombre, (short) 1);
		Cita cita2 = new Cita(usuario.getDni(), fecha2, centroNombre, (short) 2);
		citaRepository.save(cita1);
		citaRepository.save(cita2);
		citas.add(cita1);
		citas.add(cita2);
		return citas;
	}
	
	private LocalDateTime findFechaAvailableAfter(Centro centro, LocalDateTime fecha) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailableAfter(centro, fecha);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
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
	
	private LocalDateTime findFechaAvailable(Centro centro) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailable(centro);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
	}
	
	public void deleteCita(Cita cita) {
		citaRepository.deleteByEmail(cita.getEmail());
	}
}
