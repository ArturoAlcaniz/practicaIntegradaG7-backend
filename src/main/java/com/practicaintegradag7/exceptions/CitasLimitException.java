package com.practicaintegradag7.exceptions;

public class CitasLimitException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasLimitException() {
		this.mssg = "Ya existen citas con este dni";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}