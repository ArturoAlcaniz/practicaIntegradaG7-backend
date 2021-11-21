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
		
		UsuarioBuilder usuarioBuilder = new UsuarioBuilder()
			.dni("01234567A")
			.nombre("Roberto")
			.apellidos("Brasero Hidalgo")
			.email(email)
			.password("Iso+grupo7")
			.centro(centro.getNombre())
			.rol("paciente");
		
		try {
			usuarioBuilder.build();
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Email is not valid!", e.getMessage());
		}
	}	
	
	@Test
	void failWhenEmailValidIsNotValid() {
		try {
			Usuario usuario = new UsuarioBuilder()
					.dni("01234567A")
					.nombre("Roberto")
					.apellidos("Brasero Hidalgo")
					.email("valido@hotmail.com")
					.password("Iso+grupo7")
					.centro(centro.getNombre())
					.rol("paciente")
					.build();
			assertEquals("valido@hotmail.com", usuario.getEmail());
		} catch (IllegalArgumentException e) {
			fail("Exception not expected");
		}
	}
	
	@Test
	void failWhenEmailIsNotValidInBuilder() {
		try {
			UsuarioBuilder usuarioBuilder = new UsuarioBuilder()
					.dni("01234567A")
					.nombre("Roberto")
					.apellidos("Brasero Hidalgo")
					.email("novalido&hotmail.com")
					.password("Iso+grupo7")
					.centro(centro.getNombre())
					.rol("paciente");
			usuarioBuilder.getDni();
		} catch (IllegalArgumentException e) {
			assertEquals("Email is not valid!", e.getMessage());
		}
	}
	
	@Test
	void failWhenRolIsNotValidInBuilder() {
		try {
			UsuarioBuilder usuarioBuilder = new UsuarioBuilder()
					.dni("01234567A")
					.nombre("Roberto")
					.apellidos("Brasero Hidalgo")
					.email("valido@hotmail.com")
					.password("Iso+grupo7")
					.centro(centro.getNombre())
					.rol("sarten");
			usuarioBuilder.getDni();
		} catch (IllegalArgumentException e) {
			assertEquals("Rol is not valid!", e.getMessage());
		}
	}
	
	@Test
	void failWhenRolNotValid() {

		UsuarioBuilder usuarioBuilder = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("obrero");
		try {
			usuarioBuilder.build();
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals("Rol is not valid!", e.getMessage());
		}
	}
	
	@Test
	void failWhenRolValidIsNotValid() {
		try {
			Usuario usuario = new UsuarioBuilder()
					.dni("01234567A")
					.nombre("Roberto")
					.apellidos("Brasero Hidalgo")
					.email("robertoBrasero@a3media.es")
					.password("Iso+grupo7")
					.centro(centro.getNombre())
					.rol("paciente")
					.build();
			assertEquals("paciente", usuario.getRol());
		} catch (IllegalArgumentException e) {
			fail("Exception not expected");
		}
	}
	
	@Test
	void notFailCorrectRoles() {
		Usuario usuario1 = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("administrador")
				.build();
		
		Usuario usuario2 = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("sanitario")
				.build();
		
		Usuario usuario3 = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		
		assertEquals("administrador", usuario1.getRol());
		assertEquals("sanitario", usuario2.getRol());
		assertEquals("paciente", usuario3.getRol());
	}
	
	@Test
	void failWhenTheDniNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("hola@gmail.com")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals("01234567A",usuario.getDni());
	}
	
	@Test
	void failWhenTheDniEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("hola@gmail.com")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertNotEquals("01234267A",usuario.getDni());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"roberto@example.com", "roberto.brasero@example.co.in", "roberto1@example.me.org", "roberto_brasero@example.com",
							"roberto-brasero@example.com"})
	void failWhenTheEmailNotEquals(String email) {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email(email)
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		Assertions.assertEquals(email, usuario.getEmail());
	}
	
	@Test
	void failWhenThePasswordNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("A")
				.apellidos("B")
				.email("a@a.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals("Iso+grupo7",usuario.getPassword());
	}
	
	@Test
	void failWhenTheRolPacienteNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals("paciente",usuario.getRol());
	}

	@Test
	void failWhenTheRolSanitarioNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("sanitario")
				.build();
		assertEquals("sanitario",usuario.getRol());
	}

	@Test
	void failWhenTheRolAdministradorNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("Administrador")
				.build();
		assertEquals("administrador",usuario.getRol());
	}
	
	@Test
	void failWhenTheNombreNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals("Roberto",usuario.getNombre());
	}

	@Test
	void failWhenTheApellidosNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals("Brasero Hidalgo",usuario.getApellidos());
	}

	@Test
	void failWhenThePrimeraDosisNotEqualsFalse() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		usuario.setPrimeraDosis(false);
		assertFalse(usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenTheSegundaDosisNotEqualsFalse() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		usuario.setSegundaDosis(false);
		assertFalse(usuario.isSegundaDosis());
	}
	
	@Test
	void failWhenPrimeraDosisNotEqualsTrue() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		usuario.setPrimeraDosis(true);
		assertTrue(usuario.isPrimeraDosis());
	}
	
	@Test
	void failWhenSegundaDosisNotEqualsTrue() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		usuario.setSegundaDosis(true);
		assertTrue(usuario.isSegundaDosis());
	}
	
	@Test
	void failWhenTheCentroNotEquals() {
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		assertEquals(centro.getNombre(),usuario.getCentro());
	}

	@Test
	void encryptAndDecrypt() throws CifradoContrasenaException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718738J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(null)
				.rol("Paciente")
				.build();
		try {
			usuario.encryptDNI();
		} catch (CifradoContrasenaException e) {
			fail(e.getMessage());
		}
		usuario.decryptDNI();
		assertEquals("05718738J", usuario.getDniDenc());
	}
	
	@Test
	void WhenCentroEqualAfterSetCentro() {
		centro.setNombre("Centro 2");
		Usuario usuario = new UsuarioBuilder()
				.dni("01234567A")
				.nombre("Roberto")
				.apellidos("Brasero Hidalgo")
				.email("robertoBrasero@a3media.es")
				.password("Iso+grupo7")
				.centro(centro.getNombre())
				.rol("paciente")
				.build();
		
		
		assertEquals("Centro 2", usuario.getCentro());
		centro.setNombre("Centro 1");
	}
	
	@Test
	void encryptFailMoreThan16() throws CifradoContrasenaException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		Usuario usuario = new UsuarioBuilder()
				.dni("05718738J")
				.nombre("Francisco")
				.apellidos("Morisco Parra")
				.email("franMoriscoMorisco@gmail.com")
				.password("Iso+grupo7")
				.centro(null)
				.rol("Paciente")
				.build();
		try {
			usuario.encryptDNI();
		} catch (CifradoContrasenaException e) {
			assertEquals("05718738J", usuario.getDniDenc());
		}
	}
}
