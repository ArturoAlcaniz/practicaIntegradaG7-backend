package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Random;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestCentroIntegrated {
	@Autowired
	private final CentroDao aux = new CentroDao();
	
	@Autowired
	private MockMvc mockMvc;
	
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
	
	@Test
	void searchCentroByIdNotExist() {
		try {
			aux.buscarCentro("no existe");
		} catch (CentroNotFoundException e) {
			assertTrue(e.getMessage().contains("no existe"));
		}
	}
	
	@Test
	void searchCentroByNameNotExist() {
		try {
			aux.buscarCentroByNombre("no existe");
		} catch (CentroNotFoundException e) {
			assertTrue(true);
		}
	}
	
	@Test
	void shouldChangeVaccinesThenReturn200() throws Exception {
		int vacunas = aux.buscarCentroByNombre(prueba.getNombre()).getVacunas();
		JSONObject json = new JSONObject();
		json.put("hospital", prueba.getNombre());
		json.put("amount", 2);
		mockMvc.perform( MockMvcRequestBuilders.post("/api/addVaccines").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertEquals(vacunas+2, aux.buscarCentroByNombre(prueba.getNombre()).getVacunas());
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
