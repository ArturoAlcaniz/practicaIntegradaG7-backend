<<<<<<< HEAD
package com.practicaintegradag7.exceptions;

public class CentroExistException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public CentroExistException (String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
=======
package com.practicaintegradag7.exceptions;

public class CentroExistException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public CentroExistException (String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
>>>>>>> 5683872b2f93a3b77cda0575e90f3ff9b1a12075
