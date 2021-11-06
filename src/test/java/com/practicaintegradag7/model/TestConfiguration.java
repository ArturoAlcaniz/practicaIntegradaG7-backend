package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.practicaintegradag7.exceptions.ConfigurationTimeException;

class TestConfiguration {

	@Test
	void failWhenHoraInicioNotEquals() throws ConfigurationTimeException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 20;
		int franjasPorDia = 20;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		assertEquals(horaInicio, configuration.getHoraInicio());
	}
	
	@Test
	void failWhenHoraFinNotEquals() throws ConfigurationTimeException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 20;
		int franjasPorDia = 20;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		assertEquals(horaFin, configuration.getHoraFin());
	}
	
	@Test
	void failWhenCitasPorFranjaNotEquals() throws ConfigurationTimeException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 20;
		int franjasPorDia = 20;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		assertEquals(citasPorFranja, configuration.getCitasPorFranja());
	}
	
	@Test
	void failWhenFranjasPorDiaNotEquals() throws ConfigurationTimeException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 20;
		int franjasPorDia = 20;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		assertEquals(franjasPorDia, configuration.getFranjasPorDia());
		LocalTime horaInicio2 = LocalTime.parse("18:00");
		LocalTime horaFin2 = LocalTime.parse("17:00");
		
		try {
			new Configuration(horaInicio2, horaFin2, citasPorFranja, franjasPorDia);
		} catch (ConfigurationTimeException e) {
			assertEquals("La hora inicio no puede ser superior a la hora fin", e.getMessage());
		}
	}
}
