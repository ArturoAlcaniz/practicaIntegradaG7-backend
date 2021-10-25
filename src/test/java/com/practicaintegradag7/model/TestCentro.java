package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestCentro {
	
	@Test(expected = IllegalArgumentException.class)
	public void failIfVacunasNotValid() {
		new Centro("Centro 1", "Calle 1", -2);
	}
	
	@Test
	public void failIfNombreNotEquals() {
		Centro centro = new Centro("Centro 2", "Calle 2", 0);
		assertEquals("Centro 2", centro.getNombre());
	}
	
	@Test
	public void failIfDireccionNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 1", 1);
		assertEquals("Calle 1", centro.getDireccion());
	}
	
	@Test
	public void failIfVacunasDisponiblesNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 3", 100);
		assertEquals(100, centro.getVacunas());
	}
}
