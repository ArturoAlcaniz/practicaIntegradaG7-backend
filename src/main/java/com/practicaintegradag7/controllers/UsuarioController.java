package com.practicaintegradag7.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.validate.UsuarioValidator;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class UsuarioController {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private UsuarioValidator usuarioValidator;
	
	@ResponseBody
    @PostMapping("/usuarios/create")
    public void crearUsuario(@RequestParam String dni, @RequestParam String nombre, @RequestParam String apellidos
    		, @RequestParam String password, @RequestParam Centro centro, @RequestParam String rol) {
		Usuario usuario = new Usuario(dni, nombre, apellidos, apellidos, password, centro, rol);
    	usuarioValidator.createUsuarioValidation(usuario);
    	usuarioDao.createUsuario(usuario);
    }
}