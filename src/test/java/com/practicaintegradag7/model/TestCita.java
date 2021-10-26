package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import com.practicaintegradag7.exceptions.CitasDniNotValid;

public class TestCita {
	
	@Test(expected = CitasDniNotValid.class)
	public void checkValidationDni() throws CitasDniNotValid {
		new Cita("", LocalDateTime.of(2021, 10, 20, 12, 00), "");
	}
	
	@Test
	public void failWhenTheDniNotEquals() throws CitasDniNotValid {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals("01234567A", cita.getDni());
	}
	
	@Test
	public void failWhenTheDatetimeNotEquals() throws CitasDniNotValid {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "");
		assertEquals(LocalDateTime.of(2021, 10, 20, 12, 00), cita.getFecha());
	}

}
