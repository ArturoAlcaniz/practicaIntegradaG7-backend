package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

public class TestCita {
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationDni() {
		new Cita("", LocalDateTime.of(2021, 10, 20, 12, 00), "");
	}
	
	@Test
	public void failWhenTheDniNotEquals() {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals("01234567A", cita.getDni());
	}
	
	@Test
	public void failWhenTheDatetimeNotEquals() {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals(LocalDateTime.of(2021, 10, 20, 12, 00), cita.getFecha());
	}

}
