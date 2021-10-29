package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
