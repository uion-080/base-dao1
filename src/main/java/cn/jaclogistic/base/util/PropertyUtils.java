package cn.jaclogistic.base.util;

import cn.jaclogistic.base.model.DomainModel;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyUtils {
	static Log logger = null;
	public static Set<Class> PRIMITIVE_TYPES;
	public static Set<Class> NUMBER_TYPES;

	static {
		logger = LogFactory.getLog(PropertyUtils.class);

		PRIMITIVE_TYPES = new HashSet();
		NUMBER_TYPES = new HashSet();

		PRIMITIVE_TYPES.add(Byte.TYPE);
		PRIMITIVE_TYPES.add(Byte.class);
		PRIMITIVE_TYPES.add(Short.TYPE);
		PRIMITIVE_TYPES.add(Short.class);
		PRIMITIVE_TYPES.add(Integer.TYPE);
		PRIMITIVE_TYPES.add(Integer.class);
		PRIMITIVE_TYPES.add(Long.TYPE);
		PRIMITIVE_TYPES.add(Long.class);
		PRIMITIVE_TYPES.add(Double.TYPE);
		PRIMITIVE_TYPES.add(Double.class);
		PRIMITIVE_TYPES.add(Float.TYPE);
		PRIMITIVE_TYPES.add(Float.class);
		PRIMITIVE_TYPES.add(Character.TYPE);
		PRIMITIVE_TYPES.add(Character.class);
		PRIMITIVE_TYPES.add(String.class);
		PRIMITIVE_TYPES.add(Boolean.TYPE);
		PRIMITIVE_TYPES.add(Boolean.class);
		PRIMITIVE_TYPES.add(Date.class);
		PRIMITIVE_TYPES.add(Class.class);

		PRIMITIVE_TYPES.add(Enum.class);

		NUMBER_TYPES.add(Byte.TYPE);
		NUMBER_TYPES.add(Byte.class);
		NUMBER_TYPES.add(Short.TYPE);
		NUMBER_TYPES.add(Short.class);
		NUMBER_TYPES.add(Integer.TYPE);
		NUMBER_TYPES.add(Integer.class);
		NUMBER_TYPES.add(Long.TYPE);
		NUMBER_TYPES.add(Long.class);
		NUMBER_TYPES.add(Double.TYPE);
		NUMBER_TYPES.add(Double.class);
		NUMBER_TYPES.add(Float.TYPE);
		NUMBER_TYPES.add(Float.class);
	}

	public static boolean isExistProperty(Object obj, String propertyName) {
		return getPropertyType(obj.getClass(), propertyName) != null;
	}

	public static Class getPropertyType(Class beanClass, String propertyName) {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
		if (propertyName.indexOf(".") != -1) {
			String parentName = StringUtils.substringBefore(propertyName, ".");
			String childName = StringUtils.substringAfter(propertyName, ".");
			for (int i = 0; i < descriptors.length; i++) {
				if (parentName.equals(descriptors[i].getName())) {
					return getPropertyType(descriptors[i].getPropertyType(), childName);
				}
			}
		} else {
			for (int i = 0; i < descriptors.length; i++) {
				if (propertyName.equals(descriptors[i].getName())) {
					return descriptors[i].getPropertyType();
				}
			}
		}
		logger.warn("Property '" + propertyName + "' not found in bean " + beanClass);
		return null;
	}

	public static boolean isPrimitiveType(Class clazz) {
		for (Class clz : PRIMITIVE_TYPES) {
			if (clz.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumberType(Class clz) {
		for (Class numClz : NUMBER_TYPES) {
			if (numClz.isAssignableFrom(clz)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTypeMatched(Class clazz, Class otherClazz) {
		if ((clazz == null) || (otherClazz == null)) {
			return false;
		}
		if ((isPrimitiveType(clazz)) && (isPrimitiveType(otherClazz))) {
			if (clazz.isAssignableFrom(otherClazz)) {
				return true;
			}
			String simpleName = getSimpleName(clazz.getName());
			String otherSimpleName = getSimpleName(otherClazz.getName());
			return simpleName.equalsIgnoreCase(otherSimpleName);
		}
		return clazz.isAssignableFrom(otherClazz);
	}

	private static String getSimpleName(String name) {
		if (name.indexOf('.') == -1) {
			return name;
		}
		return StringUtils.substringAfterLast(name, ".");
	}

	public static <T extends DomainModel> T instanceObject(Class<T> clz) {
		try {
			Constructor constructor = clz.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			return (T) constructor.newInstance(new Object[0]);
		} catch (SecurityException e2) {
			e2.printStackTrace();
		} catch (NoSuchMethodException e2) {
			e2.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.warn("Can not instant Class [" + clz.getName() + "]");

		return null;
	}

	public static Object instanceObject(String fullName) {
		Class clz = null;
		try {
			clz = Class.forName(fullName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return instanceObject(clz);
	}
}
