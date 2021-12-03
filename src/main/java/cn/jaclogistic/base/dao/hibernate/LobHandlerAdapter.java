package cn.jaclogistic.base.dao.hibernate;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.util.Assert;

public class LobHandlerAdapter implements FactoryBean<Object>, BeanFactoryAware {
	private String lobHandler;
	private ListableBeanFactory beanFactory;

	public String getLobHandler() {
		return this.lobHandler;
	}

	public void setLobHandler(String lobHandler) {
		this.lobHandler = lobHandler;
	}

	@Override
	public Object getObject() throws Exception {
		Assert.notNull(this.lobHandler, "Handler is null");
		if (this.lobHandler == null) {
			this.lobHandler = "defaultLobHandler";
		}
		return this.beanFactory.getBean(this.lobHandler);
	}

	@Override
	public Class<LobHandler> getObjectType() {
		return LobHandler.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = ((ListableBeanFactory) beanFactory);
	}
}
