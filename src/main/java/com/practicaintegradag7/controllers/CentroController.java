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

/**
 * 
 * Clase para gestionar las peticiones relacionadas con los centros
 *
 */
@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class CentroController {
	@Autowired
	private CentroDao centroDao;
	
	@Autowired
	private CentroDao aux;
	
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	
	/**
	 * Metodo para añadir vacunas
	 * @param info la información del centro al que se van a añadir las vacunas
	 * @throws CentroNotFoundException Excepción que se lanzará cuando no se encuentre el centro
	 * @throws VacunasNoValidasException Excepción que se lanzará cuando se introduzca un número de vacunas inferior a 0
	 * @throws JSONException Excepción que se lanzará cuando surjan probelmas al crear el objeto JSON.
	 */
	@PostMapping(path="/api/addVaccines")
	public void addVacunas(@RequestBody Map<String, Object> info) throws CentroNotFoundException, VacunasNoValidasException, JSONException {
		JSONObject jso = new JSONObject(info);
		String centro = jso.getString("hospital");
		int amount = jso.getInt("amount");
		Centro c = centroDao.buscarCentroByNombre(centro);
		centroDao.addVacunas(c.getId(), amount);
	}
	
	/**
	 * Metodo para crear un centro
	 * @param datosCentro los datos del centro en formato json
	 * @return devuelve un mensaje en formato String
	 * @throws JSONException Excepción que se lanzará cuando surjan problemas al leer el json
	 * @throws CentroExistException Excepción que se lanzará cuando el centro no exista
	 */
	@PostMapping(path="/api/centros/create")
	public String crearCentro(@RequestBody Map<String, Object> datosCentro) throws JSONException, CentroExistException{
		
		JSONObject jso = new JSONObject(datosCentro);
		String code = "200";
		String mssg = "OK";
		
		String nombre = jso.getString("nombre");
		String direccion = jso.getString("direccion");
		int vacunas = jso.getInt("vacunas");
		Centro centro = new Centro(nombre, direccion, vacunas);
		centroDao.createCentro(centro);

		JSONObject response = new JSONObject();
		response.put(STATUS, code);
		response.put(MSSG, mssg);
		
		return response.toString();
	}
	
	/**
	 * Metodo para obtener todos los centros de la BBDD
	 * @return lista con los centros existentes 
	 */
	@GetMapping(path="/api/centros/obtener")
	public List<Centro> obtenerCentros(){
		return centroDao.getAllCitas();
	}
	
	/**
	 * Metodo para eliminar un centro que no contenga usuarios
	 * @param emailJSON el jSON con el nombre del centro
	 * @return string con el resultado del metodo para mostrar en el frontend
	 * @throws JSONException Excepción que saltará si hay problemas al crear el JSON
	 * @throws CentroNotFoundException Excepción que saltará si el centro no se encuentra
	 * @throws CupoNotFoundException Excepción que saltará si algún cupo no se encuentra
	 * @throws CentroNotEmptyException Excepción que saltara si el centro a eliminar no está vacío
	 */
	@PostMapping(path="api/centros/eliminar")
	public String eliminarCentro(@RequestBody Map<String, Object> emailJSON) throws JSONException, CentroNotFoundException, CupoNotFoundException, CentroNotEmptyException{
		JSONObject jso = new JSONObject(emailJSON);
		String nombreCentro =  jso.getString("nombreCentro");
		
		centroDao.deleteCentroWithNoUsers(nombreCentro);
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Ha eliminado correctamente el centro "+nombreCentro);
    	return response.toString();
	}
	
	/**
	 * Metodo para modificar un centro
	 * @param datosMCentro los nuevos datos para el centro
	 * @return String para devolver el mensaje con el resultado de la petición
	 * @throws JSONException Excepción relativa a problemas con el JSON
	 * @throws CentroNotFoundException Excepción que se lanzará si el centro no se encuentra
	 * @throws CentroExistException Excepción que se lanzará si el centro ya existe
	 */
	@PostMapping(path="/api/centro/modify")
	public String modificarCentro(@RequestBody Map<String, Object> datosMCentro) throws JSONException, CentroNotFoundException, CentroExistException{
		
		JSONObject jso = new JSONObject(datosMCentro);
		String code = "200";
		String mssg = "OK";
		try {
			String nombre = jso.getString("nombre");
			String direccion = jso.getString("direccion");
			int vacunas = Integer.parseInt(jso.getString("vacunas"));
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
