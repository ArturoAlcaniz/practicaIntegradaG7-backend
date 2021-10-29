package com.practicaintegradag7.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CentroDaoTest {

	@Autowired
	private CentroDao centroDao;
	
	@Autowired
	private Centro nuevo;
	
	@Before
	public void before() {
		nuevo = new Centro("Almudena", "Calle Toledo", 8);
	}
	
	@Test
	public void testBuscarCentroNombre()  {
		
		String nombre = nuevo.getNombre();
		try {
			assertNotNull(centroDao.buscarCentroByNombre(nombre));
		}catch(CentroNotFoundException e) {
			fail(e.getMessage());
		}
	}
	
}
