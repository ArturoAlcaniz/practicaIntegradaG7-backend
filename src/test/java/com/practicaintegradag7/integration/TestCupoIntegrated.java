package com.practicaintegradag7.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.repos.CupoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCupoIntegrated {
	@Autowired
	private CupoRepository cupoRepository;
	@Autowired
	private CupoDao cupoDao = new CupoDao();
	@Autowired
	private CentroDao centroDao = new CentroDao();
		
	@Test
	public void shouldSaveCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		
		assertNotNull(cupoDao.saveCupo(cupo));

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void shouldNotSaveCupoBecauseCupoExists() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		Cupo cupoIgual = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		try {
			centroDao.createCentro(centro);
		} catch (CentroExistException e) {
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
	
	@Test (expected = CentroNotFoundException.class)
	public void shouldNotSaveCupoBecauseCentroNotExists() throws CentroNotFoundException, CupoExistException  {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		cupoDao.saveCupo(cupo);
		
	}
	
	@Test
	public void failWhenNotGetCupo() throws CentroExistException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenCupoIdNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.id(), cupoDao.getCupoByInicialDateAndCentro(cupo.getFechaInicio(), cupo.getCentro()).id());
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenCupoIdNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
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
	public void failWhenCupoFechaInicioNotFound() throws CentroExistException, CentroNotFoundException, CupoExistException, CupoNotFoundException {
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
	public void failWhenCupoInitialDateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getFechaInicio(),cupoDao.getCupoById(cupo.id()).getFechaInicio());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenCupoCentroNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getCentro().getNombre(), cupoDao.getCupoById(cupo.id()).getCentro().getNombre());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}	
	
	@Test
	public void failWhenCupoNotFindById() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoRepository.findById(cupo.id()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenNotFindAllCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoDao.getAllCupos());	
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenCupoUpdateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException, CupoExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo,cupoDao.updateCupo(cupo));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test(expected = CupoNotFoundException.class)
	public void shouldNotUpdateCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		cupoDao.updateCupo(cupo);
	}
	
	@Test(expected = CupoNotFoundException.class)
	public void shouldNotDeleteCupoNotExists() throws CupoNotFoundException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		cupoDao.deleteCupo(cupo);
	}
}
