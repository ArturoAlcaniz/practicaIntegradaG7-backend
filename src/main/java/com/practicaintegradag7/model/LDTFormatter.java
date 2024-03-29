package com.practicaintegradag7.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LDTFormatter {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
	private LDTFormatter() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static LocalDateTime parse(String s) throws DateTimeParseException {
		try {
			s = s.replace("/", "-");
			return LocalDateTime.parse(s, formatter);
		} catch(DateTimeParseException e) {
			return LocalDateTime.parse(s);
		}
	}
	
	public static String processLDT(LocalDateTime aux) {
		return aux.format(formatter);
	}
}
