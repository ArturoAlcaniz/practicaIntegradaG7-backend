package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class CentroController {
	@Autowired
	private final CentroDao aux = new CentroDao();
	
	@Autowired
	private CentroDao centroDao;
	
	@PostMapping("api/addVaccines")
	public void addVacunas(@RequestBody Map<String, Object> info) throws CentroNotFoundException, VacunasNoValidasException, JSONException {
		JSONObject jso = new JSONObject(info);
		String centro = jso.getString("hospital");
		int amount = jso.getInt("amount");
		Centro c = aux.buscarCentroByNombre(centro);
		aux.addVacunas(c.getId(), amount);
	}
	
	@PostMapping("centros/create")
	public Centro crearCentro(@RequestBody Map<String, Object> datosCentro) throws JSONException, CentroExistException, CentroNotFoundException{
		JSONObject jso = new JSONObject(datosCentro);
		String nombre = jso.getString("nombre");
		String direccion = jso.getString("direccion");
		int vacunas = jso.getInt("vacunas");
		Centro centro = new Centro(nombre, direccion, vacunas);
		return centroDao.createCentro(centro);
	}
}
