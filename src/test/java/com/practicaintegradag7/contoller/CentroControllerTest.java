package com.practicaintegradag7.contoller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CentroControllerTest {
	
	@Autowired
	private Centro centro;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CentroDao dao;
	
	@BeforeEach
	void before() {
		centro = new Centro("Hospital prueba", "--", 10);
	}
	
	@Test
	void shouldChangeCentroThenReturn200() throws Exception{
		JSONObject json = new JSONObject();
		json.put("hospital", centro.getNombre());
		json.put("direccion", centro.getDireccion());
		json.put("vacunas", centro.getVacunas());
		mockMvc.perform( MockMvcRequestBuilders.post("api/centros/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		
	}
	
	@AfterEach
	public void after() {
		try {
			dao.deleteCentro(centro);
		} catch (CentroNotFoundException ex) {
			fail(ex.getMessage());
		}
	}

}
