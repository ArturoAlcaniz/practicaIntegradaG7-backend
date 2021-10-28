package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestCentroIntegrated {
	@Autowired
	private final CentroDao aux = new CentroDao();
	private Centro prueba = new Centro("PRUEBA", "-", 20);
	
	@BeforeEach
	public void before() throws CentroExistException, CentroNotFoundException {
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
	void testAddVacunas() {
		try {
			int nVacs = 10;
			aux.addVacunas(prueba.getId(), nVacs);
			System.out.println("testAddVacunas - vacunas anadidas");
			int vacunasRemotas = aux.buscarCentro(prueba.getId()).getVacunas();
			System.out.println("Vacunas remotas recogidas");
			int vacunasTotales = prueba.getVacunas() + nVacs;
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
	void addVacunasNonExistentCentro() {
		try {
			byte[] array = new byte[7];
		    new Random().nextBytes(array);
		    String randomCenter = new String(array, Charset.forName("UTF-8"));
		    
			int nVacs = 10;
			aux.addVacunas(randomCenter, nVacs);
			fail("Vacunas anadidas a centro no existente");
		} catch (CentroNotFoundException ex) {
			assertTrue(ex.getMessage().contains("no encontrado"));
		} catch(VacunasNoValidasException ex) {
			fail(ex.getMessage());
		}
	}
	
	@AfterEach
	public void after() {
		try {
			aux.deleteCentro(prueba);
		} catch (CentroNotFoundException ex) {
			fail(ex.getMessage());
		}
	}
}
