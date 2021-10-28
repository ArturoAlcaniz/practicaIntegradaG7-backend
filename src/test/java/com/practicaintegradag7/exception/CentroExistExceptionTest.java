package com.practicaintegradag7.exception;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.exceptions.CentroExistException;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CentroExistExceptionTest {
	
	@Autowired
	CentroExistException ex;
	
	@Before
	public void before() {
		ex = new CentroExistException("Error el centro ya existe");
	}
	
	@Test
	public void testCentroExistException () {
		
		String antiguo = ex.getMessage();
		String nuevo = "Error el centro ya existe";
		assertEquals(antiguo, nuevo);
		
	}
	
}
