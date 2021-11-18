package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestCupoIntegrated {

	@Autowired
	private CupoDao cupoDao = new CupoDao();
	@Autowired
	private CentroDao centroDao = new CentroDao();
	@Autowired
	private MockMvc mockMvc;
	
	private Centro centro = new Centro("Centro 2", "Calle 2", 1);
	private Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
	
	@BeforeEach
	public void before() throws CentroNotFoundException, CupoNotFoundException {
		try {
			centro = centroDao.createCentro(centro);
		} catch (CentroExistException e) {
			centro = centroDao.buscarCentroByNombre(centro.getNombre());
		}
		try {
			cupo = cupoDao.saveCupo(cupo);
		} catch(CupoExistException e) {
			cupo = cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro());
		} catch(CentroNotFoundException e) {
			System.out.println("TestCupoIntegrated -- Centro no existe????");
			fail();
		}
	}
	
	@Test
	void shouldSaveCupoWithController() throws Exception {
		cupoDao.deleteCupo(cupo);
		JSONObject json = new JSONObject();
		json.put("fechaini", cupo.getFechaInicio().toString());
		json.put("fechafin", cupo.getFechaFin().toString());
		json.put("ncitas", cupo.getNumeroCitas());
		json.put("centro", cupo.getCentro());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/cupo/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
	}
	
	@Test
	void failWhenExceptionNotHappen() {
		try {
			cupoDao.getCupoByInicialDateAndCentro(LocalDateTime.of(2022, 11, 11, 11, 00), centro.getNombre());
			fail("CupoNotFoundException expected");
		} catch (CupoNotFoundException e) {
			assertTrue(e.toString().contains("no existe"));
		}
	}
	
	@Test
	void shouldObtainCupos() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.get("/api/cupo/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Test
	void shouldNotSaveCupoBecauseCupoExists() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Cupo cupoIgual = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		try {
			cupoDao.saveCupo(cupoIgual);
			fail("CupoExistException expected");
		} catch (CupoExistException e) {
			e.getMessage();
			assertTrue(true);
		}
	}
	
	@Test
	void shouldNotSaveCupoBecauseCentroAlreadyExists() throws CentroExistException, CentroNotFoundException  {
		try {
			centroDao.createCentro(centro);
			fail("ExistException expected");
		}catch (CentroExistException e) {
			e.getMessage();
		}finally {
			assertTrue(true);
		}
	}
	
	@Test
	void shouldNotSaveCupoBecauseCentroNotExists() throws CentroNotFoundException, CupoExistException  {
		centroDao.deleteCentro(centro);
		Assertions.assertThrows(CentroNotFoundException.class, () ->
			cupoDao.saveCupo(cupo));
		
	}
	
	@Test
	void failWhenNotGetCupo() throws CentroExistException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		assertNotNull(cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()));
	}
	
	@Test
	void failWhenCupoIdNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		assertEquals(cupo.id(), cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()).id());
	}
	
	@Test
	void failWhenCupoIdNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		try {
			cupoDao.getCupoById("012345679A");
			fail("CupoNotFoundException expected");
		} catch (CupoNotFoundException e) {
			e.getMessage();
		}
	}
	
	@Test
	void failWhenCupoFechaInicioNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		try {
			cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio().plusYears(1), cupo.getCentro());
			fail("CupoNotFoundException expected");

		} catch (CupoNotFoundException e) {}
	}
	
	@Test
	void failWhenCupoInitialDateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		assertEquals(cupo.getFechaInicio(),cupoDao.getCupoById(cupo.id()).getFechaInicio());
	}
	
	@Test
	void failWhenCupoCentroNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		assertEquals(cupo.getCentro(), cupoDao.getCupoById(cupo.id()).getCentro());
	}	
	
	@Test
	void failWhenCupoNotFindById() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		
		assertNotNull(cupoDao.getCupoById(cupo.id()));
	}
	
	@Test
	void failWhenNotFindAllCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		assertNotNull(cupoDao.getAllCupos());	
	}
	
	@Test
	void failWhenCupoUpdateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		assertEquals(cupo,cupoDao.updateCupo(cupo));
	}
	
	@Test
	void shouldNotUpdateCupoNotExists() throws CupoNotFoundException {
		cupoDao.deleteCupo(cupo);
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.updateCupo(cupo));
	}
	
	@Test
	void shouldNotDeleteCupoNotExists() throws CupoNotFoundException {
		cupoDao.deleteAllCupos();
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.deleteCupo(cupo));
	}
	
	@AfterEach
	public void after() {
		try {
			cupoDao.deleteCupo(cupo);
		} catch(CupoNotFoundException e) {}
		try {
			centroDao.deleteCentro(centro);
		}catch(CentroNotFoundException e) {}
	}
}
