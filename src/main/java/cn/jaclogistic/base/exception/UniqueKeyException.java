package cn.jaclogistic.base.exception;

public class UniqueKeyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UniqueKeyException() {
	}

	public UniqueKeyException(String message) {
		super(message);
	}
}
