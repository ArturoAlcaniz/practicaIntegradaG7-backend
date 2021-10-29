package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class TestCentroController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CentroDao dao;
	
	@Order(1)
	@Test
	void shouldChangeCentroThenReturn200() throws Exception{
		Centro centro= new Centro("Hospital", "--", 10);
		JSONObject json = new JSONObject();
		json.put("nombre", centro.getNombre());
		json.put("direccion", centro.getDireccion());
		json.put("vacunas", centro.getVacunas());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/centros/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		try {
			dao.existeCentro(centro.getNombre());
		}catch(CentroExistException e) {
			assertTrue(true);
			
		}
		
	}
	@Order(2)
	@Test
	void testObtenerCentros() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.get("/api/centros/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Order(3)
	@Test
	void testDelete() throws CentroNotFoundException {
		Centro centro = dao.buscarCentroByNombre("Hospital");
		dao.deleteCentro(centro);
		assertTrue(true);
	}

}