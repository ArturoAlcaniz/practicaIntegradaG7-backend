package com.practicaintegradag7.exceptions;

public class UserModificationException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public UserModificationException (String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
