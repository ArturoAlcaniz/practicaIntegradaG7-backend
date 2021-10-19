package com.practicaintegradag7.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
		
		if(fechaInicio.isAfter(fechaFin)) {
			throw new IllegalArgumentException("Fecha de inicio no puede ser posterior a la fecha de fin");
		}

		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	
	}
	
	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}


}