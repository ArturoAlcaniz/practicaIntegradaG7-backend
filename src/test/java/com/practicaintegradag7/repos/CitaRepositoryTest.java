package com.practicaintegradag7.repos;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practicaintegradag7.model.Cita;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CitaRepositoryTest {

	@Autowired
	private CitaRepository citaRepository;
	
	@Test
	public void shouldSaveCita() {
		Cita cita = new Cita("07234567A", LocalDateTime.of(2021, 10, 20, 12, 00));
		assertNotNull(citaRepository.findByDni(cita.getDni()));
		citaRepository.deleteByDni(cita.getDni());
	}
}
