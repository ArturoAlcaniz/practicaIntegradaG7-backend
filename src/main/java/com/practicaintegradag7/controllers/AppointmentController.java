package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.exceptions.CitasDniNotValid;
import com.practicaintegradag7.exceptions.CitasLimitException;
import com.practicaintegradag7.model.Cita;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class AppointmentController{
	@Autowired
	private CitaDao citaDao;
	
	@PostMapping(path="/api/makeAppointment", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Cita crearCita(@RequestBody Map<String, Object> datosCita) throws CitasDniNotValid, CitasLimitException, JSONException{
		JSONObject jso = new JSONObject(datosCita);
        String dni = jso.getString("dni");
        String fecha = jso.getString("fecha");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		Cita cita = new Cita(dni, LocalDateTime.parse(fecha, formatter));
    	return citaDao.createCita(cita);
    }
}
