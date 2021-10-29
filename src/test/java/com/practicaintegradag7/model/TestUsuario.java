package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;

class TestUsuario {
	
	private static final Centro centro = new Centro("Centro 1", "Calle 1", 10);
	
	@Test
	void checkValidationDni() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
			new Usuario("0234M", "Roberto", "Brasero Hidalgo", 
					"robertoBrasero@a3media.es", "Iso+grupo7", centro, "paciente"));
	}
	
	@Test
	void checkValidationEmail() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
					"email_fail", "Iso+grupo7", centro, "paciente"));
	}
	
	@Test
	void checkValidationRol() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
					"robertoBrasero@a3media.es", "Iso+grupo7", centro, "obrero"));
	}
	
	@Test
	void failWhenTheDniNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "hola@gmail.com", "Iso+grupo7", centro,
				"paciente");
		assertEquals("01234567A",usuario.getDni());
	}
	
	@Test
	void failWhenTheEmailNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals("robertoBrasero@a3media.es",usuario.getEmail());
	}
	
	@Test

	void failWhenThePasswordNotEquals() {
		Usuario usuario = new Usuario("01234567A", "A", "B", "a@a.es", "Iso+grupo7", centro, "paciente");
		assertEquals("Iso+grupo7",usuario.getPassword());
	}
	
	@Test
	void failWhenTheRolPacienteNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals("paciente",usuario.getRol());
	}

	@Test
	void failWhenTheRolSanitarioNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"sanitario");
		assertEquals("sanitario",usuario.getRol());
	}

	@Test
	void failWhenTheRolAdministradorNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"Administrador");
		assertEquals("administrador",usuario.getRol());
	}
	
	@Test
	void failWhenTheNombreNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals("Roberto",usuario.getNombre());
	}

	@Test
	void failWhenTheApellidosNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals("Brasero Hidalgo",usuario.getApellidos());
	}

	@Test
	void failWhenThePrimeraDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals(false,usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenTheSegundaDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals(false,usuario.isSegundaDosis());
	}
	
	@Test
	void failWhenTheCentroNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		assertEquals(centro,usuario.getCentro());
	}

	@Test
	void encryptAndDecrypt() throws CifradoContrasenaException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		String DNI = "05718738J";
		Usuario usuario = new Usuario(DNI, "Francisco", "Morisco Parra", 
				"franMorisco@gmail.com", "Iso+grupo7", null, "Paciente");
		try {
			usuario.encryptDNI();
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuario.decryptDNI();
		assertEquals(usuario.getDni(), DNI);
	}
}
