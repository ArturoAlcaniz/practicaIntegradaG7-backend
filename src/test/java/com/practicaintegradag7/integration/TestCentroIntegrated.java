package com.practicaintegradag7.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCentroIntegrated {
	@Autowired
	private final CentroDao aux = new CentroDao();
	private Centro prueba = new Centro("PRUEBA", "-", 20);
	
	@Before
	public void before() {
		Centro c = aux.createCentro(prueba);
		if(c.getNombre().equals(prueba.getNombre())) {
			System.out.println("Centro de prueba anadido correctamente");
			this.prueba = c;
		}
		else {
			System.out.println("Centro de prueba no anadido");
			System.exit(1);
		}
	}
	
	@Test
	public void testAddVacunas() {
		try {
			int nVacs = 10;
			aux.addVacunas(prueba.getId(), nVacs);
			System.out.println("testAddVacunas - vacunas anadidas");
			int vacunasRemotas = aux.buscarCentro(prueba.getId()).getVacunasDisponibles();
			System.out.println("Vacunas remotas recogidas");
			int vacunasTotales = prueba.getVacunasDisponibles() + nVacs;
			assertEquals(vacunasRemotas, vacunasTotales);
		}catch(CentroNotFoundException ex) {
			fail(ex.getMessage());
		}catch(NoSuchElementException ex) {
			fail(ex.getMessage());
		}catch(VacunasNoValidasException ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void addVacunasNonExistentCentro() {
		try {
			byte[] array = new byte[7];
		    new Random().nextBytes(array);
		    String randomCenter = new String(array, Charset.forName("UTF-8"));
		    
			int nVacs = 10;
			aux.addVacunas(randomCenter, nVacs);
			fail("Vacunas anadidas a centro no existente");
		} catch (CentroNotFoundException ex) {
			assertTrue(true);
		} catch(VacunasNoValidasException ex) {
			fail(ex.getMessage());
		}
	}
	
	@After
	public void after() {
		try {
			aux.deleteCentro(prueba);
		} catch (CentroNotFoundException ex) {
			fail(ex.getMessage());
		}
	}
}
