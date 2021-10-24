package com.practicaintegradag7.repos;

import static org.junit.Assert.assertNotNull;


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
		Centro centro = new Centro("Hospital 1", "Calle Paloma", 10);
		Usuario usuario = new Usuario("05718580F", "Francisco", "Morisco Parra", "franMorisco@gmail.com", "admin", centro, "Paciente");
		assertNotNull(usuarioRepository.findByDni(usuario.getDni()));
		usuarioRepository.deleteByDni(usuario.getDni());
	}
}