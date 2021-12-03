package cn.jaclogistic.base.model;

public abstract interface EntityInterceptor
{
  public abstract void doSaveBefore();
  
  public abstract void afterPropertySet(String paramString);
  
  public abstract void updateInfo(IUser paramIUser);
}
