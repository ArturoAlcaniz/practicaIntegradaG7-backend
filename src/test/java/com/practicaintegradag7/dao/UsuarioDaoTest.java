package com.practicaintegradag7.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioDaoTest {
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Test
	public void shouldSaveUsuario() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "admin", centro, "Paciente");
		
		assertNotNull(usuarioDao.saveUsuario(usuario));

		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	public void shouldNotSaveUsuario() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "admin", centro, "Paciente");
		
		usuarioDao.saveUsuario(usuario);
		assertNull(usuarioDao.saveUsuario(usuario));
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	public void failWhenUsuarioNotEquals() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "admin", centro, "Paciente");
		usuarioDao.saveUsuario(usuario);
		assertNotEquals(usuario, usuarioDao.getUsuarioByDni(usuario.getDni()));
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
	@Test
	public void failWhenSizeIsZero() {
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718583J", "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "admin", centro, "Paciente");
		usuarioDao.saveUsuario(usuario);
		assertNotEquals(0, usuarioDao.getAllUsuarios().size());
		usuarioDao.deleteUsuarioByDni(usuario.getDni());
	}
	
}
