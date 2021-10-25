package com.practicaintegradag7.repos;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioRepositoryTest {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Test
	public void shouldSaveUsuario() {
	
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 3);
		Usuario usuario = new Usuario("05718580F", "Francisco", "Morisco Parra", "franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");

		assertNotNull(usuarioRepository.save(usuario));
		usuarioRepository.deleteByDni(usuario.getDni());
		
	}
	

	@Test
	public void shouldDeleteUsuario() {
	
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 3);
		Usuario usuario = new Usuario("05718580F", "Francisco", "Morisco Parra", "franMorisco@gmail.com", "Iso+grupo7", centro, "Paciente");
		
		usuarioRepository.save(usuario);
		usuarioRepository.deleteByDni(usuario.getDni());
		assertNull(usuarioRepository.findByDni(usuario.getDni()));
		
	}
}