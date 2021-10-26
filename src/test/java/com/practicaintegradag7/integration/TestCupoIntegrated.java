package com.practicaintegradag7.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
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
	public void shouldSaveCupo() throws CupoNotFoundException, CentroNotFoundException, CentroExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		
		assertNotNull(cupoDao.saveCupo(cupo));

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	//test que tiene que comprobar que no se puede crear otro cupo en el mismo centro y a la misma hora
	@Test
	public void shouldNotSaveCupo() {
		assertEquals(true,true);
	}
	
	@Test
	public void failWhenCupoInitialDateNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getFechaInicio(),cupoDao.getCupoById(cupo.id()).getFechaInicio());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
	
	@Test
	public void failWhenCupoCentroNotEquals() throws CupoNotFoundException, CentroNotFoundException, CentroExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertEquals(cupo.getCentro().getNombre(), cupoDao.getCupoById(cupo.id()).getCentro().getNombre());

		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}	
	
	@Test
	public void failWhenCupoNotFindById() throws CupoNotFoundException, CentroNotFoundException, CentroExistException {
		Centro centro = new Centro("Centro 2", "Calle 2", 1);
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10,centro);
		
		centroDao.createCentro(centro);
		cupoDao.saveCupo(cupo);
		
		assertNotNull(cupoRepository.findById(cupo.id()));
		
		cupoDao.deleteCupo(cupo);
		centroDao.deleteCentro(centro);
	}
}
