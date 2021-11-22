package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.practicaintegradag7.exceptions.ConfigurationCitasFranjaException;
import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.exceptions.ConfigurationTimeException;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.repos.CupoRepository;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestConfigurationIntegrated {
	
	@Autowired
	private final ConfigurationDao configurationDao = new ConfigurationDao();
	
	@Autowired
	private CupoRepository cupoRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Order(1)
	@Test
	void failWhenConfigurationAlreadySaved() throws Exception {
		configurationDao.eliminarConfiguration();
		JSONObject json = new JSONObject();
		json.put("horaInicio", "09:00");
		json.put("horaFin", "10:00");
		json.put("citasPorFranja", "10");
		json.put("franjasPorDia", "2");
		mockMvc.perform( MockMvcRequestBuilders.post("/api/configuracion/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		try {
			LocalTime horaInicio = LocalTime.parse("09:00");
			LocalTime horaFin = LocalTime.parse("10:00");
			int citasPorFranja = 10;
			int franjasPorDia = 2;
			Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
			configurationDao.save(configuration);
		} catch(ConfigurationLimitException e) {
			assertEquals("Ya hay una configuración guardada", e.getMessage());
		}
		cupoRepository.deleteAll();
		configurationDao.eliminarConfiguration();
	}
	
	@Order(2)
	@Test
	void testGetConfiguration() throws Exception {
		LocalTime horaInicio = LocalTime.parse("09:00");
		LocalTime horaFin = LocalTime.parse("10:00");
		int citasPorFranja = 10;
		int franjasPorDia = 2;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		configurationDao.save(configuration);
		
		mockMvc.perform( MockMvcRequestBuilders.get("/api/configuration/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
		assertTrue(true);
		
		cupoRepository.deleteAll();
		configurationDao.eliminarConfiguration();
	}
	
	@Order(3)
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
	
	@Order(4)
	@Test
	void failWhenCitasIncorrect() throws ConfigurationTimeException, ConfigurationLimitException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 0;
		int franjasPorDia = 1;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		try {
			configurationDao.save(configuration);
		} catch (ConfigurationCitasFranjaException e) {
			assertEquals("Valores incorrectos, citas y franjas deben ser mayor que 0", e.getMessage());
		}
	}
	
	@Order(5)
	@Test
	void failWhenFranjasIncorrect() throws ConfigurationTimeException, ConfigurationLimitException {
		LocalTime horaInicio = LocalTime.parse("08:00");
		LocalTime horaFin = LocalTime.parse("17:00");
		int citasPorFranja = 1;
		int franjasPorDia = 0;
		Configuration configuration = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		try {
			configurationDao.save(configuration);
		} catch (ConfigurationCitasFranjaException e) {
			assertEquals("Valores incorrectos, citas y franjas deben ser mayor que 0", e.getMessage());
		}
	}

}
