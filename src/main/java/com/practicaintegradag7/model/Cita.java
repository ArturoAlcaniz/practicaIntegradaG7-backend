package com.practicaintegradag7.model;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Cita")
public class Cita {

	@Id
    @Column(name = "dni")
    private String dni;

    @Column(name = "fecha")
    @NotNull
    private LocalDateTime fecha;

    @Column(name = "centroNombre")
    private String centroNombre;

    public Cita(String dni, LocalDateTime fecha, String centroNombre) {
    	
    	if(!validateDNI(dni)) {
    		throw new IllegalArgumentException("Dni no es valido!");
    	}
    	
    	this.dni = dni;
    	this.fecha = fecha;
    	this.centroNombre = centroNombre;
    }
    
    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
    
    public String getCentroNombre() {
    	return centroNombre;
    }
    
    public void setCentroNombre(Centro centro) {
    	this.centroNombre = centro.getNombre();
    }
    
    public String getDni() {
    	return dni;
    }
    
    public LocalDateTime getFecha() {
    	return fecha;
    }
}