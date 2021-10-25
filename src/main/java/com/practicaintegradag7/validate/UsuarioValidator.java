package com.practicaintegradag7.validate;


import javax.ws.rs.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.model.Usuario;

@Service
public class UsuarioValidator {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	public void createUsuarioValidation(Usuario usuario) throws BadRequestException {
		
		if(usuarioDao.getUsuarioByDni(usuario.getDni()) != null) {
			throw new BadRequestException("Ya existe un usuario con este dni");
		}
				
	}
	
}