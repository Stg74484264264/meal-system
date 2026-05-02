package com.meal.util;

import com.meal.entity.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * 会话工具类，用于管理用户登录状态
 */
@Component
public class SessionUtil {

    /**
     * 用户会话键名
     */
    public static final String USER_SESSION_KEY = "currentUser";

    /**
     * 保存用户到会话
     *
     * @param session 会话对象
     * @param user    用户对象
     */
    public static void saveUser(HttpSession session, User user) {
        session.setAttribute(USER_SESSION_KEY, user);
    }

    /**
     * 从会话中获取当前登录用户
     *
     * @param session 会话对象
     * @return 用户对象，如果未登录返回null
     */
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(USER_SESSION_KEY);
    }

    /**
     * 从会话中移除用户（退出登录）
     *
     * @param session 会话对象
     */
    public static void removeUser(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }

    /**
     * 判断用户是否已登录
     *
     * @param session 会话对象
     * @return 是否已登录
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    /**
     * 判断用户是否为指定角色
     *
     * @param session 会话对象
     * @param role    角色名称
     * @return 是否为指定角色
     */
    public static boolean hasRole(HttpSession session, String role) {
        User user = getCurrentUser(session);
        return user != null && role.equals(user.getRole());
    }
}