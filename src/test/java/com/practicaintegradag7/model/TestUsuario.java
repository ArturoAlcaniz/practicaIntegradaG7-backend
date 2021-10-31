package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	
	@ParameterizedTest
	@ValueSource(strings = {"emailfail@", "@emailfail", "roberto&example.com", "roberto#@example.me.org", })
	void failWhenEmailNotValid(String email) {
		try {
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", centro,
				"paciente");
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Email is not valid!", e.getMessage());
		}
	}	
	
	@Test
	void failWhenEmailValidIsNotValid() {
		try {
			Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "valido@hotmail.com", "Iso+grupo7", centro,
					"paciente");
			assertEquals("valido@hotmail.com", usuario.getEmail());
		} catch (IllegalArgumentException e) {
			fail("Exception not expected");
		}
	}
	
	@Test
	void failWhenRolNotValid() {
		try {
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
					"robertoBrasero@a3media.es", "Iso+grupo7", centro, "obrero");
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Rol is not valid!", e.getMessage());
		}
	}
	
	@Test
	void failWhenRolValidIsNotValid() {
		try {
			Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
					"robertoBrasero@a3media.es", "Iso+grupo7", centro, "paciente");
			assertEquals("paciente", usuario.getRol());
		} catch (IllegalArgumentException e) {
			fail("Exception not expected");
		}
	}
	
	@Test
	void notFailCorrectRoles() {
		Usuario usuario1 = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
				"robertoBrasero@a3media.es", "Iso+grupo7", centro, "administrador");
		Usuario usuario2 = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
				"robertoBrasero@a3media.es", "Iso+grupo7", centro, "sanitario");
		Usuario usuario3 = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", 
				"robertoBrasero@a3media.es", "Iso+grupo7", centro, "paciente");
		assertEquals("administrador", usuario1.getRol());
		assertEquals("sanitario", usuario2.getRol());
		assertEquals("paciente", usuario3.getRol());
	}
	
	@Test
	void failWhenTheDniNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "hola@gmail.com", "Iso+grupo7", centro,
				"paciente");
		assertEquals("01234567A",usuario.getDni());
	}
	
	@Test
	void failWhenTheDniEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "hola@gmail.com", "Iso+grupo7", centro,
				"paciente");
		assertNotEquals("01234267A",usuario.getDni());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"roberto@example.com", "roberto.brasero@example.co.in", "roberto1@example.me.org", "roberto_brasero@example.com",
							"roberto-brasero@example.com"})
	void failWhenTheEmailNotEquals(String email) {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", centro,
				"paciente");
		Assertions.assertEquals(email, usuario.getEmail());
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
	void failWhenThePrimeraDosisNotEqualsFalse() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		usuario.setPrimeraDosis(false);
		assertFalse(usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenTheSegundaDosisNotEqualsFalse() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		usuario.setSegundaDosis(false);
		assertFalse(usuario.isSegundaDosis());
	}
	
	@Test
	void failWhenPrimeraDosisNotEqualsTrue() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		usuario.setPrimeraDosis(true);
		assertTrue(usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenSegundaDosisNotEqualsTrue() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		usuario.setSegundaDosis(true);
		assertTrue(usuario.isSegundaDosis());
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
	
	@Test
	void failWhenSetPasswordNotWork() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		usuario.setPassword("iso");
		assertEquals("iso", usuario.getPassword());
	}
	
	@Test
	void failWhenNameIsLengthMoreThan16() {
		Usuario usuario = new Usuario("01234567A", "Roberto fernando Roberto fernando Roberto fernando", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", centro,
				"paciente");
		try {
			usuario.encryptDNI();
			fail("Exception expected");
		} catch (CifradoContrasenaException e) {
			assertTrue(true);
		}
	}
}
