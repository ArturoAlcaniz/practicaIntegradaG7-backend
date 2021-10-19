package com.practicaintegradag7.model;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Cita {
    
    @Id
    @Column(name = "dni")
    private String dni;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne
    private String centro;

    public Cita(String dni, LocalDateTime fecha, String centro) {
    	
    	if(!validateDNI(dni)) {
    		throw new IllegalArgumentException("Dni is not valid!");
    	}
    	
    	this.dni = dni;
    	this.fecha = fecha;
    	this.centro = centro;
    }
    
    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
    
    public String getCentro() {
    	return centro;
    }
    
    public String getDni() {
    	return dni;
    }
    
    public LocalDateTime getFecha() {
    	return fecha;
    }
}