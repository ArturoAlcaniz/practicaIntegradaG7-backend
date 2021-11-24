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
	
	/**
	 * Tomam los datos pasados desde el frontend en la variable info, mas especificamente
	 * las horas de inicio y fin de los periodos diarios de vacunacion, las franjas por dia
	 * de vacunacion, y la capacidad de cada franja en 'citasPorFranja'.
	 * A posterior guarda la configuracion y genera las franjas con los datos.
	 * @param info
	 * Los datos del frontend, en formato JSON
	 * @return 200 OK si se ha establecido la configuracion correctamente
	 * 500 Internal Server Error si ocurre alguna excepcion
	 * @throws JSONException
	 * Si no consigue un campo por que no este, o falle el metodo por el tipo de dato
	 * @throws ConfigurationLimitException
	 * Si ya hay una configuracion guardada
	 * @throws ConfigurationTimeException
	 * Si la hora de inicio es posterior a la hora de fin
	 * @throws ConfigurationCitasFranjaException
	 * Si las citas o franjas son 0
	 */
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
	
	/**
	 * Devuelve la configuracion de la base de datos
	 * @return La configuracion de la base de datos
	 * @throws ConfigurationEmptyException
	 * Si no hay una configuracion guardada
	 */
	@GetMapping(path="/api/configuration/obtener")
	public Configuration obtenerConfiguracion() throws ConfigurationEmptyException {
		return configurationDao.obtenerConfiguration();
	}
}
