package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CitaRepository;

@Service
public class CitaDao {
	
	@Autowired
	public CitaRepository citaRepository;
	
	@Autowired
	public UsuarioDao usuarioDao;
	
	@Autowired
	public CupoDao cupoDao;
	
	public Cita createCita() throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CifradoContrasenaException {
		Usuario usuario = findUsuarioAvailable();
		//usuario.decryptDNI();
		String dni = usuario.getDni();
		LocalDateTime fecha = findFechaAvailable(usuario.getCentro().getNombre());
		String centroNombre = usuario.getCentro().getNombre();
		Cita cita = new Cita(dni, fecha, centroNombre);
		return citaRepository.save(cita);
	}
	
	public List<Cita> getCitasByDni(String dni) {
		return citaRepository.findByDni(dni);
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	
	private Usuario findUsuarioAvailable() throws CitasUsuarioNotAvailable {
		List<Cita> citas = getAllCitas();
		Optional<Usuario> d = usuarioDao.getAllUsuarios().stream().filter(usuario -> 
			citas.stream().filter(cita -> usuario.getDni().equals(cita.getDni())).count()<2).findFirst();
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
	
	public void deleteCita(Cita cita) {
		citaRepository.deleteByDni(cita.getDni());
	}
}
