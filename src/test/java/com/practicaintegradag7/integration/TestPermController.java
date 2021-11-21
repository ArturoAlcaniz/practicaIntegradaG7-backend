package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class TestPermController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UsuarioDao dao;
	
	private final String EMAIL = "email";
	private final String PWD = "password";
	private final String SITE = "site";
	private final Usuario usuario = new UsuarioBuilder()
			.dni("05718583J")
			.nombre("Francisco")
			.apellidos("Morisco Parra")
			.email("franMorisco@gmail.com")
			.password("Iso+grupo7")
			.centro(null)
			.rol("Paciente")
			.build();
	
	@Order(1)
	@Test
	void insertUser() throws Exception{
		dao.saveUsuario(usuario);
		assertTrue(true);
	}
	
	@Order(2)
	@Test
	void shouldAllowAccess() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, usuario.getEmail());
		json.put(PWD, usuario.getPassword());
		json.put(SITE, "appointment");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(3)
	@Test
	void denyAccessUnknownSite() throws Exception {
		JSONObject json = new JSONObject();
		json.put(EMAIL, usuario.getEmail());
		json.put(PWD, usuario.getPassword());
		json.put(SITE, "a");
		MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
		String res = aux.getResponse().getContentAsString();
		assertTrue(res.contains("404"));
	}
	
	@Order(4)
	@Test
	void denyAccessNoPermission() throws Exception {
		JSONObject json = new JSONObject();
		json.put(EMAIL, usuario.getEmail());
		json.put(PWD, usuario.getPassword());
		json.put(SITE, "centros");
		MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
		String res = aux.getResponse().getContentAsString();
		assertTrue(res.contains("405"));
	}
	
	@Order(5)
	@Test
	void testDelete() throws CentroNotFoundException {
		dao.deleteUsuarioByEmail(usuario.getEmail());
		assertTrue(true);
	}
	
}