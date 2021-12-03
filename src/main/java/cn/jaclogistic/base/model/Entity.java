package cn.jaclogistic.base.model;

import cn.jaclogistic.base.annotation.EntityType;
import cn.jaclogistic.base.annotation.UniqueKey;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity extends DomainModel implements EntityInterceptor {
	public static final String IGNORE = "ignore";
	public static final String NORMAL = "normal";
	private Long id;
	private UpdateInfo updateInfo = new UpdateInfo();
	@EntityType(entityType = "ignore")
	private Entity oldEntity;
	private List<Field> uniqueFields = new ArrayList<Field>();

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean isNew() {
		return this.id == null;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public UpdateInfo getUpdateInfo() {
		return this.updateInfo;
	}

	public void setUpdateInfo(UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
	}

	public Entity getOldEntity() {
		return this.oldEntity;
	}

	public void setOldEntity(Entity oldEntity) {
		this.oldEntity = oldEntity;
	}

	public boolean equals(Object other) {
		return super.equals(other);
	}

	public Entity myself() {
		return this;
	}

	private void initUniqueFields(Class<?> clazz) {
		if (this.uniqueFields.isEmpty()) {
			Field[] arrayOfField;
			int j = (arrayOfField = clazz.getDeclaredFields()).length;
			for (int i = 0; i < j; i++) {
				Field field = arrayOfField[i];
				if (includeUniqueKeyAnnotation(field.getDeclaredAnnotations())) {
					this.uniqueFields.add(field);
				}
			}
		}
	}

	private boolean includeUniqueKeyAnnotation(Annotation[] annotations) {
		if (annotations == null) {
			return false;
		}
		Annotation[] arrayOfAnnotation;
		int j = (arrayOfAnnotation = annotations).length;
		for (int i = 0; i < j; i++) {
			Annotation annotation = arrayOfAnnotation[i];
			if (UniqueKey.class.isAssignableFrom(annotation.getClass())) {
				return ((UniqueKey) annotation).isUnique();
			}
		}
		return false;
	}

	public String toString() {
		return getClass().getName() + ":" + this.id;
	}

	public Long getLogId() {
		return getId();
	}

	public String getCode() {
		return "";
	}

	@Override
	public void updateInfo(IUser user) {
		if (isNew()) {
			this.updateInfo = new UpdateInfo(user);
		} else if (this.updateInfo != null) {
			this.updateInfo.setUpdateInfo(user);
		} else {
			this.updateInfo = new UpdateInfo(user);
		}
	}

	@Override
	public void doSaveBefore() {
	}

	@Override
	public void afterPropertySet(String process) {
	}
}
