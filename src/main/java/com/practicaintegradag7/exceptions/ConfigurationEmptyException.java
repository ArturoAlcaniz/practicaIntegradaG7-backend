package com.practicaintegradag7.exceptions;

public class ConfigurationEmptyException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public ConfigurationEmptyException() {
		this.mssg = "No hay una configuración guardada";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}