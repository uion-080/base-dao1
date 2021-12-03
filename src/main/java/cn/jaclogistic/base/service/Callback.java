package cn.jaclogistic.base.service;

import cn.jaclogistic.base.dao.CommonDao;

public abstract interface Callback {
	public abstract Object exec(CommonDao paramCommonDao) throws Exception;
}
