package cn.jaclogistic.base.exception;

public class MessageException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String mistake;

	public MessageException(String key) {
		super(key);
		this.mistake = key;
	}

	public String getMistake() {
		return this.mistake;
	}

	public void setMistake(String mistake) {
		this.mistake = mistake;
	}
}
