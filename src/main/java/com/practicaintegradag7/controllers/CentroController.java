package com.practicaintegradag7.controllers;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotEmptyException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class CentroController {
	@Autowired
	private CentroDao centroDao;
	
	@Autowired
	private CentroDao aux;
	
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	
	@PostMapping(path="/api/addVaccines")
	public void addVacunas(@RequestBody Map<String, Object> info) throws CentroNotFoundException, VacunasNoValidasException, JSONException {
		JSONObject jso = new JSONObject(info);
		String centro = jso.getString("hospital");
		int amount = jso.getInt("amount");
		Centro c = centroDao.buscarCentroByNombre(centro);
		centroDao.addVacunas(c.getId(), amount);
	}
	
	@PostMapping(path="/api/centros/create")
	public Centro crearCentro(@RequestBody Map<String, Object> datosCentro) throws JSONException, CentroExistException{
		JSONObject jso = new JSONObject(datosCentro);
		String nombre = jso.getString("nombre");
		String direccion = jso.getString("direccion");
		int vacunas = jso.getInt("vacunas");
		Centro centro = new Centro(nombre, direccion, vacunas);
		return centroDao.createCentro(centro);
	}
	
	@GetMapping(path="/api/centros/obtener")
	public List<Centro> obtenerCentros(){
		return centroDao.getAllCitas();
	}
	
	@PostMapping(path="api/centros/eliminar")
	public String eliminarCentro(@RequestBody Map<String, Object> emailJSON) throws JSONException, CentroNotFoundException, CupoNotFoundException, CentroNotEmptyException{
		JSONObject jso = new JSONObject(emailJSON);
		String nombreCentro =  jso.getString("nombreCentro");
		
		centroDao.deleteCentroWithNoUsers(nombreCentro);
		
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Ha eliminado correctamente el centro "+nombreCentro);
    	return response.toString();
	}
	
	@PostMapping(path="/api/centro/modify")
	public String modificarCentro(@RequestBody Map<String, Object> datosMCentro) throws JSONException, CentroNotFoundException, CentroExistException{
		
		JSONObject jso = new JSONObject(datosMCentro);
		String code = "200";
		String mssg = "OK";
		try {
			String nombre = jso.getString("nombre");
			String direccion = jso.getString("direccion");
			int vacunas = jso.getInt("vacunas");
			aux.modificarCentro(nombre, direccion, vacunas);
		}catch(JSONException e) {
			code = "500";
			mssg = e.getMessage();
		}
		
		JSONObject response = new JSONObject();
		response.put(STATUS, code);
		response.put(MSSG, mssg);
		
		return response.toString();
	
	}
}
