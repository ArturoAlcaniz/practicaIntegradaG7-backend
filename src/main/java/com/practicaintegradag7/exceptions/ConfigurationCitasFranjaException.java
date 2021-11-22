package com.practicaintegradag7.exceptions;

public class ConfigurationCitasFranjaException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public ConfigurationCitasFranjaException() {
		this.mssg = "Valores incorrectos, citas y franjas deben ser mayor que 0";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}