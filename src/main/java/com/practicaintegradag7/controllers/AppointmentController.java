package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
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
    public String crearCita() throws JSONException {
		try {
			JSONObject response = new JSONObject();
			List<Cita> citas = citaDao.createCitas();
			String mssg = "Primera cita asignada para el " + LDTFormatter.processLDT(citas.get(0).getFecha())+
					", segunda cita asignada el " + LDTFormatter.processLDT(citas.get(1).getFecha());
			response.put("status", "200");
			response.put("message", mssg);
			return response.toString();
		} catch (CitasUsuarioNotAvailable | CitasCupoNotAvailable | CupoNotFoundException | CentroNotFoundException e) {
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
	
	@PostMapping(path="/api/citas/modify")
    public String modificarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CitasUsuarioNotAvailable, CitasCupoNotAvailable, CitaNotModifiedException {
		JSONObject jso = new JSONObject(datosCita);
		String fechaAntigua =  jso.getString("fechaAntigua");
		String fechaNueva =  jso.getString("fechaNueva");
		LocalDateTime fechaAntiguaFormateada = LDTFormatter.parse(fechaAntigua);	
		LocalDateTime fechaNuevaFormateada = LDTFormatter.parse(fechaNueva);		
		
		String dni = jso.getString("dni");
		String centroNombre = jso.getString("centro");
		
		
		
		Cita citaAntigua = new Cita(dni, fechaAntiguaFormateada, centroNombre);
		Cita citaNueva = new Cita(dni, fechaNuevaFormateada, centroNombre);
		
		citaDao.modifyCita(citaAntigua, citaNueva);
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Ha modificado su cita correctamente para el "+ citaNueva.getFecha());
		
		
		
		
    	return response.toString();
    }
	
	@PostMapping(path="/api/citas/delete")
    public String eliminarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CitasUsuarioNotAvailable, CitasCupoNotAvailable {
		JSONObject jso = new JSONObject(datosCita);
		String fecha =  jso.getString("fecha");
		LocalDateTime fechaF = LDTFormatter.parse(fecha);
		String dni = jso.getString("dni");
		String centroNombre = jso.getString("centro");
		
		Cita cita = new Cita(dni, fechaF, centroNombre);
		citaDao.deleteCita(cita);
		
		JSONObject response = new JSONObject();
		response.put("status", "200");
		response.put("message", "Ha eliminado su cita");
    	return response.toString();
    }
}

