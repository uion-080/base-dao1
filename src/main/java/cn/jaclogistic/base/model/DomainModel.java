package cn.jaclogistic.base.model;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DomainModel implements Serializable {
	private static final long serialVersionUID = -2509278009344831329L;
	protected final transient Log logger = LogFactory.getLog(getClass());
}
