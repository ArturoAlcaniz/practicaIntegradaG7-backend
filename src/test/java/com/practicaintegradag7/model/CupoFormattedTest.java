package com.practicaintegradag7.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CupoFormattedTest {
	
	private static final Centro centro = new Centro("Centro 1", "Calle 1", 1); 
	
	@Test
	void failWhenFechaInicioNotEquals() {
		CupoFormatted cupo = new CupoFormatted(LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00)), LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15)), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals("20-10-2022 12:00", cupo.getFechaInicio());
	}
	
	@Test
	void failWhenFechaFinNotEquals() {
		CupoFormatted cupo = new CupoFormatted(LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00)), LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15)), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals("20-10-2022 12:15", cupo.getFechaFin());
	}
	
	@Test
	void failWhenNumeroCitasNotEquals() {
		CupoFormatted cupo = new CupoFormatted(LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00)), LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15)), 10,new Centro("Centro 1", "Calle 1", 1));
		assertEquals(10, cupo.getNumeroCitas());
	}

	@Test
	void failWhenCentroNotEquals() {
		CupoFormatted cupo = new CupoFormatted(LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00)), LDTFormatter.processLDT(LocalDateTime.of(2022, 10, 20, 12, 00).plusMinutes(15)), 10, centro);
		assertEquals(centro, cupo.getCentro());
	}
}
