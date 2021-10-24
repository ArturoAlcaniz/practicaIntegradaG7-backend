package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.repos.CitaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
public class HelloController {
	
	@Autowired
	private CitaRepository repositoryCita;
	
    @GetMapping("/Citas/insertar")
    public String hello() {
    	Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00));
    	return repositoryCita.insert(cita).toString();
    }
}