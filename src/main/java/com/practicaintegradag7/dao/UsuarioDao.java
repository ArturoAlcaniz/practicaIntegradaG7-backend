package com.practicaintegradag7.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@Service
public class UsuarioDao {
	
	@Autowired
	public UsuarioRepository usuarioRepository;
	
	public void createUsuario(Usuario usuario) {
		usuarioRepository.insert(usuario);
	}
	
	public Usuario getUsuarioByDni(String dni) {
		return usuarioRepository.findByDni(dni);
	}
	
	public List<Usuario> getAllUsuarios() {
		return usuarioRepository.findAll();
	}

}
