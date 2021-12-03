package cn.jaclogistic.base.dao.hibernate;

import cn.jaclogistic.base.annotation.UniqueKey;
import cn.jaclogistic.base.dao.CommonDao;
import cn.jaclogistic.base.exception.MessageException;
import cn.jaclogistic.base.exception.UniqueKeyException;
import cn.jaclogistic.base.holder.UpdateInfoHodler;
import cn.jaclogistic.base.model.Entity;
import cn.jaclogistic.base.model.EntityWithUnique;
import cn.jaclogistic.base.model.IUser;
import cn.jaclogistic.base.util.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
@Repository("commonDao")
public class HibernateCommonDao extends HibernateDaoSupport implements CommonDao {

	@Resource(name = "sessionFactory")
	public void setMySessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public <T extends Entity> T get(Class<T> clazz, Long id) {
		return (T) getHibernateTemplate().get(clazz, id);
	}

	@Override
	public <T extends Entity> T load(Class<T> clazz, Long id) {
		return (T) getHibernateTemplate().get(clazz, id);
	}

	@Override
	public <T extends Entity> List<T> loadAll(Class<T> clazz, List<Long> ids) {
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.in("id", ids));
		return (List<T>) getHibernateTemplate().findByCriteria(dc);
	}

	@Override
	public <T extends Entity> List<T> loadAll(final Class<T> clazz, final Long[] entityIds) {
		if (entityIds == null || entityIds.length <= 0) {
			return new ArrayList<T>();
		}
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.in("id", entityIds));
		return (List<T>) getHibernateTemplate().findByCriteria(dc);
	}

	@Override
	public <T extends Entity> List<T> loadAll(Class<T> clazz) {
		return getHibernateTemplate().loadAll(clazz);
	}

	@Override
	public <T extends Entity> void storeUniqueKey(T entity) {
		checkUniqueKey(entity);
		store(entity);
	}

	protected Map<String, Object> getUniqueKeyValue(Entity entity) {
		Map<String, Object> params = new HashMap<String, Object>();
		Class clazz = entity.getClass();
		List<Field> uniqueFields = new ArrayList<Field>();
		Field[] arrayOfField;
		int j = (arrayOfField = clazz.getDeclaredFields()).length;
		for (int i = 0; i < j; i++) {
			Field field = arrayOfField[i];
			if (includeUniqueKeyAnnotation(field.getDeclaredAnnotations())) {
				uniqueFields.add(field);
			}
		}
		for (Field f : uniqueFields) {
			String name = f.getName();
			Object value = null;
			try {
				value = Ognl.getValue(name, entity);
			} catch (OgnlException e) {
				e.printStackTrace();
			}
			params.put(name, value);
		}
		return params;
	}

	protected void checkUniqueKey(Entity entity) {
		Map<String, Object> params = getUniqueKeyValue(entity);
		if (params.isEmpty()) {
			return;
		}
		Class clazz = entity.getClass();
		String hql = "FROM " + clazz.getSimpleName() + " obj WHERE 1=1 ";
		StringBuffer buf = new StringBuffer();
		for (Map.Entry<String, Object> me : params.entrySet()) {
			String name = (String) me.getKey();
			buf.append(" AND obj." + name + "= :" + name);
		}
		hql = hql + buf.toString();
		try {
			List list = query(hql, params);
			if (entity.isNew()) {
				if ((list != null) && (!list.isEmpty())) {
					throw new UniqueKeyException("unique.key.constraint");
				}
			} else if ((list != null) && (list.size() > 1)) {
				throw new UniqueKeyException("unique.key.constraint");
			}
		} catch (Exception e) {
			throw new UniqueKeyException("unique.key.constraint");
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

	@Override
	public <T extends Entity> void store(T entity) {
		try {
			checkUniqueKey(entity);
			if ((entity instanceof EntityWithUnique)) {
				((EntityWithUnique) entity).genarateUniCode();
			}
			entity.updateInfo(UpdateInfoHodler.getUser());
			getHibernateTemplate().saveOrUpdate(entity);
		} catch (Exception e) {
			handleDaoException(e);
		}
	}

	private void handleDaoException(Exception e) {
		String message = e.getLocalizedMessage();
		if (message.contains("not-null property references a null or transient value")) {
			throw new MessageException("非空属性值为空");
		} else if(e instanceof DataIntegrityViolationException) {
			e.printStackTrace();
			throw new MessageException("已找到子记录");
		} else if(e instanceof UniqueKeyException) {
			e.printStackTrace();
			throw new MessageException("数据已存在");
		} else {
			throw new MessageException(e.getMessage());
		}
	}

	@Override
	public <T extends Entity> void store(T entity, IUser user) {
		try {
			checkUniqueKey(entity);
			if ((entity instanceof EntityWithUnique)) {
				((EntityWithUnique) entity).genarateUniCode();
			}
			entity.updateInfo(user);
			getHibernateTemplate().saveOrUpdate(entity);
		} catch (Exception e) {
			handleDaoException(e);
		}
	}

	@Override
	public <T extends Entity> void delete(T entity) {
		try {
			getHibernateTemplate().delete(entity);
			getHibernateTemplate().flush();
		} catch (Exception e) {
			handleDaoException(e);
		}
	}

	@Override
	public <T extends Entity> void deleteAll(Collection<T> entities) {
		try {
			getHibernateTemplate().deleteAll(entities);
			getHibernateTemplate().flush();
		} catch (Exception e) {
			handleDaoException(e);
		}
	}

	@Override
	public void initialize(Object object) {
		getHibernateTemplate().initialize(object);
	}

	@Override
	public void refresh(Object object) {
		getHibernateTemplate().lock(object, LockMode.READ);
	}

	@Override
	public List query(final String hql, final Map params) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				for (Iterator i = params.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					Object value = params.get(key);
					query.setParameter(key, value);
				}
				return query.list();
			}
		});
	}

	@Override
	public List findByQuery(String hql, String parameterKey, Object value) {
		return findByQuery(hql, new String[] { parameterKey }, new Object[] { value });
	}

	@Override
	public List findByQuery(String hql) {
		return findByQuery(hql, new String[0], new Object[0]);
	}

	@Override
	public List findByQuery(final String hql, final String[] paramNames, final Object[] values) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				query.getReturnAliases();
				if (values != null) {
					for (int i = 0; i < paramNames.length; i++) {
						if (!StringUtils.isEmpty(paramNames[i])) {
							if ((values[i] != null) && (Collection.class.isAssignableFrom(values[i].getClass()))) {
								query.setParameterList(paramNames[i], (Collection) values[i]);
							} else {
								query.setParameter(paramNames[i], values[i]);
							}
						}
					}
				}
				return query.list();
			}
		});
	}

	@Override
	public Object findByQueryUniqueResult(final String hql, final String[] paramNames, final Object[] values) {
		return getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < paramNames.length; i++) {
						if (!StringUtils.isEmpty(paramNames[i])) {
							if ((values[i] != null) && (Collection.class.isAssignableFrom(values[i].getClass()))) {
								query.setParameterList(paramNames[i], (Collection) values[i]);
							} else {
								query.setParameter(paramNames[i], values[i]);
							}
						}
					}
				}
				return query.uniqueResult();
			}
		});
	}

	@Override
	public int executeByHql(final String hql, final String[] paramNames, final Object[] values) {
		return ((Integer) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < paramNames.length; i++) {
						if (!StringUtils.isEmpty(paramNames[i])) {
							if ((values[i] != null) && (Collection.class.isAssignableFrom(values[i].getClass()))) {
								query.setParameterList(paramNames[i], (Collection) values[i]);
							} else {
								query.setParameter(paramNames[i], values[i]);
							}
						}
					}
				}
				return Integer.valueOf(query.executeUpdate());
			}
		})).intValue();
	}

	@Override
	public Object findByQueryUniqueResult(String hql, String parameterKey, Object value) {
		return findByQueryUniqueResult(hql, new String[] { parameterKey }, new Object[] { value });
	}

	@Override
	public int executeByHql(String hql, String parameterKey, Object value) {
		return executeByHql(hql, new String[] { parameterKey }, new Object[] { value });
	}

	@Override
	public List findByQueryMaxNum(String hql, String parameterKey, Object value, int firstResult, int pageSize) {
		return findByQueryMaxNum(hql, new String[] { parameterKey }, new Object[] { value }, firstResult, pageSize);
	}

	@Override
	public List findByQueryMaxNum(final String hql, final String[] paramNames, final Object[] values,
								  final int firstResult, final int pageSize) {
		if ((paramNames != null) && (values != null) && (paramNames.length != values.length)) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(hql);
				if (pageSize > 0) {
					if (firstResult < 0) {
						queryObject.setFirstResult(0);
					} else {
						queryObject.setFirstResult(firstResult);
					}
					queryObject.setFetchSize(pageSize);
					queryObject.setMaxResults(pageSize);
				}
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						HibernateCommonDao.this.applyParameterToQuery(queryObject, paramNames[i], values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}

	protected void applyParameterToQuery(Query queryObject, String paramName, Object value) throws HibernateException {
		if ((value instanceof Collection)) {
			queryObject.setParameterList(paramName, (Collection) value);
		} else if ((value instanceof Object[])) {
			queryObject.setParameterList(paramName, (Object[]) value);
		} else {
			queryObject.setParameter(paramName, value);
		}
	}

	@Override
	public List findPage(Class paramClass, final int start, final int limit) {
		DetachedCriteria dc = DetachedCriteria.forClass(paramClass);
		return (List) getHibernateTemplate().findByCriteria(dc, (start - 1) * limit, limit);
	}

	@Override
	public Long getTotalCount(Class paramClass) {
		// 设置查询的聚合函数，总记录数
		DetachedCriteria dc = DetachedCriteria.forClass(paramClass);
		dc.setProjection(Projections.rowCount());
		List<Long> count = (List<Long>) getHibernateTemplate().findByCriteria(dc);
		// 清空之前设置的聚合函数
		dc.setProjection(null);
		if (count != null && count.size() > 0) {
			Long totalCount = count.get(0);
			return totalCount.longValue();
		} else {
			return 0L;
		}
	}

	@Override
	public Long getTotalCount(DetachedCriteria dc) {
		// 设置查询的聚合函数，总记录数
		dc.setProjection(Projections.rowCount());

		List<Long> count = (List<Long>) getHibernateTemplate().findByCriteria(dc);

		// 清空之前设置的聚合函数
		dc.setProjection(null);

		if (count != null && count.size() > 0) {
			Long totalCount = count.get(0);
			return totalCount.longValue();
		} else {
			return 0L;
		}
	}

	@Override
	public Result findPage(DetachedCriteria dc, int start, int limit) {
		List list = (List) getHibernateTemplate().findByCriteria(dc, (start - 1) * limit, limit);
		return Result.ok(list, getTotalCount(dc));
	}

	@Override
	public <T extends Entity> void deleteAll(Class<T> paramClass, Long[] ids) {
		List<T> entities = loadAll(paramClass, ids);
		deleteAll(entities);
	}

	@Override
	public List findByQueryMaxNum(final String hql, final int page, final int limit) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(hql);
				int firstResult = (page - 1) * limit;
				if (limit > 0) {
					if (firstResult < 0) {
						queryObject.setFirstResult(0);
					} else {
						queryObject.setFirstResult(firstResult);
					}
					queryObject.setFetchSize(limit);
					queryObject.setMaxResults(limit);
				}
				return queryObject.list();
			}
		});
	}

	@Override
	public Long getTotalCount(final String hql) {
		return ((Long) getHibernateTemplate().execute(new HibernateCallback<Object>() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				Object uniqueRes = query.uniqueResult();
				return uniqueRes == null ? 0L : (Long)uniqueRes;
			}
		})).longValue();

	}

	@Override
	public <T extends Entity> List<T> loadAll(DetachedCriteria dc) {
		return (List) getHibernateTemplate().findByCriteria(dc);
	}

	@Override
	public <T extends Entity> void delete(Class<T> clazz, Long id) {
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq("id", id));
		List<T> list = (List<T>) getHibernateTemplate().findByCriteria(dc);
		if (!list.isEmpty()) {
			deleteAll(list);
		}
	}

	@Override
	public Long getTotalCount(String hql, String paramString2, Object paramObject) {
		return getTotalCount(hql, new String[] { paramString2 }, new Object[] { paramObject });
	}

	@Override
	public Long getTotalCount(final String hql, final String[] paramNames, final Object[] values) {
		return (Long) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Long doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				if (values != null) {
					for (int i = 0; i < paramNames.length; i++) {
						if (!StringUtils.isEmpty(paramNames[i])) {
							if ((values[i] != null) && (Collection.class.isAssignableFrom(values[i].getClass()))) {
								query.setParameterList(paramNames[i], (Collection) values[i]);
							} else {
								query.setParameter(paramNames[i], values[i]);
							}
						}
					}
				}
				Object uniqueRes = query.uniqueResult();
				return uniqueRes == null ? 0L : (Long)uniqueRes;
			}
		});
	}

}
