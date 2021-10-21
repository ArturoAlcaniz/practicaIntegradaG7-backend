package com.practicaintegradag7.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.validate.CitaValidator;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class CitaController {
	
	@Autowired
	private CitaDao citaDao;
	
	@Autowired
	private CitaValidator citaValidator;
	
	@ResponseBody
    @PostMapping("/citas/create")
    public void crearCita(@RequestParam String dni, @RequestParam String fecha) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		Cita cita = new Cita(dni, LocalDateTime.parse(fecha, formatter));
    	citaValidator.createCitaValidation(cita);
    	citaDao.createCita(cita);
    }
}