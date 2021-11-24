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
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioVacunadoException;
import com.practicaintegradag7.dao.CentroDao;
import java.util.List;

import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;

/**
 * 
 * Se encarga de realizar la gestión de los usuarios
 *
 */
@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class UsuarioController {
	
	@Autowired
	private UsuarioDao usuarioDao;

	@Autowired
	private CentroDao centroDao;
	
	private static final String EMAIL = "email";
	private static final String PWD = "password";
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	private static final String CENTRO = "centro";
	private static final String NOMBRE = "nombre";
	
	/**
	 * Método que se encarga de crear un usuario
	 * @param datosUsuario del usuario que se quiere guardar
	 * @return mensaje de respuesta de que ha salido bien
	 * @throws JSONException excepción que se lanza al tratar el json con los datos del usuario
	 * @throws CentroNotFoundException excepción que se lanza si el centro no existe
	 * @throws CifradoContrasenaException excepción que se lanza al cifrar la contraseña
	 */
	@PostMapping(path="api/usuario/create")
	public String crearUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		
		JSONObject jso = new JSONObject(datosUsuario);
		String rol = jso.getString("rol");
		rol = rol.substring(0,1).toUpperCase() + rol.substring(1);
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString(NOMBRE))
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
	
	/**
	 * Método que permite modificar un usuario
	 * @param datosUsuario del usuario que se quiere modificar
	 * @return mensaje devuelve el resultado de la modificacion de usuario
	 * @throws JSONException excepción que se lanza al tratar el json con los datos del usuario
	 * @throws CentroNotFoundException excepción que se lanza si el centro no existe
	 * @throws CifradoContrasenaException excepción que se lanza al cifrar la contraseña
	 */
	@PostMapping(path="api/usuario/modify")
	public String modificarUsuario(@RequestBody Map<String, Object> datosUsuario) throws JSONException, CentroNotFoundException, CifradoContrasenaException {
		
		JSONObject jso = new JSONObject(datosUsuario);
		String rol = jso.getString("rol");
		Usuario useri= new UsuarioBuilder()
				.dni(jso.getString("dni"))
				.nombre(jso.getString(NOMBRE))
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
	
	/**
	 * Método que obtiene todos los usuarios que hay
	 * @return lista de todos los usuarios que hay
	 * @throws CifradoContrasenaException excepción que se lanza al descifrar la contraseña
	 */
	@GetMapping(path="api/usuarios/obtener")
	public List<Usuario> obtenerUsuario() throws CifradoContrasenaException{
		return usuarioDao.getAllUsuarios();
	}
	
	/**
	 * Método que permite a un usuario autenticarse
	 * @param request sesión del usuario que inicia la sesión
	 * @param info email y contraseña del usuario que se quiere identificar
	 * @return datos del usuario que ha iniciado sesión y afirmación de que ha iniciado sesión
	 * correctamente
	 * @throws UsuarioNotFoundException excepción que se lanza cuando el usuario buscado no existe
	 * @throws JSONException excepción que se lanza al tratar los datos del usuario recibidos
	 */
	@PostMapping(path="api/usuario/login")
	public String login(HttpServletRequest request, @RequestBody Map<String, Object> info) throws UsuarioNotFoundException, JSONException {
		JSONObject jso = new JSONObject(info);
		String email = jso.optString(EMAIL);
		String password = DigestUtils.sha256Hex(jso.optString(PWD));
		
		Usuario usuario = usuarioDao.getUsuarioByEmail(email);
		
		if (!email.equals(usuario.getEmail()) || !password.equals(usuario.getPassword())) {
			throw new UsuarioNotFoundException("No existe un usuario con ese email y password");
		}
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Usuario ha iniciado la sesión correctamente.");
		response.put(EMAIL, usuario.getEmail());
		response.put(PWD, usuario.getPassword());
		response.put(CENTRO, usuario.getCentro());
		response.put("rol", usuario.getRol());
		response.put(NOMBRE, usuario.getNombre() + " " + usuario.getApellidos());
		return response.toString();
	}
	
	/**
	 * Método que permite eliminar a un usuario
	 * @param emailJSON email del usuario que va a eliminar
	 * @return mensaje de que el usuario se ha borrado correctamente
	 * @throws JSONException excepción que se lanza al tratar los datos del usuario recibidos
	 * @throws CitaNotFoundException excepción que se lanza cuando no existen citas
	 * @throws CentroNotFoundException excepción que se lanza si el centro no existe
	 * @throws CupoNotFoundException excepción que se lanza cuando no existe el cupo de las citas
	 * @throws UsuarioNotFoundException excepción que se lanza cuando el usuario buscado no existe
	 * @throws UsuarioVacunadoException excepción que se lanza si se intenta borrar a un usuario
	 * que ya esta vacunado, esta acción no permitimos que se realice
	 */
	@PostMapping(path="api/usuario/eliminar")
	public String eliminarUsuario(@RequestBody Map<String, Object> emailJSON) throws JSONException, CitaNotFoundException, CentroNotFoundException, CupoNotFoundException, UsuarioNotFoundException, UsuarioVacunadoException {
		JSONObject jso = new JSONObject(emailJSON);
		String emailUsuario =  jso.getString(EMAIL);
		
		usuarioDao.deleteUsuarioAndCitasByEmail(emailUsuario);
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Ha eliminado correctamente el usuario con email "+emailUsuario);
    	return response.toString();
	}
	
	
	
}
