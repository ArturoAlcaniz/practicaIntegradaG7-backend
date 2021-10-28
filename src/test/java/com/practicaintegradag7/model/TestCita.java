package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestCita {
	
	@Test
	void checkValidationDni() {
		LocalDateTime fecha = LocalDateTime.of(2021, 10, 20, 12, 00);
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Cita("", fecha, ""));
	}
	
	@Test
	void failWhenTheDniNotEquals() {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals("01234567A", cita.getDni());
	}
	
	@Test
	void failWhenTheDatetimeNotEquals() {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals(LocalDateTime.of(2021, 10, 20, 12, 00), cita.getFecha());
	}

}
