package cn.jaclogistic.base.model;

import java.util.Date;

public class UpdateInfo extends DomainModel {
	private static final long serialVersionUID = 6558979971377287150L;
	private Long creatorId;
	private String creator;
	private Date createdTime;
	private Long lastOperatorId;
	private String lastOperator;
	private Date updateTime;

	public UpdateInfo() {
	}

	public UpdateInfo(IUser user) {
		this.creatorId = (this.lastOperatorId = Long.valueOf(user == null ? 1L : user.getId().longValue()));
		this.creator = (this.lastOperator = user == null ? "admin" : user.getName());
		this.createdTime = (this.updateTime = new Date());
	}

	public Long getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Long getLastOperatorId() {
		return this.lastOperatorId;
	}

	public void setLastOperatorId(Long lastOperatorId) {
		this.lastOperatorId = lastOperatorId;
	}

	public String getLastOperator() {
		return this.lastOperator;
	}

	public void setLastOperator(String lastOperator) {
		this.lastOperator = lastOperator;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setUpdateInfo(IUser user) {
		this.lastOperatorId = Long.valueOf(user == null ? 1L : user.getId().longValue());
		this.lastOperator = (user == null ? "admin" : user.getName());
		this.updateTime = new Date();
	}
}
