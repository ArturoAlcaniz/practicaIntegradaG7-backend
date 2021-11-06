package com.practicaintegradag7.exceptions;

public class ConfigurationTimeException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public ConfigurationTimeException() {
		this.mssg = "La hora inicio no puede ser superior a la hora fin";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}