package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

public class TestCupo {
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationFecha() {
		Cupo cupo = new Cupo(LocalDateTime.now().plusMinutes(15), LocalDateTime.now(), 10);
		cupo.getFechaFin();
	}
	
	@Test
	public void failWhenFechaInicioNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10);
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00), cupo.getFechaInicio());
	}
	
	@Test
	public void failWhenFechaFinNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10);
		assertEquals(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), cupo.getFechaFin());
	}
	
	@Test
	public void failWhenNumeroCitasNotEquals() {
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10);
		assertEquals(10, cupo.getNumeroCitas());
	}


}