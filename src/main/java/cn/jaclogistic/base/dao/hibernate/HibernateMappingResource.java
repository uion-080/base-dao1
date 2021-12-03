package cn.jaclogistic.base.dao.hibernate;

import org.springframework.core.io.ClassPathResource;

public class HibernateMappingResource extends ClassPathResource implements Comparable<HibernateMappingResource> {
	private int order = 0;

	public HibernateMappingResource(String resourcePath) {
		super(resourcePath);
	}

	@Override
	public int compareTo(HibernateMappingResource resource) {
		return this.order - resource.getOrder();
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
