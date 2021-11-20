package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.model.Usuario;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class PermissionsController {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	private static final String EMAIL = "email";
	private static final String PWD = "password";
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	private static final String[] AdminSites = {"centros", "cupos", "formulario", "usuarios"};
	private static final String[] SanitarioSites = {"listaVacunacion"};
	private static final String[] PacienteSites = {"appointment", "modificarCita"};
	
	@PostMapping(path="api/perms/check")
	public String checkPrivilegesAdmin(@RequestBody Map<String, Object> datosUsuario) throws JSONException {
		JSONObject jso = new JSONObject(datosUsuario);
		String code = "200";
		String mssg = "OK";
		
		try {
			String email = jso.getString(EMAIL);
			String pwd = jso.getString(PWD);
			String from = jso.getString("site");
			boolean doDefault = true;
			Usuario u = usuarioDao.getUsuarioByEmail(email);
			checkSiteExists(from);
			
			if(!u.getPassword().equals(pwd)) throw new UsuarioNotFoundException("Usuario no reconocido");
			
			switch(u.getRol().toLowerCase()) {
			case "paciente":
				doDefault = !checkExists(PacienteSites, from);
				break;
			case "administrador":
				doDefault = !checkExists(AdminSites, from);
				break;
			case "sanitario":
				doDefault = !checkExists(SanitarioSites, from);
				break;
			default:
				break;
			}
			
			if(doDefault) throw new UsuarioNotFoundException("Rol " + u.getRol() + " no permitido en este sitio");
		} catch(UsuarioNotFoundException e) {
			code = "405"; //HTTP ACCESS DENIED
			mssg = e.getMessage();
		} catch(JSONException e) {
			code = "500";
			mssg = e.getMessage();
		} catch(IllegalArgumentException e) {
			code = "404";
			mssg = e.getMessage();
		}
		
		JSONObject response = new JSONObject();
		response.put(STATUS, code);
		response.put(MSSG, mssg);
    	return response.toString();
	}

	private void checkSiteExists(String from) {
		boolean exists = false;
		for(int i = 0; i < AdminSites.length && !exists; i++) exists = from.equals(AdminSites[i]);
		for(int i = 0; i < SanitarioSites.length && !exists; i++) exists = from.equals(SanitarioSites[i]);
		for(int i = 0; i < PacienteSites.length && !exists; i++) exists = from.equals(PacienteSites[i]);
		if(!exists) throw new IllegalArgumentException("El sitio no existe");
	}

	private boolean checkExists(String[] array, String site) {
		boolean exists = false;
		for(int i = 0; i < array.length && !exists; i++) exists = array[i].equals(site);
		return exists;
	}
	
}
