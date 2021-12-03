package cn.jaclogistic.base.model;

public abstract class EntityWithUnique extends Entity {
	
	private static final long serialVersionUID = -4642148951834986662L;
	
	private int uniCode;

	public int getUniCode() {
		this.uniCode = hashCode();
		return this.uniCode;
	}

	public void setUniCode(int uniCode) {
		if (uniCode == 0) {
			this.uniCode = hashCode();
		} else {
			this.uniCode = uniCode;
		}
	}

	public void genarateUniCode() {
		this.uniCode = hashCode();
	}
}
