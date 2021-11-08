package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.ConfigurationDao;
import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.model.Configuration;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestConfigurationIntegrated {
	
	@Autowired
	private final ConfigurationDao configurationDao = new ConfigurationDao();
	
	@Autowired
	private MockMvc mockMvc;
	
	@Order(1)
	@Test
	void failWhenConfigurationAlreadySaved() throws Exception {
		configurationDao.eliminarConfiguration();
		JSONObject json = new JSONObject();
		json.put("horaInicio", "08:00");
		json.put("horaFin", "17:00");
		json.put("citasPorFranja", "20");
		json.put("franjasPorDia", "20");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/configuracion/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		try {
			LocalTime horaInicio = LocalTime.parse("08:00");
			LocalTime horaFin = LocalTime.parse("17:00");
			int citasPorFranja = 20;
			int franjasPorDia = 20;
			Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
			configurationDao.save(configuration);
		} catch(ConfigurationLimitException e) {
			assertEquals("Ya hay una configuración guardada", e.getMessage());
		}
	}
	
	@Order(2)
	@Test
	void failWhenConfigurationNotSaved() throws Exception {
		configurationDao.eliminarConfiguration();
		mockMvc.perform( MockMvcRequestBuilders.get("/api/configuracion/obtener").accept(MediaType.ALL)).andExpect(status().is4xxClientError());
		try {
			configurationDao.obtenerConfiguration();
		} catch(ConfigurationEmptyException e) {
			assertEquals("No hay una configuración guardada", e.getMessage());
		}
	}

}
