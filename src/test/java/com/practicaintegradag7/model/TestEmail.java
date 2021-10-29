package com.practicaintegradag7.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;

public class TestEmail {	
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationEmail() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "emailfail@", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}	
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationEmai1() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "@emailfail", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}	
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationEmail2() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice&example.com", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}	
	
	@Test(expected = IllegalArgumentException.class)
	public void checkValidationEmail3() {
		new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice#@example.me.org", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
	}	
		
	@Test
	public void failWhenTheEmailNotEquals() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "robertoBrasero@a3media.es", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("robertoBrasero@a3media.es",usuario.getEmail());
	}
	
	@Test
	public void failWhenTheEmailNotEquals1() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice@example.com", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("alice@example.com",usuario.getEmail());
	}
	
	@Test
	public void failWhenTheEmailNotEquals2() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice.bob@example.co.in", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("alice.bob@example.co.in",usuario.getEmail());
	}
	
	@Test
	public void failWhenTheEmailNotEquals3() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice1@example.me.org", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("alice1@example.me.org",usuario.getEmail());
	}
	
	@Test
	public void failWhenTheEmailNotEquals4() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice_bob@example.com", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("alice_bob@example.com",usuario.getEmail());
	}
	
	@Test
	public void failWhenTheEmailNotEquals5() {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", "alice-bob@example.com", "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		assertEquals("alice-bob@example.com",usuario.getEmail());
	}
	

}
