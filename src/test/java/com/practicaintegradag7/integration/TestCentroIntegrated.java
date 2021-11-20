package com.practicaintegradag7.integration;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CupoDao;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotEmptyException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestCentroIntegrated {
	@Autowired
	private final CentroDao aux = new CentroDao();
	
	@Autowired
	private final UsuarioDao usuarioDao = new UsuarioDao();
	
	@Autowired
	private final CupoDao cupoDao = new CupoDao();
	
	@Autowired
	private MockMvc mockMvc;
	
	private Centro prueba = new Centro("PRUEBA", "-", 20);
	
	@BeforeEach
	public void before() throws CentroExistException, CentroNotFoundException {
		try {
			Centro pos = aux.buscarCentroByNombre(prueba.getNombre());
			aux.deleteCentro(pos);
		} catch(CentroNotFoundException e) {}
		prueba = aux.createCentro(prueba);
		assertNotEquals(prueba.getId(), null);
	}
	
	@Test
	void testAddVacunas() {
		try {
			int nVacs = 10;
			aux.addVacunas(prueba.getId(), nVacs);
			System.out.println("testAddVacunas - vacunas anadidas");
			int vacunasRemotas = aux.buscarCentro(prueba.getId()).getVacunas();
			System.out.println("Vacunas remotas recogidas");
			int vacunasTotales = prueba.getVacunas() + nVacs;
			assertEquals(vacunasRemotas, vacunasTotales);
		}catch(CentroNotFoundException ex) {
			fail(ex.getMessage());
		}catch(NoSuchElementException ex) {
			fail(ex.getMessage());
		}catch(VacunasNoValidasException ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	void addVacunasNonExistentCentro() {
		try {
			byte[] array = new byte[7];
		    new Random().nextBytes(array);
		    String randomCenter = new String(array, Charset.forName("UTF-8"));
		    
			int nVacs = 10;
			aux.addVacunas(randomCenter, nVacs);
			fail("Vacunas anadidas a centro no existente");
		} catch (CentroNotFoundException ex) {
			assertTrue(ex.getMessage().contains("no encontrado"));
		} catch(VacunasNoValidasException ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	void failVacunasNotValid() {
		try {
			aux.addVacunas(prueba.getNombre(), -1);
		} catch (CentroNotFoundException e) {
			fail("VacunasNoValidasException expected");
		} catch (VacunasNoValidasException e) {
			assertTrue(e.toString().contains("mayor que 0"));
		}
	}
	
	@Test
	void shouldChangeVaccinesThenReturn200() throws Exception {
		int vacunas = aux.buscarCentroByNombre(prueba.getNombre()).getVacunas();
		JSONObject json = new JSONObject();
		json.put("hospital", prueba.getNombre());
		json.put("amount", 2);
		mockMvc.perform( MockMvcRequestBuilders.post("/api/addVaccines").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		assertEquals(vacunas+2, aux.buscarCentroByNombre(prueba.getNombre()).getVacunas());
	}
	
	@Test
	void shouldEliminateEmptyCentro() throws CentroNotEmptyException, CupoNotFoundException, CentroExistException{

		try {
			aux.deleteCentroWithNoUsers(prueba.getNombre());
			aux.buscarCentro(prueba.getNombre());
		} catch (CentroNotFoundException e) {
			assertTrue(e.getMessage().contains("no existe"));
		} finally {
			aux.createCentro(prueba);
		}
	}
	
	@Test
	void shouldNotEliminateNotEmptyCentro() throws CifradoContrasenaException, CentroNotEmptyException, CupoNotFoundException, CentroNotFoundException{
		
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(prueba.getNombre())
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		
		try {
			aux.deleteCentroWithNoUsers(prueba.getNombre());
		} catch (CentroNotEmptyException e) {
			assertTrue(e.getMessage().contains("no puede ser eliminado porque contiene"));
		} finally {
			usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		}
		
	}
	
	@Test
	void shouldChangeDataCentroThenReturn200() throws Exception {
		JSONObject json = new JSONObject();
		json.put("nombre", prueba.getNombre());
		json.put("direccion", "La paz");
		json.put("vacunas", 89);
		mockMvc.perform( MockMvcRequestBuilders.post("/api/centro/modify").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		prueba = aux.buscarCentroByNombre(prueba.getNombre());
		assertEquals("PRUEBA", prueba.getNombre());
	}
	
	@Test
	void shouldEliminateCuposCentro() throws CupoNotFoundException, CentroNotFoundException, CupoExistException, CentroExistException {
		
		Cupo cupo = new Cupo(LocalDateTime.of(2022, 1, 10, 0, 0), LocalDateTime.of(2022, 1, 10, 1, 0), 5, prueba.getNombre());
		cupoDao.saveCupo(cupo);
		
		try {
			aux.deleteCentroWithNoUsers(prueba.getNombre());
			aux.createCentro(prueba);
			assertEquals(0, cupoDao.getAllCuposByCentro(prueba).size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@AfterEach
	public void after() {
		try {
			aux.deleteCentro(prueba);
		} catch (CentroNotFoundException e) {
			assertTrue(e.toString().contains("no encontrado"));
		}
	}
}
