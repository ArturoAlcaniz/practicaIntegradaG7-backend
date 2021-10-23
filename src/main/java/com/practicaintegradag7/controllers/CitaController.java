package com.practicaintegradag7.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.validate.CitaValidator;

@RestController
public class CitaController {
	
	@Autowired
	private CitaDao citaDao;
	
	@Autowired
	private CitaValidator citaValidator;
	
    @PostMapping("/citas/create")
    public Cita crearCita(@RequestBody Map<String, Object> datosCita){
		JSONObject jso = new JSONObject(datosCita);
        String dni = jso.getString("dni");
        String fecha = jso.getString("fecha");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		Cita cita = new Cita(dni, LocalDateTime.parse(fecha, formatter));
    	citaValidator.createCitaValidation(cita);
    	return citaDao.createCita(cita);
    }
    
    
}