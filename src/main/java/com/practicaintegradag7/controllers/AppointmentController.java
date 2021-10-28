package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class AppointmentController{
	@Autowired
	private CitaDao citaDao;
	
	@PostMapping(path="/api/citas/create")
    public String crearCita() throws JSONException, CitasUsuarioNotAvailable {
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Ha pedido cita correctamente para el "+citaDao.createCita().getFecha());
    	return response.toString();
    }
}
