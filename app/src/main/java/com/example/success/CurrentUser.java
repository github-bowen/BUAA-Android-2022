package com.example.success;

import com.example.success.entity.User;

public class CurrentUser {

    private static User currentUser;

    public static User getUser() {
        return currentUser;
    }

    // 用户是否已经登录
    public static boolean userLoggedIn() {
        return currentUser != null;
    }

    // 登录账号
    public static void logInUser(User user) {
        currentUser = user;
    }
}
