package br.com.coderup.restfuljava.exception;

public class FieldNotFoundException extends Exception {

	private static final long serialVersionUID = 264765999312846538L;

	public FieldNotFoundException() {
		super();
	}

	public FieldNotFoundException(String message, Throwable t, boolean arg2, boolean arg3) {
		super(message, t, arg2, arg3);
	}

	public FieldNotFoundException(String message, Throwable t) {
		super(message, t);
	}

	public FieldNotFoundException(String message) {
		super(message);
	}

	public FieldNotFoundException(Throwable t) {
		super(t);
	}

}
