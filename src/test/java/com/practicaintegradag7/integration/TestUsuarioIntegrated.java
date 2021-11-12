package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.practicaintegradag7.dao.CentroDao;
import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.model.UsuarioBuilder;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestUsuarioIntegrated {

	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private CentroDao centroDao;
	
	@Autowired
	private CitaDao citaDao;
	
	@Autowired
	private MockMvc mockMvc;
	
	private Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
	
	@Order(1)
	@Test
	void before() throws CentroExistException {
		if(!centroDao.existeCentro(centro.getNombre())) centroDao.createCentro(centro);
		assertTrue(true);
	}
	
	@Order(2)
	@Test
	void shouldSaveUsuario() throws CifradoContrasenaException, UsuarioNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		
		try {
			assertNotNull(usuarioDao.saveUsuario(usuario));
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuario = usuarioDao.getUsuarioByEmail(usuario.getEmail());
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		assertTrue(!usuario.isPrimeraDosis() && !usuario.isSegundaDosis());
	}
	
	@Order(3)
	@Test
	void shouldSaveUsuarioWithPrimeraDosis() throws CifradoContrasenaException, UsuarioNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra") 
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuario.setPrimeraDosis(true);
		try {
			assertNotNull(usuarioDao.saveUsuario(usuario));
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuario = usuarioDao.getUsuarioByEmail(usuario.getEmail());
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		assertTrue(usuario.isPrimeraDosis());
	}
	
	@Order(4)
	@Test
	void shouldSaveUsuarioWithSegundaDosis() throws CifradoContrasenaException, UsuarioNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuario.setSegundaDosis(true);
		try {
			assertNotNull(usuarioDao.saveUsuario(usuario));
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}

		usuario = usuarioDao.getUsuarioByEmail(usuario.getEmail());
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		assertTrue(usuario.isSegundaDosis());
	}
	
	@Order(5)
	@Test
	void shouldSaveUsuarioWithController() throws Exception {
		JSONObject json = new JSONObject();
		
		centro = centroDao.createCentro(centro);
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		json.put("dni", usuario.getDni());
		json.put("nombre", usuario.getNombre());
		json.put("apellidos", usuario.getApellidos());
		json.put("email", usuario.getEmail());
		json.put("password", usuario.getPassword());
		json.put("centro", usuario.getCentro().getNombre());
		json.put("rol", usuario.getRol());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		usuario.encryptDNI();
		assertNotNull(usuarioDao.getUsuarioByEmail(usuario.getEmail()));
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		centroDao.deleteCentro(centro);
	}
	
	@Order(6)
	@Test
	void shouldNotSaveUsuario() throws CifradoContrasenaException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Julio")
				.apellidos("Morisco Parra") 
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		
		Usuario usuarioMismoDni = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Julio")
				.apellidos("Parra Morisco")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		
		try {
			usuarioDao.saveUsuario(usuario);
			System.out.println(usuario.getNombre()+" "+usuario.getApellidos()+" "+usuario.getDni());
			Usuario aux = usuarioDao.saveUsuario(usuarioMismoDni);
			assertNull(aux);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
	}
	
	@Order(7)
	@Test
	void shouldObtainUsuariosWithController() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.get("/api/usuarios/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Order(8)
	@Test
	void failWhenUsuarioDniNotEquals() throws UsuarioNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		
		assertEquals(usuario.getEmail(), usuarioDao.getUsuarioByEmail(usuario.getEmail()).getEmail());
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
	}
	
	@Order(9)
	@Test
	void failWhenSizeIsZero() throws CifradoContrasenaException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		assertNotEquals(0, usuarioDao.getAllUsuarios().size());
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
	}
	
	@Order(10)
	@Test
	void failWhenPasswordNotValid() throws CifradoContrasenaException {
		Usuario usuario = new UsuarioBuilder()
				.dni("01118583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("7")
				.centro(centro)
				.rol("Paciente")
				.build();
		try {
			usuarioDao.saveUsuario(usuario);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Password is not valid!", e.getMessage());
		}
	}
	
	@Order(11)
	@Test
	void failWhenPasswordNotValid2() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01118583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("a")
				.centro(centro)
				.rol("Paciente")
				.build();
		try {
			usuarioDao.saveUsuario(usuario);
			fail("IllegalArgumentException expected");
		} catch (CifradoContrasenaException e) {
			fail("CifradoContrasenaException not expected");
		} catch (IllegalArgumentException e) {
			assertTrue(e.toString().contains("not valid"));
		}
	}
	
	@Order(12)
	@Test
	void failWhenUsuarioDniNotValid() throws CifradoContrasenaException {
		Usuario usuario = new UsuarioBuilder()
				.dni("1")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		try {
			usuarioDao.saveUsuario(usuario);
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Dni is not valid!", e.getMessage());
		}
	}
	
	@Order(13)
	@Test
	void shouldLoginWithController() throws Exception {
		JSONObject json = new JSONObject();
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		json.put("email", usuario.getEmail());
		json.put("password", "Iso+grupo7");

		mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());

		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
	}
	
	@Order(14)
	@Test
	void shouldNotLoginWithController() throws CentroNotFoundException, JSONException, CifradoContrasenaException, CentroExistException {
		JSONObject json = new JSONObject();
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		String emailFail = "emailFail@gmail.com";
		json.put("email", emailFail);
		json.put("password", usuario.getPassword());
		
		try {
			mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isBadRequest());
		} catch (UsuarioNotFoundException e) {
			assertEquals("No existe un usuario con ese email y password", e.getMessage());
		} catch (Exception e) {
		}	finally {
			usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		}
	}
	
	@Order(15)
	@Test
	void shouldModifyUser() throws Exception {
		JSONObject json = new JSONObject();
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		json.put("email", usuario.getEmail());
		json.put("dni", usuario.getDni());
		json.put("nombre", "Pepito");
		json.put("apellidos", usuario.getApellidos());
		json.put("centro", usuario.getCentro().getNombre());
		json.put("rol", usuario.getRol());
		json.put("password", "Iso+grupo7");

		mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/modify").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());

		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
	}
	
	//Should not modify user, because they will be vaccinated and centro will change
	@Order(16)
	@Test
	void shouldNotModifyUser() throws Exception {
		JSONObject json = new JSONObject();
		Centro centro2 = new Centro("Hospital 2", "Calle Paloma", 10);
		centroDao.createCentro(centro2);
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuario.setPrimeraDosis(true);
		usuarioDao.saveUsuario(usuario);
		json.put("email", usuario.getEmail());
		json.put("dni", usuario.getDni());
		json.put("nombre", "Pepito");
		json.put("apellidos", usuario.getApellidos());
		json.put("centro", centro2.getNombre());
		json.put("rol", usuario.getRol());
		json.put("password", "Iso+grupo7");

		MvcResult aux = mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/modify").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andReturn();
		String res = aux.getResponse().getContentAsString();

		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		centroDao.deleteCentro(centro2);
		assertTrue(res.contains("500"));
	}
	
	@Order(17)
	@Test 
	void shouldEliminateUsuario() throws CentroExistException, CifradoContrasenaException, CentroNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		
		usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		
		try {
		
		assertEquals(null, usuarioDao.getUsuarioByEmail(usuario.getEmail()));
		
		} catch (Exception e) {
		}	finally {
			usuarioDao.deleteUsuarioByEmail(usuario.getEmail());
		}
		
	}
	
	@Order(18)
	@Test 
	void shouldEliminateUsuarioAndCitas() throws CentroExistException, CifradoContrasenaException, CentroNotFoundException, CitasUsuarioNotAvailable, CitasCupoNotAvailable, CupoNotFoundException, CupoExistException, CitaNotFoundException, UsuarioNotFoundException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718583J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(centro)
				.rol("Paciente")
				.build();
		usuarioDao.saveUsuario(usuario);
		
		usuarioDao.deleteUsuarioAndCitasByEmail(usuario.getEmail());

		assertEquals(true, citaDao.getCitasByEmail(usuario.getEmail()).isEmpty());
	}
	
	@Order(19)
	@Test
	void after() throws CentroNotFoundException, CentroExistException {
		if(centroDao.existeCentro(centro.getNombre())) {
			centro = centroDao.buscarCentroByNombre(centro.getNombre());
			centroDao.deleteCentro(centro);
		}
		assertTrue(true);
	}
}
