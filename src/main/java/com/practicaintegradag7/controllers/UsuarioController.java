package com.practicaintegradag7.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.practicaintegradag7.exceptions.UserModificationException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
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
	
	private static final String EMAIL = "email";
	private static final String PWD = "password";
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	
	@PostMapping(path="api/usuario/create")
	public String crearUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		
		JSONObject jso = new JSONObject(datosUsuario);
		String rol = jso.getString("rol");
		rol = rol.substring(0,1).toUpperCase() + rol.substring(1);
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString("nombre"))
				.apellidos(jso.getString("apellidos"))
				.email(jso.getString(EMAIL))
				.password(jso.getString(PWD))
				.centro(dao.buscarCentroByNombre(jso.getString("centro")))
				.rol(rol)
				.build();
		user.saveUsuario(useri);
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario con DNI\" + dni + \" creado correctamente.");
    	return response.toString();
	}
	
	@PostMapping(path="api/usuario/modify")
	public String modificarUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, UserModificationException, CifradoContrasenaException {
		
		JSONObject jso = new JSONObject(datosUsuario);
		String rol = jso.getString("rol");
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString("nombre"))
				.apellidos(jso.getString("apellidos"))
				.email(jso.getString("emailog"))
				.password(jso.getString(PWD))
				.centro(dao.buscarCentroByNombre(jso.getString("centro")))
				.rol(rol)
				.build();
		user.modifyUsuario(useri);
		JSONObject response = new JSONObject();
		System.out.println(useri.getNombre());
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario modificado correctamente.");
    	return response.toString();
	}
	
	@GetMapping(path="api/usuarios/obtener")
	public List<Usuario> obtenerUsuario() throws CifradoContrasenaException{
		return user.getAllUsuarios();
	}
	
	@PostMapping(path="api/usuario/login")
	public String login(HttpServletRequest request, @RequestBody Map<String, Object> info) throws UsuarioNotFoundException, JSONException {
		JSONObject jso = new JSONObject(info);
		String email = jso.optString(EMAIL);
		String password = DigestUtils.sha256Hex(jso.optString("password"));
		
		Usuario usuario = user.getUsuarioByEmail(email);
		
		if (usuario==null || !email.equals(usuario.getEmail()) || !password.equals(usuario.getPassword())) {
			throw new UsuarioNotFoundException("No existe un usuario con ese email y password");
		}
		request.getSession().setAttribute(EMAIL, email);
		request.getSession().setAttribute("rol", usuario.getRol());
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario ha iniciado la sesión correctamente.");
    	return response.toString();
	}
	
}
