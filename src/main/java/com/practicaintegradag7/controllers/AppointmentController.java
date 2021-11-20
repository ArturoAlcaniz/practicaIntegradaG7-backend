package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasNotAvailableException;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.VacunacionDateException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.LDTFormatter;
import com.practicaintegradag7.model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class AppointmentController{
	 
	@Autowired
	private CitaDao citaDao;
	@Autowired
	private CupoDao cupoDao;
	@Autowired
	private CentroDao centroDao;
	@Autowired
	private UsuarioDao usuarioDao;
	
	private static final String STATUS = "status";
	private static final String MSSG = "message";
	private static final String CENTRO = "centro";
	private static final String EMAIL = "email";
	private static final String NCITA = "ncita";
	
	@PostMapping(path="/api/citas/create")
    public String crearCita(@RequestBody Map<String, Object> fechaJSON) throws JSONException, CentroNotFoundException, CupoNotFoundException, CupoExistException, CitaNotFoundException {
		JSONObject response = new JSONObject();
		String mssg = "";
		String status = "";
		
		try {
			JSONObject jso = new JSONObject(fechaJSON);
			String email =  jso.getString(EMAIL);
			Usuario user = usuarioDao.getUsuarioByEmail(email);
			
			List<Cita> citas = citaDao.createCitas(user);
			mssg = doConfirmMessage(citas);
			status = "200";
		} catch (CitasUsuarioNotAvailable | CitasCupoNotAvailable | UsuarioNotFoundException e) {
			status = "500";
			mssg = e.getMessage();
		}
		response.put(STATUS, status);
		response.put(MSSG, mssg);
		
		return response.toString();
    }
	
	private String doConfirmMessage(List<Cita> citas) {
		String mssg;
		try {
			mssg = "Primera cita asignada para el " + LDTFormatter.processLDT(citas.get(0).getFecha())+
					", segunda cita asignada el " + LDTFormatter.processLDT(citas.get(1).getFecha());
		}catch(IndexOutOfBoundsException e) {
			mssg = "Nueva segunda cita asignada para el " + LDTFormatter.processLDT(citas.get(0).getFecha());
		}
		return mssg;
	}

	@GetMapping(path="/api/citas/obtener")
	public List<Cita> obtenerCitas(){
		return citaDao.getAllCitas();
	}
	
	@PostMapping(path="/api/citas/obtenerCuposLibres")
	public List<Cupo> obtenerCuposLibres(@RequestBody Map<String, Object> fechaJSON) throws JSONException, CentroNotFoundException{
		JSONObject jso = new JSONObject(fechaJSON);
		String fecha =  jso.getString("fechaSeleccionada");
		String centroNombre = jso.getString(CENTRO);
		Centro centro = centroDao.buscarCentroByNombre(centroNombre);
		LocalDateTime fechaFormateada = LDTFormatter.parse(fecha+"T00:00:00");
		return cupoDao.getAllCuposAvailableInADay(centro, fechaFormateada);
	}
	
	@PostMapping(path="/api/citas/modify")
    public String modificarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		JSONObject jso = new JSONObject(datosCita);
		String fechaAntigua =  jso.getString("fechaAntigua");
		String fechaNueva =  jso.getString("fechaNueva");
		LocalDateTime fechaAntiguaFormateada = LDTFormatter.parse(fechaAntigua);	
		LocalDateTime fechaNuevaFormateada = LDTFormatter.parse(fechaNueva);		
		
		String email = jso.getString(EMAIL);
		String centroNombre = jso.getString(CENTRO);
		short ncita = Short.parseShort(jso.getString(NCITA));
		
		Cita citaAntigua = new Cita(email, fechaAntiguaFormateada, centroNombre, ncita);
		Cita citaNueva = new Cita(email, fechaNuevaFormateada, centroNombre, ncita);
		
		citaDao.modifyCita(citaAntigua, citaNueva);
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Ha modificado su cita correctamente para el "+ citaNueva.getFecha());
		
    	return response.toString();
    }
	
	
	@PostMapping(path="/api/citas/delete")
    public String eliminarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		JSONObject jso = new JSONObject(datosCita);
		String fecha =  jso.getString("fecha").replace(" a las ", "T");
		LocalDateTime fechaF = LDTFormatter.parse(fecha);
		String centroNombre = jso.getString(CENTRO);
		String email = jso.getString(EMAIL);
		
		short ncita = Short.parseShort(jso.getString(NCITA));
		Cita cita = new Cita(email, fechaF, centroNombre, ncita);
		citaDao.deleteCita(cita);
		
		JSONObject response = new JSONObject();
		response.put(STATUS, "200");
		response.put(MSSG, "Ha eliminado su cita");
    	return response.toString();
    }
	
	@PostMapping(path="/api/marcarVacunacion")
	public String marcarVacunacion(@RequestBody Map<String, Object> datosVacunacion) throws JSONException, CitaNotFoundException, VacunacionDateException, UsuarioNotFoundException, CifradoContrasenaException, CentroNotFoundException, CupoNotFoundException, CupoExistException, CitasNotAvailableException {
		JSONObject jso = new JSONObject(datosVacunacion);
		JSONObject response = new JSONObject();
		
		String email = jso.getString(EMAIL);
		short ncita = (short) jso.getInt(NCITA);
		Centro centro = centroDao.buscarCentroByNombre(usuarioDao.getUsuarioByEmail(email).getCentro());
		if(centro.getVacunas() > 0) {
			citaDao.vacunar(citaDao.findByEmailAndNcita(email, ncita));
			centro.setVacunas(centro.getVacunas()-1);
			centroDao.save(centro);
			response.put(STATUS,  "200");
			response.put(MSSG, "Paciente vacunado correctamente");
		} else {
			response.put(STATUS,  "500");
			response.put(MSSG, "No hay vacunas suficientes");
		}
		return response.toString();
	}
	
	@PostMapping(path="/api/citas/obtenerPorFechaAndCentro")
	public String obtenerCitasPorFechaAndCentro(@RequestBody Map<String, Object> info) throws JSONException, CifradoContrasenaException{
		JSONObject jso = new JSONObject(info);
		String fechaString = jso.getString("fecha");
		String centro = jso.getString(CENTRO);
		LocalDateTime fechaMin = LDTFormatter.parse(fechaString+"T00:00");
		LocalDateTime fechaMax = LDTFormatter.parse(fechaString+"T23:59");
		
		List<Cita> citas = citaDao.findByFechaAndCentroNombre(fechaMin,fechaMax, centro);
		List<String> emails = new ArrayList<>();
		
		for (Cita cita : citas) {
			emails.add(cita.getEmail());
		}
		
		List<Usuario>usuarios = usuarioDao.getAllByEmail(emails);
		
		JSONArray citasConUsuarios = new JSONArray();
		
		
		for (Cita cita : citas) {
			JSONObject citaUsuario = new JSONObject();
			
			String nombre=null;
			String apellidos=null;
			String dni=null;
			
			for (Usuario usuario : usuarios) {
				if (cita.getEmail().equals(usuario.getEmail())) {
					nombre = usuario.getNombre();
					apellidos = usuario.getApellidos();
					dni = usuario.getDniDenc();
					break;
				}
			}
			
			citaUsuario.put("fecha", cita.getFecha());
			citaUsuario.put("dni", dni);
			citaUsuario.put("nombre", nombre);
			citaUsuario.put("apellidos", apellidos);
			citaUsuario.put(NCITA, cita.getNcita());
			
			citasConUsuarios.put(citaUsuario);
		}
		
		return citasConUsuarios.toString();
	}
}

