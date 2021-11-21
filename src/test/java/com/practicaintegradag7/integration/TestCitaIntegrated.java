package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.practicaintegradag7.dao.ConfigurationDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasNotAvailableException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.exceptions.ConfigurationTimeException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.VacunacionDateException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.LDTFormatter;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;
import com.practicaintegradag7.repos.CentroRepository;
import com.practicaintegradag7.repos.CitaRepository;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestCitaIntegrated {
	@Autowired
	private ConfigurationDao conf;
	
	@Autowired
	private CupoDao cupos;
	
	@Autowired
	private CentroDao centros;
	
	@Autowired
	private UsuarioDao users;
	
	@Autowired
	private CitaDao citas;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CitaRepository citaRepository; //Access chain violation, for testing only
	
	@Autowired
	CentroRepository ax;
	
	private Usuario paciente = new UsuarioBuilder()
			.dni("12345678A")
			.nombre("PRUEBA_CITAINT")
			.apellidos("A A")
			.email("PRUEBACITAINT@TESTCITAINT.COM")
			.password("Iso+grupo7")
			.centro("HOSPITAL_PRUEBACITAINT")
			.rol("Paciente")
			.build();
	private Usuario desafortunado = new UsuarioBuilder()
			.dni("12345678A")
			.nombre("PRUEBA_CITAINT_DES")
			.apellidos("A A")
			.email("DESAFORTUNADO@TESTCITAINT.COM")
			.password("Iso+grupo7")
			.centro("CENTROSINDOSIS_TESTCITAINT")
			.rol("Paciente")
			.build();
	private Centro centropru = new Centro("HOSPITAL_PRUEBACITAINT", "CALLE_PRUEBACITAINT", 200);
	private Centro centroSinDosis = new Centro("CENTROSINDOSIS_TESTCITAINT", "CALLE_PRUEBACITAINT", 0);
	private boolean clean = false;
	private Cita intrusa;
	
	@Test
	@Order(Integer.MIN_VALUE)
	void before() throws ConfigurationTimeException, ConfigurationLimitException, InterruptedException, CifradoContrasenaException {
		if(!clean) {
			System.out.println(clean+"- --------------------CLEANING UP--------------------");
			conf.eliminarConfiguration();
			cupos.deleteAllCupos();
			
			LocalTime t1 = LocalTime.parse("08:00");
			LocalTime t2 = LocalTime.parse("20:00");
			int citasXfranja = 10;
			int franjasXdia = 10;
			
			Configuration c = new Configuration(t1,t2,citasXfranja,franjasXdia);
			conf.save(c);
			
			paciente.encryptDNI();
			desafortunado.encryptDNI();
			centropru = centros.save(centropru);
			centroSinDosis = centros.save(centroSinDosis);
			paciente = users.save(paciente);
			desafortunado = users.save(desafortunado);
			
			cupos.autogenerarFranjas(c);

			this.clean = true;
		}
		assertTrue(clean);
	}
	
	@Test
	@Order(1)
	void testCrearCitas() {
		JSONObject json = new JSONObject();
		json.put("email", paciente.getEmail());
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("200"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(2)
	void testFailCrearCitas() {
		JSONObject json = new JSONObject();
		json.put("email", "nonsense@shouldfail.com");
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("500"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(3)
	void testGetCitas() {
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.get("/api/citas/obtener").
					contentType(MediaType.APPLICATION_JSON)).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains(paciente.getEmail()));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(4)
	void testGetCuposLibres() {
		JSONObject json = new JSONObject();
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		json.put("fechaSeleccionada", tomorrow.toString().substring(0, tomorrow.toString().indexOf("T")));
		json.put("centro", centropru.getNombre());
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/obtenerCuposLibres").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains(centropru.getNombre()));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(5)
	void testModifyCita() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita antigua = lcitas.get(1);
			json.put("fechaAntigua", antigua.getFecha().toString());
			antigua.setFecha(antigua.getFecha().plusDays(2));
			json.put("fechaNueva", antigua.getFecha().toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "2");
			
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("200"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(6)
	void testFailModifyCitaSameDate() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita antigua = lcitas.get(1);
			json.put("fechaAntigua", antigua.getFecha().toString());
			json.put("fechaNueva", antigua.getFecha().toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "2");
			Assertions.assertThrows(org.springframework.web.util.NestedServletException.class, () ->
				mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").contentType(MediaType.APPLICATION_JSON).
					content(json.toString())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(7)
	void testFailModifyCitaFirstDateAfterSecondOne() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita primera = lcitas.get(0);
			Cita segunda = lcitas.get(1);
			
			json.put("fechaAntigua", primera.getFecha().toString());
			json.put("fechaNueva", segunda.getFecha().plusDays(5).toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "1");
			
			Assertions.assertThrows(org.springframework.web.util.NestedServletException.class, () ->
			mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").contentType(MediaType.APPLICATION_JSON).
				content(json.toString())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(8)
	void testFailModifyCitaFirstDateLimitSurpassed() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita primera = lcitas.get(0);
			
			LocalDateTime surpassed = LocalDateTime.of(2022, 1, 11, 23, 59);
			
			json.put("fechaAntigua", primera.getFecha().toString());
			json.put("fechaNueva", surpassed.toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "1");
			
			Assertions.assertThrows(org.springframework.web.util.NestedServletException.class, () ->
			mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").contentType(MediaType.APPLICATION_JSON).
				content(json.toString())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(9)
	void testFailModifyCitaSecondDateLessThan21DaysAwayFromFirst() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita primera = lcitas.get(0);
			
			json.put("fechaAntigua", primera.getFecha().toString());
			json.put("fechaNueva", primera.getFecha().plusDays(10).toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "2");
			
			Assertions.assertThrows(org.springframework.web.util.NestedServletException.class, () ->
			mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").contentType(MediaType.APPLICATION_JSON).
				content(json.toString())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(10)
	void testFailModifyCitaSecondDateLimitSurpassed() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita segunda = lcitas.get(1);
			
			LocalDateTime surpassed = LocalDateTime.of(2022, 3, 1, 23, 59);
			
			json.put("fechaAntigua", segunda.getFecha().toString());
			json.put("fechaNueva", surpassed.toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "1");
			
			Assertions.assertThrows(org.springframework.web.util.NestedServletException.class, () ->
			mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/modify").contentType(MediaType.APPLICATION_JSON).
				content(json.toString())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(11)
	void testEliminarCita() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita segunda = lcitas.get(1);
			
			json.put("fecha", segunda.getFecha().toString());
			json.put("email", paciente.getEmail());
			json.put("centro", paciente.getCentro());
			json.put("ncita", "2");
			
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/delete").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("200"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(12)
	void testGetCitasFechaCentro() {
		JSONObject json = new JSONObject();
		try {
			List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
			Cita unica = lcitas.get(0); //la segunda fue borrada en el anterior metodo
			String fecha = unica.getFecha().toString().substring(0, unica.getFecha().toString().indexOf("T"));
			
			json.put("fecha", fecha);
			json.put("centro", paciente.getCentro());
			
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/obtenerPorFechaAndCentro").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains(fecha));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	

	
	@Order(13)
	@Test
	void testRegenerateSegundaCitaWithATwist() throws Exception {
		twist();
		JSONObject json = new JSONObject();
		json.put("email", paciente.getEmail());
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(!res.contains(LDTFormatter.processLDT(intrusa.getFecha())));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	/**
	 * Genera una cita intrusa 21 dias despues de la primera que tiene el Usuario "paciente"
	 * @throws CitaNotFoundException 
	 */
	private void twist() throws CitaNotFoundException {
		List<Cita> citaAsign = citas.getCitasByEmail(paciente.getEmail());
		Cita primera = citaAsign.get(0);
		
		intrusa = new Cita(desafortunado.getEmail(), primera.getFecha().plusDays(21), centroSinDosis.getNombre(), (short)1);
		citaRepository.save(intrusa);
	}
	
	@Order(14)
	@Test
	void shouldVacunarPrimeraDosis() throws Exception {
		rigCitas();
		JSONObject json = new JSONObject();
		try {
			json.put("email", paciente.getEmail());
			json.put("ncita", "1");
			
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/marcarVacunacion").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("200"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	private void rigCitas() throws CitaNotFoundException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		LocalDateTime t = LocalDateTime.now();
		Cita citaP = citas.findByEmailAndNcita(paciente.getEmail(), (short)1);
		Cita citaS = citas.findByEmailAndNcita(paciente.getEmail(), (short)2);
		Cita citades = citas.findByEmailAndNcita(desafortunado.getEmail(), (short)1);
		Cupo cup = cupos.getCupoByInicialDateAndCentro(citaP.getFecha(), centropru.getNombre());
		citas.deleteCita(citaS);
		citas.deleteCita(citaP);
		citaRepository.deleteByEmailAndFechaAndNcita(citades.getEmail(), citades.getFecha(), citades.getNcita());
		cup.setFechaInicio(t);
		citaP.setFecha(t);
		citaS.setFecha(t);
		citades.setFecha(t);
		citaRepository.save(citaP);
		citaRepository.save(citaS);
		citaRepository.save(citades);
		cupos.saveCupo(cup);
	}
	
	@Order(15)
	@Test
	void shouldVacunarSegundaDosis() throws Exception {
		JSONObject json = new JSONObject();
		try {
			json.put("email", paciente.getEmail());
			json.put("ncita", "2");
			
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/marcarVacunacion").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			assertTrue(res.contains("200"));
		}catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Order(16)
	@Test
	void failWhenDosisNotAvailable() throws Exception {
		if(createCitasT15()) {
			JSONObject json = new JSONObject();
			try {
				json.put("email", desafortunado.getEmail());
				json.put("ncita", "1");

				MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/marcarVacunacion").
						contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
				String res = aux.getResponse().getContentAsString();
				assertTrue(res.contains("500"));
			}catch(Exception ex) {
				fail(ex.getMessage());
			}
		}
	}
	
	@Order(17)
	@Test
	void testFailModifyCitaSecondDateLessThan21DaysAwayFromFirstFromDao() throws CitaNotFoundException {
		
		List<Cita> lcitas = citas.getCitasByEmail(paciente.getEmail());
		Cita primera = lcitas.get(0);
		
		try {
			citas.modifyCita(primera, primera);
		} catch (CitaNotModifiedException e) {
			e.getMessage();
			assertTrue(true);
		} catch (CentroNotFoundException e) {
			e.getMessage();
		} catch (CupoNotFoundException e) {
			e.getMessage();
		}
	}
	
	@Order(18)
	@Test
	void testFailWhenPacienteHasZeroCitas() {
		try {
			List<Cita> lcitas = citas.getCitasByEmail("noexiste");
			if (lcitas.size()<1) throw new CitaNotFoundException("Este usuario no tiene citas");
		} catch (CitaNotFoundException e) {
			assertEquals("Este usuario no tiene citas",e.getMessage());
		}
	}

	@Order(19)
	@Test
	void testFailWhenCitasNotAvailable() throws CentroNotFoundException, CitaNotFoundException {
			List<Cita> citasUsuario = citas.getCitasByEmail(paciente.getEmail());
			Centro centro = centros.buscarCentroByNombre(paciente.getCentro());
			centro.setVacunas(0);
			try {
				citas.vacunar(citasUsuario.get(0));
			} catch (VacunacionDateException e) {
				e.getMessage();
				assertTrue(true);
			} catch (UsuarioNotFoundException e) {
				e.getMessage();
				assertTrue(true);
			} catch (CentroNotFoundException e) {
				e.getMessage();
				assertTrue(true);
			} catch (CitasNotAvailableException e) {
				e.getMessage();
				assertTrue(true);
			}
			

	}
	
	private boolean createCitasT15() {
		JSONObject json = new JSONObject();
		json.put("email", paciente.getEmail());
		try {
			MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").
					contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
			String res = aux.getResponse().getContentAsString();
			return res.contains("200");
		}catch(Exception ex) {
			System.out.println("SETUP ERROR TEST N15: " + ex.getMessage());
			return false;
		}
	}
	
	@Order(Integer.MAX_VALUE)
	@Test
	void after() {
			citas.deleteAllCitas();
			conf.eliminarConfiguration();
			cupos.deleteAllCupos();
			
			ax.deleteByNombre(centropru.getNombre());
			ax.deleteByNombre(centroSinDosis.getNombre());
			
			users.deleteUsuarioByEmail(paciente.getEmail());
			users.deleteUsuarioByEmail(desafortunado.getEmail());
			assertTrue(true);
	}
}
