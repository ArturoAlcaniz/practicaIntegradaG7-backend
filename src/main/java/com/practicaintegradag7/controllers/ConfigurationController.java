package com.practicaintegradag7.controllers;

import java.time.LocalTime;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.ConfigurationDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.exceptions.ConfigurationCitasFranjaException;
import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.exceptions.ConfigurationTimeException;
import com.practicaintegradag7.model.Configuration;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class ConfigurationController {

	@Autowired
	private ConfigurationDao configurationDao;
	
	@Autowired
	private CupoDao cupoDao;
	
	@PostMapping(path="/api/configuracion/create")
	public String crearConfiguration(@RequestBody Map<String, Object> info) throws JSONException, ConfigurationLimitException, ConfigurationTimeException, ConfigurationCitasFranjaException {
		JSONObject jso = new JSONObject(info);
		int citasPorFranja = Integer.parseInt(jso.getString("citasPorFranja"));
		int franjasPorDia = Integer.parseInt(jso.getString("franjasPorDia"));
		LocalTime horaInicio = LocalTime.parse(jso.getString("horaInicio"));
		LocalTime horaFin = LocalTime.parse(jso.getString("horaFin"));
		Configuration configuracion = new Configuration(horaInicio, horaFin, citasPorFranja, franjasPorDia);
		configurationDao.save(configuracion);
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Ha guardado la configuracion correctamente");
		cupoDao.autogenerarFranjas(configuracion);
		return response.toString();
	}
	
	@GetMapping(path="/api/configuration/obtener")
	public Configuration obtenerConfiguracion() throws ConfigurationEmptyException {
		return configurationDao.obtenerConfiguration();
	}
}
