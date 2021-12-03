package cn.jaclogistic.base.model;

public abstract class VersionalEntity extends Entity {
	
	private static final long serialVersionUID = 3611424833674956237L;
	
	private long version;

	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
