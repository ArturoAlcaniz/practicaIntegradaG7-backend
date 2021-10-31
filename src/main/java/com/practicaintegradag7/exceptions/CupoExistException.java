package com.practicaintegradag7.exceptions;

public class CupoExistException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public CupoExistException (String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
