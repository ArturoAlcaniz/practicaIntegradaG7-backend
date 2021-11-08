package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;
import com.practicaintegradag7.repos.CitaRepository;
import com.practicaintegradag7.repos.CupoRepository;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
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
		List<Usuario> usuarios = usuarioDao.getAllUsuarios();
		for(int i=0; i<usuarios.size(); i++) {
			usuarioDao.deleteUsuarioByDni(usuarios.get(i).getDni());
		}
		cupoDao.getAllCupos().forEach((p) -> { try {
			cupoDao.deleteCupo(p);
		} catch (CupoNotFoundException e) {
		} });
		Random random = new Random();
		centroPrueba = new Centro("Centro Prueba Citas "+random.nextInt(100), "Calle 1", 1);
		try {
			centroDao.createCentro(centroPrueba);
		} catch (CentroExistException e1) {
			fail("CentroExistException not expected");
		}
	}
	
	@Order(2)
	@Test
	void failWhenNotUsuariosAvailable() throws CifradoContrasenaException, CupoNotFoundException, CentroNotFoundException {
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
	void failWhenNotCuposAvailable() throws CifradoContrasenaException, CupoNotFoundException, CentroNotFoundException {
		Random random = new Random();
		String dni = random.nextInt(10)+"0"+random.nextInt(10)+"2"+random.nextInt(10)+"1"+random.nextInt(10)+"1"+"A";
		usuarioPrueba = new UsuarioBuilder()
				.dni(dni)
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba)
				.rol("paciente")
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
	void shouldSaveCita() throws CifradoContrasenaException, CupoNotFoundException {
		cupoPrueba1 = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 20, centroPrueba);
		cupoPrueba2 = new Cupo(cupoPrueba1.getFechaInicio().plusDays(21), cupoPrueba1.getFechaFin().plusDays(21), 20, centroPrueba);
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
	void failWhenCreateMoreThan2Citas() throws CupoNotFoundException, CentroNotFoundException {
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
	void zeroCitas() {
		if(citaPrueba != null && citaPruebaAlt != null) {
			citaDao.deleteCita(citaPrueba);
			citaDao.deleteCita(citaPruebaAlt);
			Assertions.assertEquals(0, citaDao.getCitasByDni(citaPrueba.getDni()).size());
		}
	}
	
	@Order(8)
	@Test
	void validSaveThenReturn200() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Order(9)
	@Test
	void findCitaByDni() {
		if(citaPrueba != null)
		assertTrue(citaDao.getCitasByDni(citaPrueba.getDni()).size() > 0);
	}
	
	@Order(10)
	@Test
	void checkCentroCita() {
		if(citaPrueba != null)
		assertEquals(citaDao.getCitasByDni(citaPrueba.getDni()).get(0).getCentroNombre(), centroPrueba.getNombre());
	}
	
	@Order(11)
	@Test
	void findUsuarioWithCitasDifferentDni() throws CifradoContrasenaException, CitasUsuarioNotAvailable, CitasCupoNotAvailable, CupoNotFoundException, CentroNotFoundException {
		Random random = new Random();
		String dni = random.nextInt(10)+"0"+random.nextInt(10)+"2"+random.nextInt(10)+"1"+random.nextInt(10)+"1"+"A";
		usuarioPrueba2 = new UsuarioBuilder()
				.dni(dni)
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centroPrueba)
				.rol("paciente")
				.build();
		usuarioPrueba2 = usuarioDao.saveUsuario(usuarioPrueba2);
		List<Cita> citas1 = citaDao.createCitas();
		List<Cita> citas2 = citaDao.createCitas();
		List<Cita> citas3 = citaDao.createCitas();
		citaPrueba3 = citas1.get(0);
		citaPrueba4 = citas1.get(1);
		citaPrueba5 = citas2.get(0);
		citaPrueba6 = citas2.get(1);
		citaPrueba7 = citas3.get(0);
		citaPrueba8 = citas3.get(1);
		assertTrue(citaDao.getAllCitas().size() > 4);
	}
	
	@Order(12)
	@Test
	void deleteCitasPrueba() {
		if(citaPrueba3 != null) citaDao.deleteCita(citaPrueba3);
		if(citaPrueba4 != null) citaDao.deleteCita(citaPrueba4);
		if(citaPrueba5 != null) citaDao.deleteCita(citaPrueba5);
		if(citaPrueba6 != null) citaDao.deleteCita(citaPrueba6);
		if(citaPrueba7 != null) citaDao.deleteCita(citaPrueba7);
		if(citaPrueba8 != null) citaDao.deleteCita(citaPrueba8);
		assertTrue(true);
	}
	
	@Order(13)
	@Test
	void deleteCitasPrueba2() {
		if(citaPrueba != null) {
			citaDao.deleteCita(citaPrueba);
		}
		if(citaPruebaAlt != null) {
			citaDao.deleteCita(citaPruebaAlt);
		}
		if(citaPrueba2 != null) {
			citaDao.deleteCita(citaPrueba2);
		}
		assertTrue(true);
	}
	
	@Order(15)
	@Test
	void presetClean() {
		try {
			if(cupoPrueba1 != null) {
				cupoDao.deleteCupo(cupoPrueba1);
			}
			if(cupoPrueba2 != null) {
				cupoDao.deleteCupo(cupoPrueba2);
			}
		} catch (CupoNotFoundException e) {
			fail("CupoNotFoundException not expected");
		}
	}
	
	@Order(16)
	@Test
	void assignAppointmentWithSecondDateAlreadyReserved() throws Exception {
		//try {
			List<Cita> ncitas = citaDao.getAllCitas();
			List<Cupo> ncupos = cupoDao.getAllCupos();
			if(ncitas.size() > 0 || ncupos.size() > 0) throw new Exception("Los repositorios no estan vacios");
			cupoPruebaTaken		= new Cupo(LocalDateTime.of(2022, 10, 22, 12, 00), LocalDateTime.of(2022, 10, 22, 12, 00).plusMinutes(15), 1, centroPrueba);
			cupoPruebaInicial	= new Cupo(LocalDateTime.of(2022, 10, 1, 12, 00), LocalDateTime.of(2022, 10, 1, 12, 00).plusMinutes(15), 20, centroPrueba);
			cupoPruebaAlt		= new Cupo(LocalDateTime.of(2022, 10, 22, 12, 15), LocalDateTime.of(2022, 10, 22, 12, 15).plusMinutes(15), 20, centroPrueba);
			cupoPruebaInicial = auxCupo.save(cupoPruebaInicial);
			cupoPruebaTaken = auxCupo.save(cupoPruebaTaken);
			cupoPruebaAlt = auxCupo.save(cupoPruebaAlt);
			citaPruebaAlt = new Cita("DNI_PRUEBA", LocalDateTime.of(2022, 10, 22, 12, 00), centroPrueba.getNombre(), (short) 1);
			citaPruebaAlt = auxCita.save(citaPruebaAlt);
			List<Cita> citas = citaDao.createCitas();
			citaPrueba2 = citas.get(0);
			citaPrueba = citas.get(1);
			assertTrue(cupoPruebaAlt.getCentro().getNombre().equals(citaPrueba.getCentroNombre()) && citaPrueba.getFecha().equals(cupoPruebaAlt.getFechaInicio()));
		/*} catch(Exception ex) {
			fail(ex.getMessage());
		}*/
	}
	
	@Order(17)
	@Test
	void deleteUsuarioPrueba() {
		if(usuarioPrueba != null) {
			usuarioDao.deleteUsuarioByDni(usuarioPrueba.getDni());
		}
		if(usuarioPrueba2 != null) {
			usuarioDao.deleteUsuarioByDni(usuarioPrueba2.getDni());
		}
		assertTrue(true);
	}
	
	@Order(18)
	@Test
	void after() {
		try {
			if(centroPrueba != null) { 
				centroDao.deleteCentro(centroPrueba);
			}
			if(cupoPruebaTaken != null) {
				cupoDao.deleteCupo(cupoPruebaTaken);
			}
			if(cupoPruebaInicial != null) {
				cupoDao.deleteCupo(cupoPruebaInicial);
			}
			if(cupoPruebaAlt != null) {
				cupoDao.deleteCupo(cupoPruebaAlt);
			}
			if(citaPrueba2 != null) {
				citaDao.deleteCita(citaPrueba2);
			}
			if(citaPrueba != null) {
				citaDao.deleteCita(citaPrueba);
			}
			if(citaPruebaAlt != null) {
				citaDao.deleteCita(citaPruebaAlt);
			}
		} catch (CentroNotFoundException e) {
			fail("CentroNotFoundException not expected");
		} catch (CupoNotFoundException e) {
			fail("CupoNotFoundException not expected");
		}
	}

}
