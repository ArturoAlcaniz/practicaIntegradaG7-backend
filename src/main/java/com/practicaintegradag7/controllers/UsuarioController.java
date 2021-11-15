package com.practicaintegradag7.controllers;

import java.util.Map;
import java.util.stream.Collectors;

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
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CitaDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.LDTFormatter;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class UsuarioController {
	
	@Autowired
	private UsuarioDao usuarioDao;

	@Autowired
	private CentroDao centroDao;
	
	@Autowired
	private CitaDao citaDao;
	
	private static final String EMAIL = "email";
	private static final String PWD = "password";
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	private static final String CENTRO = "centro";
	
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
				.centro(centroDao.buscarCentroByNombre(jso.getString(CENTRO)).getNombre())
				.rol(rol)
				.build();
		usuarioDao.saveUsuario(useri);
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario con DNI\" + dni + \" creado correctamente.");
    	return response.toString();
	}
	
	@PostMapping(path="api/usuario/modify")
	public String modificarUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		
		JSONObject jso = new JSONObject(datosUsuario);
		String rol = jso.getString("rol");
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString("nombre"))
				.apellidos(jso.getString("apellidos"))
				.email(jso.getString(EMAIL))
				.password(jso.getString(PWD))
				.centro(centroDao.buscarCentroByNombre(jso.getString(CENTRO)).getNombre())
				.rol(rol)
				.build();
		try {
			usuarioDao.modifyUsuario(useri);
			JSONObject response = new JSONObject();
			response.put(STATUS, "200");
			response.put(MSSG, "Usuario modificado correctamente.");
			return response.toString();
		}catch(Exception ex) {
			JSONObject response = new JSONObject();
			response.put(STATUS, "500");
			response.put(MSSG, ex.getMessage());
			return response.toString();
		}
	}
	
	@GetMapping(path="api/usuarios/obtener")
	public List<Usuario> obtenerUsuario() throws CifradoContrasenaException{
		return usuarioDao.getAllUsuarios();
	}
	
	@PostMapping(path="api/usuario/login")
	public String login(HttpServletRequest request, @RequestBody Map<String, Object> info) throws UsuarioNotFoundException, JSONException {
		JSONObject jso = new JSONObject(info);
		String email = jso.optString(EMAIL);
		String password = DigestUtils.sha256Hex(jso.optString(PWD));
		
		Usuario usuario = usuarioDao.getUsuarioByEmail(email);
		
		if (usuario==null || !email.equals(usuario.getEmail()) || !password.equals(usuario.getPassword())) {
			throw new UsuarioNotFoundException("No existe un usuario con ese email y password");
		}
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario ha iniciado la sesión correctamente.");
		response.put(EMAIL, usuario.getEmail());
		response.put(PWD, usuario.getPassword());
		response.put(CENTRO, usuario.getCentro());
    	return response.toString();
	}
	
	@PostMapping(path="api/usuario/eliminar")
	public String eliminarUsuario(@RequestBody Map<String, Object> emailJSON) throws JSONException, CitaNotFoundException, CentroNotFoundException, CupoNotFoundException, CupoExistException{
		JSONObject jso = new JSONObject(emailJSON);
		String emailUsuario =  jso.getString(EMAIL);
		
		usuarioDao.deleteUsuarioAndCitasByEmail(emailUsuario);
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Ha eliminado correctamente el usuario con email "+emailUsuario);
    	return response.toString();
	}
	
	@GetMapping(path="/api/usuario/obtenerPorFechaAndCentro")
	public List<Usuario> obtenerCitasPorFechaAndCentro(@RequestBody Map<String, Object> info) throws JSONException, CifradoContrasenaException{
		JSONObject jso = new JSONObject(info);
		String fechaString = jso.getString("fecha");
		String centro = jso.getString("centro");
		LocalDateTime fechaMin = LDTFormatter.parse(fechaString+"T00:00");
		LocalDateTime fechaMax = LDTFormatter.parse(fechaString+"T23:59");
		
		List<Cita> citas = citaDao.findByFechaAndCentroNombre(fechaMin,fechaMax, centro);
		List<String> emails = citas.stream().map(Cita::getEmail).collect(Collectors.toList());
		
		for (String e : emails) System.out.println(e);
		
		List<Usuario> usuarios = usuarioDao.getAllByEmail(emails);
		
		System.out.println("Buscamos los usuarios");
		for (Usuario u : usuarios) System.out.println(u.getEmail()+" : "+u.getNombre());
		return usuarios;
	}
	
}
