package cn.jaclogistic.base.dao;

import cn.jaclogistic.base.model.Entity;
import cn.jaclogistic.base.model.IUser;
import cn.jaclogistic.base.util.Result;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

public interface CommonDao {
	public abstract <T extends Entity> T get(Class<T> paramClass, Long paramLong);

	public abstract <T extends Entity> T load(Class<T> paramClass, Long paramLong);

	public abstract <T extends Entity> List<T> loadAll(Class<T> paramClass, Long[] paramArrayOfLong);

	public abstract <T extends Entity> List<T> loadAll(Class<T> paramClass);

	public abstract <T extends Entity> List<T> loadAll(Class<T> paramClass, List<Long> paramList);

	public abstract <T extends Entity> List<T> loadAll(DetachedCriteria dc);

	public abstract <T extends Entity> void store(T paramT);

	public abstract <T extends Entity> void store(T paramT, IUser paramIUser);

	public abstract <T extends Entity> void storeUniqueKey(T paramT);

	public abstract <T extends Entity> void delete(T paramT);
	
	public abstract <T extends Entity> void delete(Class<T> paramClass, Long id);

	public abstract <T extends Entity> void deleteAll(Collection<T> paramCollection);

	public abstract <T extends Entity> void deleteAll(Class<T> paramClass, Long[] ids);

	public abstract void initialize(Object paramObject);

	public abstract void refresh(Object paramObject);

	public abstract List query(String paramString, Map paramMap);

	public abstract List findByQuery(String paramString1, String paramString2, Object paramObject);

	public abstract List findByQuery(String paramString);

	public abstract List findByQuery(String paramString, String[] paramArrayOfString, Object[] paramArrayOfObject);

	public abstract Object findByQueryUniqueResult(String paramString, String[] paramArrayOfString,
			Object[] paramArrayOfObject);

	public abstract Object findByQueryUniqueResult(String paramString1, String paramString2, Object paramObject);

	public abstract int executeByHql(String paramString, String[] paramArrayOfString, Object[] paramArrayOfObject);

	public abstract int executeByHql(String paramString1, String paramString2, Object paramObject);

	public abstract List findByQueryMaxNum(String paramString1, String paramString2, Object paramObject, int paramInt1,
			int paramInt2);

	public abstract List findByQueryMaxNum(String hql, int page, int limit);

	public abstract List findByQueryMaxNum(String paramString, String[] paramArrayOfString, Object[] paramArrayOfObject,
			int paramInt1, int paramInt2);

	public abstract Long getTotalCount(Class paramClass);

	public abstract Long getTotalCount(String hql);
	
	public abstract Long getTotalCount(String hql, String paramString, Object paramObject);
	
	public abstract Long getTotalCount(String hql, String[] paramArrayOfString, Object[] paramArrayOfObject);

	public abstract List findPage(Class paramClass, int start, int limit);

	public abstract Long getTotalCount(DetachedCriteria dc);

	public abstract Result findPage(DetachedCriteria dc, int start, int limit);
}
