package com.practicaintegradag7.model;

import java.time.LocalDateTime;
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
    
    @Column(name = "ordenCita")
    private short ncita;

    public Cita(String dni, LocalDateTime fecha, String centroNombre, short ncita) {
    	this.dni = dni;
    	this.fecha = fecha;
    	this.centroNombre = centroNombre;
    	this.ncita = ncita;
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

	public short getNcita() {
		return ncita;
	}

	public void setNcita(short ncita) {
		this.ncita = ncita;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
    
}