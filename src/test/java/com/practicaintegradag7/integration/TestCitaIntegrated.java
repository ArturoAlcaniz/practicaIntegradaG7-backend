package com.practicaintegradag7.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.model.Cita;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCitaIntegrated {

	@Autowired
	private final CitaDao citaDao = new CitaDao();
	private Cita citaPrueba;

	@Before
	public void before() throws CitasUsuarioNotAvailable, CitasCupoNotAvailable {
		citaPrueba = citaDao.createCita();
	}
	
	@Test
	public void findCitaCreated() {
		Assert.assertTrue(citaDao.getCitasByDni(citaPrueba.getDni()).size() > 0);
	}
	
	@Test
	public void findAllCitas() {
		Assert.assertTrue(citaDao.getAllCitas().size() > 0);
	}
	
	@After
	public void after() {
		citaDao.deleteCita(citaPrueba);
	}
}
