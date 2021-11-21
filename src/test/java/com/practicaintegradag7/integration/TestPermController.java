package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.codec.digest.DigestUtils;
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
	private final Usuario sanitario = new UsuarioBuilder()
			.dni("05718583J")
			.nombre("Francisco")
			.apellidos("Morisco Parra")
			.email("sanitario@gmail.com")
			.password("Iso+grupo7")
			.centro(null)
			.rol("sanitario")
			.build();
	private final Usuario administrador = new UsuarioBuilder()
			.dni("05718583J")
			.nombre("Francisco")
			.apellidos("Morisco Parra")
			.email("admin@gmail.com")
			.password("Iso+grupo7")
			.centro(null)
			.rol("Administrador")
			.build();
	
	@Order(1)
	@Test
	void insertUser() throws Exception{
		dao.saveUsuario(usuario);
		dao.saveUsuario(sanitario);
		dao.saveUsuario(administrador);
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
	void shouldAllowAccessInIf() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, usuario.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "appointment");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(6)
	@Test
	void shouldAllowAccessInIf1() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, usuario.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "ninguno");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(7)
	@Test
	void shouldAllowAccessInIf2() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, sanitario.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "appointment");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(8)
	@Test
	void shouldAllowAccessInIf3() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, sanitario.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "ninguno");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(9)
	@Test
	void shouldAllowAccessInIf4() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, administrador.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "appointment");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}
	
	@Order(10)
	@Test
	void shouldAllowAccessInIf5() throws Exception{
		JSONObject json = new JSONObject();
		json.put(EMAIL, administrador.getEmail());
		json.put(PWD,  DigestUtils.sha256Hex("Iso+grupo7"));
		json.put(SITE, "ninguno");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/perms/check").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertTrue(true);
	}

	@Order(11)
	@Test
	void testDelete() throws CentroNotFoundException {
		dao.deleteUsuarioByEmail(usuario.getEmail());
		dao.deleteUsuarioByEmail(sanitario.getEmail());
		dao.deleteUsuarioByEmail(administrador.getEmail());
		assertTrue(true);
	}
	
}