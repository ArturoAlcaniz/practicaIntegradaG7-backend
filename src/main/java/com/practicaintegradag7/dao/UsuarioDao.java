package com.practicaintegradag7.dao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.UserModificationException;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@Service
public class UsuarioDao {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
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
	
	public Usuario getUsuarioByEmail(String email) {
		return usuarioRepository.findByEmail(email); 
	}
	
	public List<Usuario> getAllUsuarios() throws CifradoContrasenaException {
		List<Usuario> aux = usuarioRepository.findAll();
		for(Usuario u : aux) u.decryptDNI();
		return aux;
	}
	
	public void deleteUsuarioByEmail(String email) {
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
    
	public void modifyUsuario(Usuario newUser) throws UserModificationException, CifradoContrasenaException {
		Usuario old = usuarioRepository.findByEmail(newUser.getEmail());
		
		if(newUser.getNombre().equals("")) throw new UserModificationException("Nombre no puede ser nulo");
		if(newUser.getApellidos().equals("")) throw new UserModificationException("Apellidos no puede ser nulo");
		if(newUser.getDni().equals("")) throw new UserModificationException("DNI no puede ser nulo");
		
		if(newUser.getPassword().equals("")) newUser.setPassword(old.getPassword());
		else {
			if(validatePasswordPolicy(newUser.getPassword())) newUser.hashPassword();
			else throw new IllegalArgumentException("Password is not valid!");
		}
		
		//Atributos no modificables en la interfaz, aparte del email
		newUser.setRol(old.getRol());
		newUser.setPrimeraDosis(old.isPrimeraDosis());
		newUser.setSegundaDosis(old.isSegundaDosis());
		
		if(!newUser.getCentro().equals(old.getCentro()) && newUser.isPrimeraDosis()) throw new UserModificationException("Un usuario ya vacunado no puede cambiar de centro");
		newUser.encryptDNI();
		usuarioRepository.deleteByEmail(old.getEmail());
		usuarioRepository.save(newUser);
	}
	
}
