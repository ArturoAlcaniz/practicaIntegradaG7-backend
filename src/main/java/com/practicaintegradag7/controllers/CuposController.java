package com.practicaintegradag7.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;

public class CuposController {
	@Autowired
	private final CentroDao aux;
	@Autowired
	private final CupoDao cupodao;
	

	@PostMapping(path="api/cupo/create")
	public Cupo crearCupos(@RequestBody Map<String, Object> datosCupo) throws JSONException, CentroNotFoundException {
		JSONObject jso = new JSONObject(datosCupo);
		String fechaini =  jso.getString("fecha fin");
		LocalDateTime fechainicio = LocalDateTime.parse(fechaini);
		String fecha2 = jso.getString("fecha fin");
		LocalDateTime fechafin = LocalDateTime.parse(fecha2);
		int numcitas = jso.getInt("numero citas");
		Centro centro = aux.buscarCentroByNombre(jso.getString("centro"));
		Cupo cupo= new Cupo(fechainicio, fechafin, numcitas, centro);
		cupodao.saveCupo(cupo);
		return cupo;
		}

}
