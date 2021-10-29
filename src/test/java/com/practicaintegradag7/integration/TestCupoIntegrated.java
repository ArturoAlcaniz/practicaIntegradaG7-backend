package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.repos.CupoRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestCupoIntegrated {
	@Autowired
	private CupoRepository cupoRepository;
	@Autowired
	private CupoDao cupoDao = new CupoDao();
	@Autowired
	private CentroDao centroDao = new CentroDao();
		
	@Test
	void shouldSaveCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		
		assertNotNull(cupoDao.saveCupo(cupo));

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotSaveCupoBecauseCupoExists() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		Cupo cupoIgual = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		try {
			centroDao.createCentro(centro);
		}catch (CentroExistException e) {
			e.getMessage();
		}
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
		
		try {
			centroDao.createCentro(centro);
		}catch (CentroExistException e) {
			e.getMessage();
		}try {
			centroDao.createCentro(centro);
			fail("ExistException expected");
		}catch (CentroExistException e) {
			e.getMessage();
		}finally {
			centroDao.deleteCentro(centro);
		}
	}
	
	@Test
	void shouldNotSaveCupoBecauseCentroNotExists() throws CentroNotFoundException, CupoExistException  {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		Assertions.assertThrows(CentroNotFoundException.class, () ->
			cupoDao.saveCupo(cupo));
		
	}
	
	@Test
	void failWhenNotGetCupo() throws CentroExistException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoIdNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.id(), cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()).id());
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoIdNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
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
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
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
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getFechaInicio(),cupoDao.getCupoById(cupo.id()).getFechaInicio());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoCentroNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getCentro().getNombre(), cupoDao.getCupoById(cupo.id()).getCentro().getNombre());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}	
	
	@Test
	void failWhenCupoNotFindById() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoRepository.findById(cupo.id()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenNotFindAllCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getAllCupos());	
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void failWhenCupoUpdateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo,cupoDao.updateCupo(cupo));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotUpdateCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.updateCupo(cupo));
	}
	
	@Test
	void shouldNotDeleteCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		Assertions.assertThrows(CupoNotFoundException.class, () ->
			cupoDao.deleteCupo(cupo));
	}
}
