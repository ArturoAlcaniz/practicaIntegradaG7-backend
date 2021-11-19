package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.LDTFormatter;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;
import com.practicaintegradag7.repos.CitaRepository;
import com.practicaintegradag7.repos.CupoRepository;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestCitaIntegrated {

	@Autowired
	private final CitaDao citaDao = new CitaDao();
	
	@Autowired
	private final UsuarioDao usuarioDao = new UsuarioDao();
	
	@Autowired
	private final CupoDao cupoDao = new CupoDao();
	
	@Autowired
	private final CentroDao centroDao = new CentroDao();
	
	@Autowired
	CupoRepository auxCupo; //Access chain violation, for testing purpouses only
	
	@Autowired
	CitaRepository auxCita; //Access chain violation, for testing purpouses only
	
	@Autowired
	private MockMvc mockMvc;
	
	private static Cita citaPrueba;
	private static Cita citaPruebaAlt;
	private static Cita citaPrueba2;
	private static Cita citaPrueba3;
	private static Cita citaPrueba4;
	private static Cita citaPrueba5;
	private static Cita citaPrueba6;
	private static Cita citaPrueba7;
	private static Cita citaPrueba8;
	private static Usuario usuarioPrueba;
	private static Usuario usuarioPrueba2;
	private static Centro centroPrueba;
	private static Cupo cupoPrueba1;
	private static Cupo cupoPrueba2;
	private static Cupo cupoPruebaTaken;
	private static Cupo cupoPruebaInicial;
	private static Cupo cupoPruebaAlt;
	
	@Order(1)
	@Test
	void before() {
		usuarioDao.deleteAllUsuarios();
		cupoDao.deleteAllCupos();
		citaDao.deleteAllCitas();
		centroPrueba = new Centro("Centro Prueba Citas", "Calle 1", 1);
			try {
				centroDao.createCentro(centroPrueba);
			} catch (CentroExistException e) {}
		assertTrue(true);
	}
	
	@Order(2)
	@Test
	void failWhenNotUsuariosAvailable() throws CifradoContrasenaException, CupoNotFoundException, CentroNotFoundException, CupoExistException, CitaNotFoundException {
		try {
			citaDao.createCitas();
		} catch (CitasUsuarioNotAvailable e) {
			assertEquals("Todos los usuarios tienen el maximo de citas", e.getMessage());
		} catch (CitasCupoNotAvailable e) {
			fail("CitasUsuarioNotAvailable expected");
		}
	}
	
	@Order(3)
	@Test
	void failWhenNotCuposAvailable() throws CifradoContrasenaException, CupoNotFoundException, CentroNotFoundException, CupoExistException, CitaNotFoundException {
		String dni = "11336678A";
		usuarioPrueba = new UsuarioBuilder()
				.dni(dni)
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba.getNombre())
				.rol("Paciente")
				.build();
		try {
			usuarioPrueba = usuarioDao.saveUsuario(usuarioPrueba);
			citaDao.createCitas();
		} catch (CitasUsuarioNotAvailable e) {
			fail("CitasCupoNotAvailable expected");
		} catch (CitasCupoNotAvailable e) {
			assertEquals("No hay cupos disponibles", e.getMessage());
		} catch (CifradoContrasenaException e) {
			fail("CitasCupoNotAvailable expected");
		}
	}

	@Order(4)
	@Test
	void shouldSaveCita() throws CifradoContrasenaException, CupoNotFoundException, CitaNotFoundException {
		cupoPrueba1 = new Cupo(LocalDateTime.of(2021, 10, 20, 12, 00), LocalDateTime.of(2021, 10, 20, 12, 00).plusMinutes(15), 20, centroPrueba.getNombre());
		cupoPrueba2 = new Cupo(cupoPrueba1.getFechaInicio().plusDays(22), cupoPrueba1.getFechaFin().plusDays(22), 20, centroPrueba.getNombre());
		try {
			cupoPrueba1 = cupoDao.saveCupo(cupoPrueba1);
			cupoPrueba2 = cupoDao.saveCupo(cupoPrueba2);
			List<Cita> citas = citaDao.createCitas();
			citaPrueba = citas.get(0);
			citaPruebaAlt = citas.get(1);
			assertTrue(citas.size() > 0);
		} catch (CitasUsuarioNotAvailable e) {
			fail("CitasUsuarioNotAvailable not expected");
		} catch (CitasCupoNotAvailable e) {
			fail("CitasCuposNotAvailable not expected");
		} catch (CentroNotFoundException e) {
			fail("CentroNotFoundException not expected");
		} catch (CupoExistException e) {
			fail("CupoExistException not expected");
		}
	}
	
	@Order(5)
	@Test
	void findAllCitas() {
		Assertions.assertTrue(citaDao.getAllCitas().size() > 0);
	}
	
	@Order(6)
	void failWhenCreateMoreThan2Citas() throws CupoNotFoundException, CentroNotFoundException, CupoExistException, CifradoContrasenaException, CitaNotFoundException {
		try {
			citaDao.createCitas();
			citaDao.createCitas();
		} catch (CitasUsuarioNotAvailable e) {
			assertTrue(true);
		} catch (CitasCupoNotAvailable e) {
			fail("CitasCupoNotAvailbale not expected");
		}
	}
	
	@Order(7)
	@Test
	void zeroCitas() throws CentroNotFoundException, CupoNotFoundException, CupoExistException, CitaNotFoundException {
		if(citaPrueba != null && citaPruebaAlt != null) {
			citaDao.deleteCitaModificar(citaPrueba);
			citaDao.deleteCitaModificar(citaPruebaAlt);
			Assertions.assertEquals(0, citaDao.getCitasByEmail(citaPrueba.getEmail()).size());
		}
	}
	
	
	@Order(8)
	@Test
	void validSaveThenReturn200() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Order(9)
	@Test
	void findCitaByDni() throws CitaNotFoundException {		
		assertTrue(citaDao.getCitasByEmail(citaPrueba.getEmail()).size() > 0);
	}
	
	@Order(10)
	@Test
	void checkCentroCita() throws CitaNotFoundException {
		if(citaPrueba != null)
			assertEquals(citaDao.getCitasByEmail(citaPrueba.getEmail()).get(0).getCentroNombre(), centroPrueba.getNombre());
	}
	
	@Order(11)
	@Test
	void findUsuarioWithCitasDifferentEmail() throws CifradoContrasenaException, CitasUsuarioNotAvailable, CitasCupoNotAvailable, CupoNotFoundException, CentroNotFoundException, CupoExistException, CitaNotFoundException {
		usuarioDao.deleteAllUsuarios();
		citaDao.deleteAllCitas();
		usuarioPrueba = new UsuarioBuilder()
				.dni("05718581X")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba.getNombre())
				.rol("paciente")
				.build();
		usuarioPrueba = usuarioDao.saveUsuario(usuarioPrueba);
		usuarioPrueba2 = new UsuarioBuilder()
				.dni("05718581F")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBraseroDos@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba.getNombre())
				.rol("paciente")
				.build();
		usuarioPrueba2 = usuarioDao.saveUsuario(usuarioPrueba2);
		List<Cita> citas1 = citaDao.createCitas();
		List<Cita> citas2 = citaDao.createCitas();
		
		citaPrueba3 = citas1.get(0);
		citaPrueba4 = citas1.get(1);
		citaPrueba5 = citas2.get(0);
		citaPrueba6 = citas2.get(1);
		assertTrue(citaDao.getAllCitas().size() > 3);
	}
	
	@Order(12)
	@Test
	void shouldModifyCita() throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		
		Centro centro = centroDao.buscarCentroByNombre(citaPrueba.getCentroNombre());
		
		Cupo cupo = new Cupo(LocalDateTime.of(2021, 11, 10, 0, 0), LocalDateTime.of(2021, 11, 10, 1, 0), 5, centro.getNombre());
		cupoDao.saveCupo(cupo);
		citaPrueba2 = new Cita(citaPrueba.getEmail(), cupo.getFechaInicio(), citaPrueba.getCentroNombre(), citaPrueba.getNcita());
			
		assertTrue(citaDao.modifyCita(citaPrueba, citaPrueba2));
	}
	
	@Order(13)
	@Test
	void shouldNotModifyCitaIfEqual() throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
	
		citaPrueba2 = new Cita(citaPrueba.getEmail(), citaPrueba.getFecha(), citaPrueba.getCentroNombre(), citaPrueba.getNcita());
		
		try {
			citaDao.modifyCita(citaPrueba, citaPrueba2);
		} catch (CitaNotModifiedException e) {
			assertEquals("Debe insertar una fecha distinta a la antigua", e.getMessage());
		}
		

	}
	
	@Order(14)
	@Test
	void shouldControlFirstCita() throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = centroDao.buscarCentroByNombre(citaPrueba.getCentroNombre());
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 11, 10, 0, 0), LocalDateTime.of(2022, 11, 10, 1, 0), 5, centro.getNombre());
		cupoDao.saveCupo(cupo); 
		
		citaPrueba2 = new Cita(citaPrueba.getEmail(), LocalDateTime.of(2022, 11, 10, 0, 0), citaPrueba.getCentroNombre(), citaPrueba.getNcita());
		
	
		try {
			citaDao.modifyCita(citaPrueba, citaPrueba2);
		} catch (CitaNotModifiedException e) {
			assertEquals("La fecha de la primera cita no puede ser posterior a la segunda (2021-11-11T12:00)", e.getMessage());
		}

	}
	
	@Order(15)
	@Test
	void shouldControlSecondCita() throws CentroNotFoundException, CupoExistException, CupoNotFoundException {
		
		Centro centro = centroDao.buscarCentroByNombre(citaPrueba.getCentroNombre());
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 1, 1, 0, 0), LocalDateTime.of(2022, 1, 1, 1, 0), 5, centro.getNombre());
		Cupo cupo2 = new Cupo(LocalDateTime.of(2022, 1, 2, 0, 0), LocalDateTime.of(2022, 1, 2, 1, 0), 5, centro.getNombre());
		cupoDao.saveCupo(cupo); 
		cupoDao.saveCupo(cupo2); 
		citaPrueba3 = new Cita(citaPrueba.getEmail(), cupo.getFechaInicio(), citaPrueba.getCentroNombre(), Short.parseShort("2"));
		citaPrueba2 = new Cita(citaPrueba.getEmail(), cupo2.getFechaInicio(), citaPrueba.getCentroNombre(), Short.parseShort("2"));
		
	
		try {
			citaDao.modifyCita(citaPrueba2, citaPrueba3);
		} catch (CitaNotModifiedException e) {
			assertEquals("La fecha de la segunda cita no puede ser posterior a la primera", e.getMessage());
		}
		}
		
		@Order(16)
		@Test
		void shouldDeleteFirstCita() throws CentroNotFoundException, CupoNotFoundException, CupoExistException, CitaNotFoundException {
		
			List <Cita> citas = citaDao.getCitasByEmail(usuarioPrueba2.getEmail());
			Cita cita1 = citas.get(0);
			citaDao.deleteCita(cita1);
			
			citas = citaDao.getCitasByEmail(usuarioPrueba2.getEmail());
			cita1 = citas.get(0);
			
			assertEquals(cita1.getNcita(), Short.valueOf("1"));
			citaDao.deleteCita(citas.get(0));
		}
		
		@Order(17)
		@Test
		void shouldDeleteSecondCita() throws CentroNotFoundException, CupoNotFoundException, CupoExistException, CitasUsuarioNotAvailable, CitasCupoNotAvailable, NumberFormatException, CitaNotFoundException, CifradoContrasenaException {
		
			List <Cita> citas = citaDao.createCitas();
			Cita cita2 = citaDao.findByEmailAndNcita(usuarioPrueba2.getEmail(), Short.valueOf("2"));
			citaDao.deleteCita(cita2);
			citas = citaDao.getCitasByEmail(usuarioPrueba2.getEmail());
			assertEquals(1, citas.size());
		}
		
	
	@Order(18)
	@Test
	void deleteCitasPrueba() throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		if(citaPrueba3 != null) citaDao.deleteCitaModificar(citaPrueba3);
		if(citaPrueba4 != null) citaDao.deleteCitaModificar(citaPrueba4);
		if(citaPrueba5 != null) citaDao.deleteCitaModificar(citaPrueba5);
		if(citaPrueba6 != null) citaDao.deleteCitaModificar(citaPrueba6);
		if(citaPrueba7 != null) citaDao.deleteCitaModificar(citaPrueba7);
		if(citaPrueba8 != null) citaDao.deleteCitaModificar(citaPrueba8);
		assertTrue(true);
	}
	
	@Order(19)
	@Test
	void deleteCitasPrueba2() throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		citaDao.deleteAllCitas();
		assertTrue(true);
	}
	
	@Order(20)
	@Test
	void presetClean() {
		cupoDao.deleteAllCupos();
		assertTrue(true);
	}
	
	@Order(21)
	@Test
	void assignAppointmentWithSecondDateAlreadyReserved() throws Exception {
		try {
			cupoDao.deleteAllCupos();
			citaDao.deleteAllCitas();
			List<Cita> ncitas = citaDao.getAllCitas();
			List<Cupo> ncupos = cupoDao.getAllCupos();
			if(ncitas.size() > 0 || ncupos.size() > 0) throw new Exception("Los repositorios no estan vacios");
			cupoPruebaTaken		= new Cupo(LocalDateTime.of(2022, 10, 22, 12, 00), LocalDateTime.of(2022, 10, 22, 12, 00).plusMinutes(15), 1, centroPrueba.getNombre());
			cupoPruebaInicial	= new Cupo(LocalDateTime.of(2022, 10, 1, 12, 00), LocalDateTime.of(2022, 10, 1, 12, 00).plusMinutes(15), 20, centroPrueba.getNombre());
			cupoPruebaAlt		= new Cupo(LocalDateTime.of(2022, 10, 22, 12, 15), LocalDateTime.of(2022, 10, 22, 12, 15).plusMinutes(15), 20, centroPrueba.getNombre());
			cupoPruebaInicial = auxCupo.save(cupoPruebaInicial);
			cupoPruebaTaken = auxCupo.save(cupoPruebaTaken);
			cupoPruebaAlt = auxCupo.save(cupoPruebaAlt);
			citaPruebaAlt = new Cita("DNI_PRUEBA", LocalDateTime.of(2022, 10, 22, 12, 00), centroPrueba.getNombre(), (short) 1);
			citaPruebaAlt = auxCita.save(citaPruebaAlt);
			List<Cita> citas = citaDao.createCitas();
			citaPrueba2 = citas.get(0);
			citaPrueba = citas.get(1);
			cupoDao.deleteAllCuposByCentro(centroPrueba.getNombre());
			assertTrue(cupoPruebaAlt.getCentro().equals(citaPrueba.getCentroNombre()) && citaPrueba.getFecha().equals(cupoPruebaAlt.getFechaInicio()));
		} catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Order(22)
	@Test
	void deleteUsuarioPrueba() {
		if(usuarioPrueba != null) {
			usuarioDao.deleteUsuarioByEmail(usuarioPrueba.getEmail());
		}
		if(usuarioPrueba2 != null) {
			usuarioDao.deleteUsuarioByEmail(usuarioPrueba2.getEmail());
		}
		assertTrue(true);
	}
	
	@Order(23)
	@Test
	void after() throws CupoNotFoundException, CupoExistException {
		citaDao.deleteAllCitas();
		assertTrue(true);
	}
	
	@Order(24)
	@Test
	void expectedCrearCitaException() throws Exception {
		MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").accept(MediaType.ALL)).andReturn();
		String res = aux.getResponse().getContentAsString();
		assertTrue(res.contains("500"));
	}
	
	@Order(25)
	@Test
	void deleteCentroMalo() throws CentroNotFoundException {
		centroDao.deleteCentro(centroPrueba);
		assertTrue(true);
	}
	
	@Order(26)
	@Test
	void shouldVacunarPrimeraDosis() throws Exception {
		centroPrueba = new Centro("Centro Prueba Citas", "Calle 1", 1);
		centroDao.createCentro(centroPrueba);
		usuarioPrueba = new UsuarioBuilder()
				.dni("12345678A")
				.nombre("Prueba")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba.getNombre())
				.rol("Paciente")
				.build();
		usuarioDao.deleteUsuarioByEmail(usuarioPrueba.getEmail());
		usuarioDao.saveUsuario(usuarioPrueba);
		citaDao.deleteAllCitas();
		Cupo cupo = new Cupo(LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(10), 1, centroPrueba.getNombre());
		cupoDao.saveCupo(cupo);
		Cita cita = new Cita(usuarioPrueba.getEmail(), cupo.getFechaInicio(), centroPrueba.getNombre(), (short) 1);
		citaDao.saveCita(cita);
		JSONObject json = new JSONObject();
		json.put("email", usuarioPrueba.getEmail());
		json.put("ncita", cita.getNcita());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/marcarVacunacion").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		usuarioDao.deleteUsuarioByEmail(usuarioPrueba.getEmail());
		centroDao.deleteCentro(centroPrueba);
	}
	
	@Order(27)
	@Test
	void shouldVacunarSegundaDosis() throws Exception {
		centroPrueba = new Centro("Centro Prueba Citas", "Calle 1", 1);
		centroDao.createCentro(centroPrueba);
		usuarioPrueba = new UsuarioBuilder()
				.dni("12345678A")
				.nombre("Prueba")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba.getNombre())
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuarioPrueba);
		Cupo cupo = new Cupo(LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(10), 2, centroPrueba.getNombre());
		cupoDao.saveCupo(cupo);
		Cita cita = new Cita(usuarioPrueba.getEmail(), cupo.getFechaInicio(), centroPrueba.getNombre(), (short) 2);
		citaDao.saveCita(cita);
		JSONObject json = new JSONObject();
		json.put("email", usuarioPrueba.getEmail());
		json.put("ncita", cita.getNcita());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/marcarVacunacion").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		usuarioDao.deleteUsuarioByEmail(usuarioPrueba.getEmail());
		try {
			cupoDao.deleteAllCuposByCentro(centroPrueba.getNombre());
		} catch (CentroNotFoundException e) {}
		centroDao.deleteCentro(centroPrueba);
	}
	
	@Order(28)
	@Test
	void failWhenNotGetCitasByCentroAndAllDay() {
		citaDao.findByFechaAndCentroNombre(LDTFormatter.parse("2021-10-16T00:00"), LDTFormatter.parse("2022-01-31T00:00"), "Centro Prueba Test 26");
		assertTrue(true);
	}
}
