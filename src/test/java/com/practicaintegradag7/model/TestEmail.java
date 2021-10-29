package com.practicaintegradag7.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class TestEmail {
	
	private static final Centro centro = new Centro("Centro 1", "Calle 1", 10);
	
	@ParameterizedTest
	@ValueSource(strings = {"emailfail@", "@emailfail", "roberto&example.com", "roberto#@example.me.org", })
	void checkValidationEmail(String email) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
			new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", centro,
				"paciente"));
	}	
	
	@ParameterizedTest
	@ValueSource(strings = {"roberto@example.com", "roberto.brasero@example.co.in", "roberto1@example.me.org", "roberto_brasero@example.com",
							"roberto-brasero@example.com"})
	void failWhenTheEmailNotEquals(String email) {
		Usuario usuario = new Usuario("01234567A", "Roberto", "Brasero Hidalgo", email, "Iso+grupo7", new Centro("Centro 1", "Calle 1", 10),
				"paciente");
		Assertions.assertEquals(email, usuario.getEmail());
	}
}
