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
	private MockMvc mockMvc;
	
	private static Cita citaPrueba;
	private static Usuario usuarioPrueba;
	private static Centro centroPrueba;
	private static Cupo cupoPrueba;
	
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
	void failWhenNotUsuariosAvailable() throws CifradoContrasenaException {
		try {
			citaDao.createCita();
		} catch (CitasUsuarioNotAvailable e) {
			assertEquals("Todos los usuarios tienen el maximo de citas", e.getMessage());
		} catch (CitasCupoNotAvailable e) {
			fail("CitasUsuarioNotAvailable expected");
		}
	}
	
	@Order(3)
	@Test
	void failWhenNotCuposAvailable() {
		Random random = new Random();
		String dni = random.nextInt(10)+"0"+random.nextInt(10)+"2"+random.nextInt(10)+"1"+random.nextInt(10)+"1"+"A";
		System.out.println(centroPrueba.getNombre());
		usuarioPrueba = new Usuario(dni, "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centroPrueba, "paciente");
		try {
			usuarioDao.saveUsuario(usuarioPrueba);
			citaDao.createCita();
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
	void shouldSaveCita() throws CifradoContrasenaException {
		cupoPrueba = new Cupo(LocalDateTime.of(2022, 10, 20, 12, 00), LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15), 10, centroPrueba);
		try {
			cupoPrueba = cupoDao.saveCupo(cupoPrueba);
			citaPrueba = citaDao.createCita();
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
	@Test
	void zeroCitas() {
		citaDao.deleteCita(citaPrueba);
		Assertions.assertEquals(0, citaDao.getAllCitas().size());
	}
	
	@Order(7)
	@Test
	void validSaveThenReturn200() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.post("/api/citas/create").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Order(8)
	@Test
	void findCitaByDni() {		
		assertTrue(citaDao.getCitasByDni(citaPrueba.getDni()).size() > 0);
	}
	
	@Order(9)
	@Test
	void checkCentroCita() {
		assertEquals(citaDao.getCitasByDni(citaPrueba.getDni()).get(0).getCentroNombre(), centroPrueba.getNombre());
	}
	
	@Order(9)
	@Test
	void after() {
		try {
			if(centroPrueba != null) { 
				centroDao.deleteCentro(centroPrueba);
			}
			if(cupoPrueba != null) {
				cupoDao.deleteCupo(cupoPrueba);
			}
			if(usuarioPrueba != null) {
				usuarioDao.deleteUsuarioByDni(usuarioPrueba.getDni());
			}
			if(citaPrueba != null) {
				citaDao.deleteCita(citaPrueba);
			}
		} catch (CentroNotFoundException e) {
			fail("CentroNotFoundException not expected");
		} catch (CupoNotFoundException e) {
			fail("CupoNotFoundException not expected");
		}
	}

}
