package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestCentro {
	
	@Test
	void failIfVacunasNotValid() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Centro("Centro 1", "Calle 1", -2));
	}
	
	@Test
	void failIfNombreNotEquals() {
		Centro centro = new Centro("Centro 2", "Calle 2", 0);
		assertEquals("Centro 2", centro.getNombre());
	}
	
	@Test
	void failIfDireccionNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 1", 1);
		assertEquals("Calle 1", centro.getDireccion());
	}
	
	@Test
	void failIfVacunasDisponiblesNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 3", 100);
		assertEquals(100, centro.getVacunas());
	}
	
	@Test
	void failWhenIdNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 3", 100);
		centro.setId("prueba");
		assertEquals("prueba", centro.getId());
	}
	
	@Test
	void failWhenNombreSetNotEquals() {
		Centro centro = new Centro("Centro 2", "Calle 2", 0);
		centro.setNombre("Centro 3");
		assertEquals("Centro 3", centro.getNombre());
	}
	
	@Test
	void failWhenDireccionSetNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 1", 1);
		centro.setDireccion("Calle 2");
		assertEquals("Calle 2", centro.getDireccion());
	}
	
	@Test
	void failWhenCentroCitasNotEquals() {
		Cita cita = new Cita("01234567A", LocalDateTime.of(2021, 10, 20, 12, 00), "", (short) 1);
		Set<Cita> citas = new HashSet<Cita>();
		citas.add(cita);
		Centro centro = new Centro("Centro 1", "Calle 1", 1);
		centro.setCentroCitas(citas);
		assertEquals(citas, centro.getCentroCitas());
	}
}
