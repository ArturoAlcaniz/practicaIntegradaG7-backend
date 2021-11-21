package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TestCita {
	
	@Test
	void failWhenTheEmailNotEquals() {
		Cita cita = new Cita("alice@example.es", LocalDateTime.of(2021, 10, 20, 12, 00), "", (short) 1);
		assertEquals("alice@example.es", cita.getEmail());
	}
	
	@Test
	void failWhenTheDatetimeNotEquals() {
		Cita cita = new Cita("alice@example.es", LocalDateTime.of(2021, 10, 20, 12, 00), "", (short) 1);
		assertEquals(LocalDateTime.of(2021, 10, 20, 12, 00), cita.getFecha());
	}
	
	@Test
	void failWhenSetCentroNotWork() {
		Cita cita = new Cita("alice@example.es", LocalDateTime.of(2021, 10, 20, 12, 00), "", (short) 1);
		Centro centro = new Centro("Centro 1", "Calle 1", 1);
		cita.setCentroNombre(centro);
		assertEquals(centro.getNombre(), cita.getCentroNombre());
	}
	
	@Test
	void failWhenSetNCitaNotEquals() {
		Cita cita = new Cita("alice@example.es", LocalDateTime.of(2021, 10, 20, 12, 00), "", (short) 1);
		cita.setNcita((short) 2);
		assertEquals((short)2,cita.getNcita());
	}

}
