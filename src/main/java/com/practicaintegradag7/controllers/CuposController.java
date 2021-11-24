package com.practicaintegradag7.controllers;

import java.time.LocalDateTime;
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
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.LDTFormatter;

/**
 * Se encargará de realizar la gestión de los cupos
 */
@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class CuposController {
	@Autowired
	private CentroDao aux;
	@Autowired
	private CupoDao cupodao;
	
	/**
	 * Método que se encarga de crear un cupo con la ruta configurada
	 * @param datosCupo los datos del cupo que se quiere crear en formato JSON
	 * @return el cupo ya creado
	 * @throws JSONException excepción que se lanzara si el formato no es el correcto
	 * @throws CentroNotFoundException excepción que se lanzara si no encuentra el centro para ese cupo
	 * @throws CupoExistException excepción que se lanzara si el cupo ya existe
	 */
	@PostMapping(path="/api/cupo/create")
	public Cupo crearCupos(@RequestBody Map<String, Object> datosCupo) throws JSONException, CentroNotFoundException, CupoExistException {
		JSONObject jso = new JSONObject(datosCupo);
		String fechaini =  jso.getString("fechaini");
		LocalDateTime fechainicio = LDTFormatter.parse(fechaini);
		String fecha2 = jso.getString("fechafin");
		LocalDateTime fechafin = LDTFormatter.parse(fecha2);
		int numcitas = jso.getInt("ncitas");
		Centro centro = aux.buscarCentroByNombre(jso.getString("centro"));
		Cupo cupo= new Cupo(fechainicio, fechafin, numcitas, centro.getNombre());
		cupodao.saveCupo(cupo);
		return cupo;
	}
	
	/**
	 * Método que se encarga para obtener todos los cupos
	 * @return la lista de todos los cupos
	 */
	@GetMapping(path="/api/cupo/obtener")
	public List<Cupo> obtenerCupos() {
		return cupodao.getAllCupos();
	}
}
