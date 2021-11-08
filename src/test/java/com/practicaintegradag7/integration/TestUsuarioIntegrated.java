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
import org.junit.jupiter.api.Disabled;
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
import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
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
	private MockMvc mockMvc;
	
	@Test
	void shouldSaveUsuario() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void shouldSaveUsuarioWithPrimeraDosis() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void shouldSaveUsuarioWithSegundaDosis() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void shouldSaveUsuarioWithController() throws Exception {
		JSONObject json = new JSONObject();
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		centroDao.createCentro(centro);
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
	
	@Test
	void shouldNotSaveUsuario() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		
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
	
	@Test
	void shouldObtainUsuariosWithController() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.get("/api/usuarios/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Test
	void failWhenUsuarioDniNotEquals() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void failWhenSizeIsZero() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void failWhenPasswordNotValid() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
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
	
	@Test
	void failWhenPasswordNotValid2() {
		Centro centro = new Centro("Hospital", "Calle Paloma", 10);
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
	
	@Test
	void failWhenUsuarioDniNotValid() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital", "Calle Paloma", 10);
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
	
	@Test
	void shouldLoginWithController() throws Exception {
		JSONObject json = new JSONObject();
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		centroDao.createCentro(centro);
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
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotLoginWithController() throws CentroNotFoundException, JSONException, CifradoContrasenaException, CentroExistException {
		JSONObject json = new JSONObject();
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		centroDao.createCentro(centro);
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
			centroDao.deleteCentro(centro);
		}
	}
}
