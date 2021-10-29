package com.practicaintegradag7.exception;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VacunasNoValidasExceptionTest {

	@Autowired
	VacunasNoValidasException vac;
	
	@Before
	public void before() {
		vac = new VacunasNoValidasException("Vacunas no válidas");
	}
	
	@Test
	public void testCentroExistException () {
		
		String antiguo = vac.getMessage();
		String nuevo = "Vacunas no válidas";
		assertEquals(antiguo, nuevo);
		
	}
}
