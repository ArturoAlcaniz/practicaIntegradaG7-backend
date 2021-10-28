package com.practicaintegradag7.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioDaoTest {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Test
	public void shouldSaveUsuario() throws CifradoContrasenaException {
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
	public void shouldNotSaveUsuario() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Julio", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		Usuario usuarioMismoDni = new Usuario("05718583J", "Julio", "Parra Morisco", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		
		try {
			System.out.println("---------------------------------------------");
			usuarioDao.saveUsuario(usuario);
			System.out.println(usuario.getNombre()+" "+usuario.getApellidos()+" "+usuario.getDni());
			Usuario aux = usuarioDao.saveUsuario(usuarioMismoDni);
			System.out.println("-------"+usuarioMismoDni.getNombre()+" "+usuarioMismoDni.getApellidos()+" "+usuarioMismoDni.getDni());
			assertNull(aux);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	public void failWhenUsuarioDniNotEquals() {
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
	public void failWhenSizeIsZero() throws CifradoContrasenaException {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		try {
			usuarioDao.saveUsuario(usuario);
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		System.out.println("Tamaño de la lista: " +usuarioDao.getAllUsuarios().size());
		assertNotEquals(0, usuarioDao.getAllUsuarios().size());
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
}
