package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

class TestLDTFormatter {
	
	@Test
	void testProcess() {
		LocalDateTime aux = LocalDateTime.of(2021, Month.NOVEMBER, 4, 8, 30);
		assertEquals("04-11-2021 08:30", LDTFormatter.processLDT(aux));
	}
	
	@Test
	void testParse() {
		String datetime = "05-10-2022 20:00";
		LocalDateTime aux = LocalDateTime.of(2022, Month.OCTOBER, 5, 20, 0);
		assertEquals(aux, LDTFormatter.parse(datetime));
	}
	

}
