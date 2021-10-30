package com.practicaintegradag7.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Cupo")
public class Cupo {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;
	
	@Column(name = "fechaInicio")
	private LocalDateTime fechaInicio;
	
	@Column(name = "fechaFin")
	private LocalDateTime fechaFin;
	
	@Column(name = "numeroCitas")
	private int numeroCitas;
	
	@ManyToOne
	private Centro centro;
	
	public Cupo(LocalDateTime fechaInicio, LocalDateTime fechaFin, int numeroCitas, Centro centro) {
		
		if(!validateFechas(fechaInicio, fechaFin)) {
			throw new IllegalArgumentException("Fecha de inicio no puede ser posterior a la fecha de fin");
		}

		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.numeroCitas = numeroCitas;
		this.centro = centro;
	
	}
	
	private boolean validateFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    	return fechaInicio.isBefore(fechaFin);
    }
	
	public String id() {
		return id;
	}
	
	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}
	
	public int getNumeroCitas() {
		return numeroCitas;
	}

	public Centro getCentro() {
		return centro;
	}

}
