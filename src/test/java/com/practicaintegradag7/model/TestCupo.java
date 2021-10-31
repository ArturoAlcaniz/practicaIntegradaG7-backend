package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestCupo {
	
	private static final Centro centro = new Centro("Centro 1", "Calle 1", 1); 
	private static final LocalDateTime fechaInicio = LocalDateTime.now().plusMinutes(15);
	private static final LocalDateTime fechaFin = LocalDateTime.now();
	
	
	@Test
	void checkValidationFecha() {
		try {
			Cupo cupo = new Cupo(fechaInicio, fechaFin, 10, centro);
			cupo.getFechaInicio();
		} catch (IllegalArgumentException e) {
			assertTrue(e.toString().contains("Fecha de inicio no puede ser posterior a la fecha de fin"));
		}
		
	}
	
	@Test
	void checkValidationFechaActual() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Cupo(fechaInicio, fechaFin, 10, centro));
	}
	
	@Test
	void failWhenFechaInicioNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00), cupo.getFechaInicio());
	}
	
	@Test
	void failWhenFechaFinNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), cupo.getFechaFin());
	}
	
	@Test
	void failWhenNumeroCitasNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals(10, cupo.getNumeroCitas());
	}

	@Test
	void failWhenCentroNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		assertEquals(centro, cupo.getCentro());
	}

}
