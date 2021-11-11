package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.LDTFormatter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class AppointmentController{
	
	@Autowired
	private CitaDao citaDao;
	
	@PostMapping(path="/api/citas/create")
    public String crearCita() throws JSONException, CentroNotFoundException {
		try {
			JSONObject response = new JSONObject();
			List<Cita> citas = citaDao.createCitas();
			String mssg = "Primera cita asignada para el " + LDTFormatter.processLDT(citas.get(0).getFecha())+
					", segunda cita asignada el " + LDTFormatter.processLDT(citas.get(1).getFecha());
			response.put("status", "200");
			response.put("message", mssg);
			return response.toString();
		} catch (CitasUsuarioNotAvailable | CitasCupoNotAvailable e) {
			JSONObject response = new JSONObject();
			response.put("status", "500");
			response.put("message", e.getMessage());
			return response.toString();
		}
    }
	
	@GetMapping(path="/api/citas/obtener")
	public List<Cita> obtenerCitas(){
		return citaDao.getAllCitas();
	}
	
	@GetMapping(path="/api/citas/obtenerPorFechaAndCentro")
	public List<Cita> obtenerCitasPorFechaAndCentro(@RequestBody Map<String, Object> info) throws JSONException{
		JSONObject jso = new JSONObject(info);
		String centro = jso.getString("centro");
		LocalDateTime fecha = LDTFormatter.parse(jso.getString("fecha"));
		
		return citaDao.findByFechaAndCentroNombre(fecha, centro);
	}
	
}

