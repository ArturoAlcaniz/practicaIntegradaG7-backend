package com.practicaintegradag7.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.practicaintegradag7.exceptions.ConfigurationTimeException;

@Document(collection = "Configuration")
public class Configuration {
	
	@JsonIgnore
	@Column(name = "horaInicio")
	private LocalTime horaInicio;
	
	@JsonIgnore
	@Column(name = "horaFin")
	private LocalTime horaFin;
	
	@JsonProperty("citasPorFranja")
	@Column(name = "citasPorFranja")
	private int citasPorFranja;
	
	@JsonProperty("franjasPorDia")
	@Column(name = "franjasPorDia")
	private int franjasPorDia;
	
	@Transient
	@JsonSerialize
	@JsonProperty("horaInicio")
	private String horaInicioFormatted;
	
	@Transient
	@JsonSerialize
	@JsonProperty("horaFin")
	private String horaFinFormatted;
	
	public Configuration(LocalTime horaInicio, LocalTime horaFin, int citasPorFranja, int franjasPorDia) throws ConfigurationTimeException {
		if (horaInicio.compareTo(horaFin) > 0) {
			throw new ConfigurationTimeException();
		}
		
		this.citasPorFranja = citasPorFranja;
		this.franjasPorDia = franjasPorDia;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		this.horaInicioFormatted = horaInicio.format(dtf);
		this.horaFinFormatted = horaFin.format(dtf);
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}
	
	public LocalTime getHoraInicio() {
		return horaInicio;
	}
	
	public LocalTime getHoraFin() {
		return horaFin;
	}
	
	public int getCitasPorFranja() {
		return citasPorFranja;
	}
	
	public int getFranjasPorDia() {
		return franjasPorDia;
	}
}
