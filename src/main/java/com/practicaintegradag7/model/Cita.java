package com.practicaintegradag7.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Cita")
public class Cita {

	@Id
    @Column(name = "email")
    private String email;

    @Column(name = "fecha")
    @NotNull
    private LocalDateTime fecha;

    @Column(name = "centroNombre")
    private String centroNombre;

    public Cita(String email, LocalDateTime fecha, String centroNombre) {
    	this.email= email;
    	this.fecha = fecha;
    	this.centroNombre = centroNombre;
    }
    
    public String getCentroNombre() {
    	return centroNombre;
    }
    
    public void setCentroNombre(Centro centro) {
    	this.centroNombre = centro.getNombre();
    }
    
    public String getEmail() {
    	return email;
    }
    
    public LocalDateTime getFecha() {
    	return fecha;
    }
}