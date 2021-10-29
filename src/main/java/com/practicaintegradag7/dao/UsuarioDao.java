package com.practicaintegradag7.dao;

import java.util.List;

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
		}else {
			usuario.encryptDNI();
			if (usuarioRepository.existsByDni(usuario.getDni()))
				return null;
			else
			{
				return usuarioRepository.save(usuario);
			}
		}
		
	}
	
	public Usuario getUsuarioByDni(String dni) {
		return usuarioRepository.findByDni(dni); 
	}
	
	public List<Usuario> getAllUsuarios() {
		return usuarioRepository.findAll();
	}
	
	public void deleteUsuarioByDni(String dni) {
		usuarioRepository.deleteByDni(dni);
	}
	
	private boolean validatePasswordPolicy(String password) {
		if(password.charAt(0) == 'a') return false;
		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
		return password.matches(pattern);
}

}
