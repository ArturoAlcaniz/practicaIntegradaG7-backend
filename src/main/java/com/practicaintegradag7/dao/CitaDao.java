package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasDniNotValid;
import com.practicaintegradag7.exceptions.CitasLimitException;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CitaRepository;

@Service
public class CitaDao {
	
	@Autowired
	public CitaRepository citaRepository;
	
	@Autowired
	public UsuarioDao usuarioDao;
	
	public String createCita() throws CitasUsuarioNotAvailable, CitasDniNotValid {
		Usuario usuario = findUsuarioAvailable();
		String dni = usuario.getDni();
		String fecha = "2021-10-30 11:30";
		String centroNombre = usuario.getCentro().getNombre();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		Cita cita = new Cita(dni, LocalDateTime.parse(fecha, formatter), centroNombre);
		citaRepository.save(cita);
		return "Ha pedido cita correctamente para el "+fecha;
	}
	
	public List<Cita> getCitasByDni(String dni) {
		return citaRepository.findByDni(dni);
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	
	private void checkCitasLimit(Cita cita) throws CitasLimitException {
		if(getCitasByDni(cita.getDni()).size() > 1) {
			throw new CitasLimitException();
		}
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
	
	private String findFechaAvailable() throws CitasCupoNotAvailable {
		return "";
	}
}
