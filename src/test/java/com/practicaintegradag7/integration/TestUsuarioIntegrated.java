package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
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
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;

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
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		
		try {
			assertNotNull(usuarioDao.saveUsuario(usuario));
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}

		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	void shouldSaveUsuarioWithController() throws Exception {
		JSONObject json = new JSONObject();
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		centroDao.createCentro(centro);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		json.put("dni", usuario.getDni());
		json.put("nombre", usuario.getNombre());
		json.put("apellidos", usuario.getApellidos());
		json.put("email", usuario.getEmail());
		json.put("password", usuario.getPassword());
		json.put("centro", usuario.getCentro().getNombre());
		json.put("rol", usuario.getRol());
		mockMvc.perform( MockMvcRequestBuilders.post("/api/usuario/create").contentType(MediaType.APPLICATION_JSON).content(json.toString())).andExpect(status().isOk());
		usuario.encryptDNI();
		assertNotNull(usuarioDao.getUsuarioByDni(usuario.getDni()));
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
		centroDao.deleteCentro(centro);
	}
	
	@Test
	void shouldNotSaveUsuario() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Julio", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		Usuario usuarioMismoDni = new Usuario("05718583J", "Julio", "Parra Morisco", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		
		try {
			usuarioDao.saveUsuario(usuario);
			System.out.println(usuario.getNombre()+" "+usuario.getApellidos()+" "+usuario.getDni());
			Usuario aux = usuarioDao.saveUsuario(usuarioMismoDni);
			assertNull(aux);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	void shouldObtainUsuariosWithController() throws Exception {
		mockMvc.perform( MockMvcRequestBuilders.get("/api/usuarios/obtener").accept(MediaType.ALL)).andExpect(status().isOk());
	}
	
	@Test
	void failWhenUsuarioDniNotEquals() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		
		assertEquals(usuario.getDni(), usuarioDao.getUsuarioByDni(usuario.getDni()).getDni());
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	void failWhenSizeIsZero() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		assertNotEquals(0, usuarioDao.getAllUsuarios().size());
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	void failWhenPasswordNotValid() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("01118583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "7", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			assertTrue(e.toString().contains("not valid"));
		}
	}
	
	@Test
	void failWhenPasswordNotValid2() {
		Centro centro = new Centro("Hospital", "Calle Paloma", 10);
		Usuario usuario = new Usuario("01118583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "a", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			assertTrue(e.toString().contains("not valid"));
		}
	}
	
	@Test
	void failWhenUsuarioDniNotValid() {
		Centro centro = new Centro("Hospital", "Calle Paloma", 10);
		Usuario usuario = new Usuario("1", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "a", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail("Exception not expected");
		} catch (IllegalArgumentException e) {
			assertTrue(e.toString().contains("not valid"));
		}
	}
}
