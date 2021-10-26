package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CentroRepository;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class UsuarioController {
	
	public Usuario crearUsuario(@RequestBody Map<String, Object> datosUsuario) {
		JSONObject jso = new JSONObject(datosUsuario);
		String dni = jso.getString("dni");
		String nombre = jso.getString("nombre");
		String apellidos = jso.getString("apellidos");
		String email = jso.getString("email");
		String password = jso.getString("password");
		Centro centro = CentroRepository.findByNombre(jso.getString("centro"));
		String rol = jso.getString("rol");
		Usuario user= new Usuario(dni, nombre, apellidos, email, password, centro, rol);
		return user;
	}

}
