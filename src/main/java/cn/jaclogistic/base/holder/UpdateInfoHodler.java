package cn.jaclogistic.base.holder;

import cn.jaclogistic.base.model.IUser;

public class UpdateInfoHodler {
	private static ThreadLocal<IUser> currentUser = new InheritableThreadLocal<IUser>();

	public static void addUser(IUser user) {
		currentUser.set(user);
	}

	public static IUser getUser() {
		return (IUser) currentUser.get();
	}

	public static void clear() {
		currentUser.set(null);
		currentUser.remove();
	}
}
