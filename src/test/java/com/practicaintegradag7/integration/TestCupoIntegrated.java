package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
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
	
	@Test
	void shouldSaveCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		
		assertNotNull(cupoDao.saveCupo(cupo));

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldSaveCupoWithController() throws Exception {
		Centro centro = new Centro("Centro 2", "Calle 2", 10);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 13,centro.getNombre());
		centroDao.createCentro(centro);
		JSONObject json = new JSONObject();
		json.put("fechaini", cupo.getFechaInicio().toString());
		json.put("fechafin", cupo.getFechaFin().toString());
		json.put("ncitas", cupo.getNumeroCitas());
		json.put("centro", cupo.getCentro());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/cupo/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		cupoDao.deleteCupo(cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), centro.getNombre()));
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenExceptionNotHappen() {
		Centro centro = new Centro("Centro 2", "Calle 2", 10);
		try {
			cupoDao.getCupoByInicialDateAndCentro(LocalDateTime.of(2022, 11, 11, 11, 00), centro.getNombre());
			fail("CupoNotFoundException expected");
		} catch (CupoNotFoundException e) {
			assertTrue(e.toString().contains("no existe"));
		}
	}
	
	@Test
	void shouldObtainCupos() throws Exception {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		
		mockMvc.perform( MockMvcRequestBuilders.get("/api/cupo/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
		assertNotNull(cupoDao.saveCupo(cupo));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotSaveCupoBecauseCupoExists() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 10, 00), LocalDateTime.of(2022, 10, 20, 10, 00).plusMinutes(15), 10,centro.getNombre());
		Cupo cupoIgual = new Cupo(LocalDateTime.of(2022, 10, 20, 10, 00), LocalDateTime.of(2022, 10, 20, 10, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		try {
			cupoDao.saveCupo(cupo);
		}catch (CupoExistException e) {
		}
		try {
		cupoDao.saveCupo(cupoIgual);
		fail("CupoExistException expected");

		} catch (CupoExistException e) {
			e.getMessage();
		}finally {
			cupoDao.deleteCupo(cupo);
			centroDao.deleteCentro(centro);
		}
	}
	
	@Test
	void shouldNotSaveCupoBecauseCentroAlreadyExists() throws CentroExistException, CentroNotFoundException  {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		
		centroDao.createCentro(centro);
		try {
			centroDao.createCentro(centro);
			fail("ExistException expected");
		}catch (CentroExistException e) {
			e.getMessage();
		}finally {
			centroDao.deleteCentro(centro);
			assertTrue(true);
		}
	}
	
	@Test
	void shouldNotSaveCupoBecauseCentroNotExists() throws CentroNotFoundException, CupoExistException  {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		Assertions.assertThrows(CentroNotFoundException.class, () ->
			cupoDao.saveCupo(cupo));
		
	}
	
	@Test
	void failWhenNotGetCupo() throws CentroExistException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoIdNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.id(), cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()).id());
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoIdNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		try {
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		} catch (CentroNotFoundException e) {
			e.getMessage();
		}

		try {
		cupoDao.getCupoById("012345679A");
		fail("CupoNotFoundException expected");

		} catch (CupoNotFoundException e) {
			e.getMessage();
		}finally {
			cupoDao.deleteCupo(cupo);
			centroDao.deleteCentro(centro);
		}
	}
	
	@Test
	void failWhenCupoFechaInicioNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);

		try {
		cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio().plusYears(1), cupo.getCentro());
		fail("CupoNotFoundException expected");

		} catch (CupoNotFoundException e) {
		}finally {
			cupoDao.deleteCupo(cupo);
			centroDao.deleteCentro(centro);
		}
	}
	
	@Test
	void failWhenCupoInitialDateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getFechaInicio(),cupoDao.getCupoById(cupo.id()).getFechaInicio());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoCentroNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getCentro(), cupoDao.getCupoById(cupo.id()).getCentro());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}	
	
	@Test
	void failWhenCupoNotFindById() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getCupoById(cupo.id()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenNotFindAllCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getAllCupos());	
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoUpdateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo,cupoDao.updateCupo(cupo));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotUpdateCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.updateCupo(cupo));
	}
	
	@Test
	void shouldNotDeleteCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro.getNombre());
		
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.deleteCupo(cupo));
	}
}
