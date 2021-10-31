package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.dao.CentroDao;
import java.util.List;

import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class UsuarioController {
	
	@Autowired
	private UsuarioDao user;

	@Autowired
	private CentroDao dao;
	
	@PostMapping(path="api/usuario/create")
	public String crearUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		JSONObject jso = new JSONObject(datosUsuario);
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString("nombre"))
				.apellidos(jso.getString("apellidos"))
				.email(jso.getString("email"))
				.password(jso.getString("password"))
				.centro(dao.buscarCentroByNombre(jso.getString("centro")))
				.rol(jso.getString("rol"))
				.build();
		user.saveUsuario(useri);
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Usuario con DNI\" + dni + \" creado correctamente.");
    	return response.toString();
	}
	
	@GetMapping(path="api/usuarios/obtener")
	public List<Usuario> obtenerUsuario(){
		return user.getAllUsuarios();
	}
}
