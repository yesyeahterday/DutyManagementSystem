package com.luke.login;


public class LoginUserHolder {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    public static void setLoginUser(LoginUser loginUser) {
        threadLocal.set(loginUser);
    }
    public static LoginUser getLoginUser() {
        return threadLocal.get();
    }
    public static void removeLoginUser() {
        threadLocal.remove();
    }
}
