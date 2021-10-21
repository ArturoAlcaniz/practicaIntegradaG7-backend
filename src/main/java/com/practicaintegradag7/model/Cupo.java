package com.practicaintegradag7.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Cupo {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;
	
	@Column(name = "fechaInicio")
	private LocalDateTime fechaInicio;
	
	@Column(name = "fechaFin")
	private LocalDateTime fechaFin;
	
	
	public Cupo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		
		if(!validateFechas(fechaInicio, fechaFin)) {
			throw new IllegalArgumentException("Fecha de inicio no puede ser posterior a la fecha de fin");
		}

		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	
	}
	
	private boolean validateFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    	return fechaInicio.isBefore(fechaFin) && !fechaInicio.isBefore(LocalDateTime.now());
    }
	
	public String id () {
		return id;
	}
	
	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}


}
