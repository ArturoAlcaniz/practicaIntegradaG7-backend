package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
			new Cupo(fechaInicio, fechaFin, 10, centro.getNombre());
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Fecha de inicio no puede ser posterior a la fecha de fin", e.getMessage());
		}
		
	}
	
	@Test
	void checkValidationFechaActual() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Cupo(fechaInicio, fechaFin, 10, centro.getNombre()));
	}
	
	@Test
	void failWhenFechaInicioNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,"Centro 1");
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00), cupo.getFechaInicio());
	}
	
	@Test
	void failWhenFechaFinNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,"Centro 1");
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), cupo.getFechaFin());
	}
	
	@Test
	void failWhenNumeroCitasNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,"Centro 1");
		assertEquals(10, cupo.getNumeroCitas());
	}

	@Test
	void failWhenCentroNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		assertEquals(centro.getNombre(), cupo.getCentro());
	}

}
