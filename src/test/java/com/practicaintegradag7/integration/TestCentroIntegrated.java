package com.practicaintegradag7.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;

public class TestCentroIntegrated {
	private final CentroDao aux = new CentroDao();
	private final Centro prueba = new Centro("PRUEBA", "", 20);
	
	@Before
	public void before() {
		aux.createCentro(prueba);
		assertTrue(true); //Centro added correctly
	}
	
	@Test
	public void testAddVacunas() {
		try {
			int nVacs = 10;
			aux.addVacunas(prueba.getNombre(), nVacs);
			int vacunasRemotas = aux.buscarCentro(prueba.getNombre()).getVacunasDisponibles();
			int vacunasTotales = prueba.getVacunasDisponibles() + nVacs;
			assertEquals(vacunasRemotas, vacunasTotales);
		}catch(CentroNotFoundException ex) {
			assertTrue(false);
		}catch(NoSuchElementException ex) {
			assertTrue(false);
		}
	}
	
	@Test
	public void addVacunasNonExistentCentro() {
		try {
			byte[] array = new byte[7]; // length is bounded by 7
		    new Random().nextBytes(array);
		    String randomCenter = new String(array, Charset.forName("UTF-8"));
		    
			int nVacs = 10;
			aux.addVacunas(randomCenter, nVacs);
			assertTrue(false);
		} catch (CentroNotFoundException e) {
			assertTrue(true);
		}
	}
}
