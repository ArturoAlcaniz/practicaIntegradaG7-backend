package com.practicaintegradag7.dao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@Service
public class UsuarioDao {
	
	@Autowired
	public UsuarioRepository usuarioRepository;
	
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
	
	public Usuario getUsuarioByEmail(String email) {
		return usuarioRepository.findByEmail(email); 
	}
	
	public List<Usuario> getAllUsuarios() {
		return usuarioRepository.findAll();
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

}
