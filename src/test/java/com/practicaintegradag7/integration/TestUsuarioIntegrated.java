package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.practicaintegradag7.dao.UsuarioDao;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestUsuarioIntegrated {

	@Autowired
	private UsuarioDao usuarioDao;
	
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
}
