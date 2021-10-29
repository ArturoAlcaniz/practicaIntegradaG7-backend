package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;

class TestUsuario {
	
	private static final Centro centro = new Centro("Centro 1", "Calle 1", 10);
	
	@Test
	void checkValidationDni() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
			new Usuario("0234M", "Roberto", "Brasero Hidalgo", 
					"robertoBrasero@a3media.es", "Iso+grupo7", centro, "paciente"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"emailfail@", "@emailfail", "roberto&example.com", "roberto#@example.me.org", })
	void checkValidationEmail(String email) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", centro,
				"paciente"));
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
		Assertions.assertEquals("01234567A",usuario.getDni());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"roberto@example.com", "roberto.brasero@example.co.in", "roberto1@example.me.org", "roberto_brasero@example.com",
							"roberto-brasero@example.com"})
	void failWhenTheEmailNotEquals(String email) {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		Assertions.assertEquals(email, usuario.getEmail());
	}
	
	@Test
	void failWhenThePasswordNotEquals() {
		Usuario usuario = new Usuario("01234567A", "A", "B", "a@a.es", "Iso+grupo7", centro, "paciente");
		Assertions.assertEquals("Iso+grupo7",usuario.getPassword());
	}
	
	@Test
	void failWhenTheRolPacienteNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals("paciente",usuario.getRol());
	}

	@Test
	void failWhenTheRolSanitarioNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"sanitario");
		Assertions.assertEquals("sanitario",usuario.getRol());
	}

	@Test
	void failWhenTheRolAdministradorNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"Administrador");
		Assertions.assertEquals("administrador",usuario.getRol());
	}
	
	@Test
	void failWhenTheNombreNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals("Roberto",usuario.getNombre());
	}

	@Test
	void failWhenTheApellidosNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals("Brasero Hidalgo",usuario.getApellidos());
	}

	@Test
	void failWhenThePrimeraDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals(false,usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenTheSegundaDosisNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals(false,usuario.isSegundaDosis());
	}
	
	@Test
	void failWhenTheCentroNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals(centro,usuario.getCentro());
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
		Assertions.assertEquals(usuario.getDni(), DNI);
	}
}
