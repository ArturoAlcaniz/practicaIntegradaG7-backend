package com.practicaintegradag7.contoller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.practicaintegradag7.controllers.CentroController;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.model.Centro;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CentroControllerTest {
	
	@Autowired
	private Centro centro;
	
	@Autowired
	private CentroController controller;
	
	@BeforeEach
	public void before() {
		centro = new Centro("Hospital 1", "Calle Paloma", 10);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void crearCentroTest() {
		try {
			controller.crearCentro((Map<String, Object>) centro);
		} catch (JSONException e) {
			fail(e.getMessage());
		} catch (CentroExistException e) {
			fail(e.getMessage());
		}
	}
}
