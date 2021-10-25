package com.practicaintegradag7.model;

import static org.junit.Assert.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class TestUsuario {
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationDni() {
		new Usuario("0234M", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationEmail() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "email_fail", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationRol() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"obrero");
	}
	
	@Test
	public void failWhenTheDniNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "hola@gmail.com", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("01234567A",usuario.getDni());
	}
	
	@Test
	public void failWhenTheEmailNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("robertoBrasero@a3media.es",usuario.getEmail());
	}
	
	@Test
	public void failWhenThePasswordNotEquals() {
		//Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),"paciente");
		Usuario usuario = new Usuario("01234567A", "A", "B", "a@a.es", "Iso+grupo7", new Centro("Centro 2", "Calle 2", 0), "paciente");
		assertEquals("Iso+grupo7",usuario.getPassword());
	}
	
	@Test
	public void failWhenTheRolPacienteNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("paciente",usuario.getRol());
	}

	@Test
	public void failWhenTheRolSanitarioNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"sanitario");
		assertEquals("sanitario",usuario.getRol());
	}

	@Test
	public void failWhenTheRolAdministradorNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"Administrador");
		assertEquals("administrador",usuario.getRol());
	}
	
	@Test
	public void failWhenTheNombreNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("Roberto",usuario.getNombre());
	}

	@Test
	public void failWhenTheApellidosNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("Brasero Hidalgo",usuario.getApellidos());
	}

	@Test
	public void failWhenThePrimeraDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals(false,usuario.isPrimeraDosis());
	}
	
	@Test
	public void failWhenTheSegundaDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals(false,usuario.isSegundaDosis());
	}
	
	@Test
	public void failWhenTheCentroNotEquals() {
		Centro centro = new Centro("Centro 1", "Calle 1", 10);
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals(centro,usuario.getCentro());
	}
	
}