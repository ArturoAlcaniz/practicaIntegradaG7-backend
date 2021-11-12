package com.practicaintegradag7.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@Service
public class UsuarioDao {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CitaDao citaDao;
	
	public Usuario saveUsuario(Usuario usuario) throws CifradoContrasenaException {
		
		if (!validatePasswordPolicy(usuario.getPassword())) {
			throw new IllegalArgumentException("Password is not valid!");
		}
		if(!validateDNI(usuario.getDni())) {
			throw new IllegalArgumentException("Dni is not valid!");
		}
		usuario.encryptDNI();
		if (usuarioRepository.existsByEmail(usuario.getEmail()))
			return null;
		else
		{
			usuario.hashPassword();
			return usuarioRepository.save(usuario);
		}
	}
	public void deleteAllUsuarios() {
		usuarioRepository.deleteAll();
	}
	
	public Usuario getUsuarioByEmail(String email) throws UsuarioNotFoundException {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		if (usuario.isPresent()) {
			return usuario.get();
		}else throw new UsuarioNotFoundException("El usuario con email "+email+" no existe");
		
	}

	
	public List<Usuario> getAllUsuarios() {
		return usuarioRepository.findAll();
	}
	
	public List <Usuario> getAllUsuariosByCentro(String nombreCentro){
		List <Usuario> usuarios = usuarioRepository.findAll();
		List <Usuario> usuariosCentro = new ArrayList<>();
		
		for (Usuario usuario : usuarios) {
			if (usuario.getCentro().getNombre().equals(nombreCentro))
				usuariosCentro.add(usuario);
		}
		
		return usuariosCentro;
		
	}
	
	public void deleteUsuarioByEmail(String email) {
		usuarioRepository.deleteByEmail(email);
	}
	
	public void deleteUsuarioAndCitasByEmail(String email) throws CitaNotFoundException, UsuarioNotFoundException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		
		List <Cita> citas = citaDao.getCitasByEmail(email);
		for (Cita cita : citas) {
			citaDao.deleteCitaModificar(cita);
		}
		if (citaDao.getCitasByEmail(email).isEmpty())
			usuarioRepository.deleteByEmail(email);
	}
	
	private boolean validatePasswordPolicy(String password) {
		Pattern regexPassword = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.-])(?=\\S+$).{8,}");
		Matcher comparePassword = regexPassword.matcher(password);
		return comparePassword.matches();
	}

    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
    
    

}
