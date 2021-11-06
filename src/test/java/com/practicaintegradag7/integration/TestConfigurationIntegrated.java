package com.practicaintegradag7.integration;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TestConfigurationIntegrated {
	
	@Test
	void failWhenConfigurationAlreadySaved() {
		fail("Not implemented");
	}
	
	@Test
	void failWhenConfigurationNotSaved() {
		fail("Not implemented");
	}

}
