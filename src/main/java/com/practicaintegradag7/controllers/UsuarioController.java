package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.dao.CentroDao;
import java.util.List;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class UsuarioController {
	
	@Autowired
	private UsuarioDao user;

	@Autowired
	private CentroDao dao;
	
	@PostMapping(path="api/usuario/create")
	public Usuario crearUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		JSONObject jso = new JSONObject(datosUsuario);
		String dni = jso.getString("dni");
		String nombre = jso.getString("nombre");
		String apellidos = jso.getString("apellidos");
		String email = jso.getString("email");
		String password = jso.getString("password");
		Centro centro = dao.buscarCentroByNombre(jso.getString("centro"));
		String rol = jso.getString("rol");
		Usuario useri= new Usuario(dni, nombre, apellidos, email, password, centro, rol);
		return user.saveUsuario(useri);
	}
	
	@GetMapping(path="api/usuarios/obtener")
	public List<Usuario> obtenerUsuario(){
		try {
			return user.getAllUsuarios();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
}
