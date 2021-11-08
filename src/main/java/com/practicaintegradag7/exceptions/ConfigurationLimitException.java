package com.practicaintegradag7.exceptions;

public class ConfigurationLimitException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public ConfigurationLimitException() {
		this.mssg = "Ya hay una configuración guardada";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}