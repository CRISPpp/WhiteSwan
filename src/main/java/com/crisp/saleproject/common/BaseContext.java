package com.crisp.saleproject.common;

//线程设置变量
public class BaseContext {
    private  static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(long id ){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
