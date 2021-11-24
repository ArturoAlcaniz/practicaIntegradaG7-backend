package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
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
	private static final String FECHA = "fecha";
	
	/**
	 * Método para crear una cita, este método comunica la parte del front con el back
	 * @param fechaJSON, se le pasan los datos de la fecha que se quiere crear
	 * @return te devuelve un mensaje y un código para mostarlo en el front
	 * @throws JSONException excepción que se lanzará en el caso de que no esté bien formado el json
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentre ese cupo (franja horaria)
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que no se encuentren citas en ese cupo.
	 */
	@PostMapping(path="/api/citas/create")
    public String crearCita(@RequestBody Map<String, Object> fechaJSON) throws JSONException, CentroNotFoundException, CupoNotFoundException, CitaNotFoundException {
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
	
	/**
	 * Método de mensaje de confirmación para la creación de citas
	 * @param citas
	 * @return devuelve un mensaje con la información de las fechas de vacunación
	 */
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
	/**
	 * Método para obtener todas las citas de la base de datos relacionadas con el usuario
	 * @param json datos del json
	 * @return devuelve todas las citas del usuario
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que no se encuentren citas asignadas a ese usuario
	 * @throws UsuarioNotFoundException excepción que se lanzará en el caso de que no se encuentre el usuario
	 */
	@PostMapping(path="/api/citas/obtener")
	public List<Cita> obtenerCitas(@RequestBody Map<String, Object> json) throws CitaNotFoundException, UsuarioNotFoundException{
		try {
			JSONObject jso = new JSONObject(json);
			String email =  jso.getString(EMAIL);
			return citaDao.getCitasByEmail(email);
		}catch(JSONException e) {
			throw new UsuarioNotFoundException("Email no encontrado, ¿se ha logeado?");
		}
	}
	/**
	 * Método que devuelve los los cupos libres
	 * @param fechaJSON, datos del json
	 * @return lista con todos los cupos libres
	 * @throws JSONException excepción que se lanzará en el caso de que haya un problema con el json (formato)
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro.
	 */
	@PostMapping(path="/api/citas/obtenerCuposLibres")
	public List<Cupo> obtenerCuposLibres(@RequestBody Map<String, Object> fechaJSON) throws JSONException, CentroNotFoundException{
		JSONObject jso = new JSONObject(fechaJSON);
		String fecha =  jso.getString("fechaSeleccionada");
		String centroNombre = jso.getString(CENTRO);
		Centro centro = centroDao.buscarCentroByNombre(centroNombre);
		LocalDateTime fechaFormateada = LDTFormatter.parse(fecha+"T00:00:00");
		return cupoDao.getAllCuposAvailableInADay(centro, fechaFormateada);
	}
	/**
	 * Método para modificar la cita, este método enlaza el front con el back
	 * @param datosCita, datos de la cita a modificar
	 * @return mensaje para saber si se ha modificado o hay un error al modificar la cita
	 * @throws JSONException excepción que se lanzará en el caso de que haya un problema con el json (formato)
	 * @throws CitaNotModifiedException excepción que se lanzará en el caso de que haya algún problema con la modificación de la cita.
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro.
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentre el cupo.
	 */
	@PostMapping(path="/api/citas/modify")
    public String modificarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException {
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
		response.put(MSSG, "Ha modificado su cita correctamente para el "+ LDTFormatter.processLDT(citaNueva.getFecha()));
		
    	return response.toString();
    }
	
	/**
	 * Método para eliminar las citas, enlaza el front con el back
	 * @param datosCita, los datos de la cita a eliminar
	 * @return mensaje para saber si se ha eliminado o hay un error al eliminar la cita
	 * @throws JSONException excepción que se lanzará en el caso de que haya un problema con el json (formato)
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro.
	 * @throws CupoNotFoundException excepción que se lanzará en el caso de que no se encuentre el cupo
	 * @throws CupoExistException excepción que se lanzará en el caso de que el cupo exista.
	 */
	@PostMapping(path="/api/citas/delete")
    public String eliminarCita(@RequestBody Map<String, Object> datosCita) throws JSONException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		JSONObject jso = new JSONObject(datosCita);
		String fecha =  jso.getString(FECHA).replace(" a las ", "T");
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
	/**
	 * Método para marcar la vacunación como realizada
	 * @param datosVacunacion, datos de la vacunación
	 * @return mensaje de confirmación o de error, para comunicarselo al front
	 * @throws JSONException excepción que se lanzará en el caso de que haya problemas con el json.
	 * @throws CitaNotFoundException excepción que se lanzará en el caso de que no se encuentren citas.
	 * @throws VacunacionDateException excepción que se lanzará en el caso de que la fecha no coincida con la fecha de la vacunación
	 * @throws UsuarioNotFoundException excepción que se lanzará en el caso de que no se encuentre/exista el usuario
	 * @throws CentroNotFoundException excepción que se lanzará en el caso de que no se encuentre el centro.
	 * @throws CitasNotAvailableException excepción que se lanzará en el caso de que no haya citas disponibles
	 */
	@PostMapping(path="/api/marcarVacunacion")
	public String marcarVacunacion(@RequestBody Map<String, Object> datosVacunacion) throws JSONException, CitaNotFoundException, VacunacionDateException, UsuarioNotFoundException, CentroNotFoundException, CitasNotAvailableException {
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
	/**
	 * Método para obtener las citas por fecha y centro, enlaza el front con el back
	 * @param info, información del centro y las fechas de inicio y fin
	 * @return devuelve un string con las citas de ese centro y esa fecha
	 * @throws JSONException excepción que se lanzará en el caso de que haya un problema con el json
	 * @throws CifradoContrasenaException excepción que se lanzará en el caso que haya un problema con el cifrado de la contraseña.
	 */
	@PostMapping(path="/api/citas/obtenerPorFechaAndCentro")
	public String obtenerCitasPorFechaAndCentro(@RequestBody Map<String, Object> info) throws JSONException, CifradoContrasenaException{
		JSONObject jso = new JSONObject(info);
		String fechaString = jso.getString(FECHA);
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
			String email=null;
			boolean primeraDosis=false;
			boolean segundaDosis=false;
			
			for (Usuario usuario : usuarios) {
				if (cita.getEmail().equals(usuario.getEmail())) {
					nombre = usuario.getNombre();
					apellidos = usuario.getApellidos();
					dni = usuario.getDniDenc();
					email = usuario.getEmail();
					primeraDosis=usuario.isPrimeraDosis();
					segundaDosis=usuario.isSegundaDosis();
					break;
				}
			}
			
			citaUsuario.put(FECHA, cita.getFecha());
			citaUsuario.put("dni", dni);
			citaUsuario.put("nombre", nombre);
			citaUsuario.put("apellidos", apellidos);
			citaUsuario.put(NCITA, cita.getNcita());
			citaUsuario.put(EMAIL, email);
			citaUsuario.put("primeraDosis", primeraDosis);
			citaUsuario.put("segundaDosis", segundaDosis);
			
			
			citasConUsuarios.put(citaUsuario);
		}
		
		return citasConUsuarios.toString();
	}
}

