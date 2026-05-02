package com.meal.service;

import com.meal.entity.User;

/**
 * 登录服务接口
 */
public interface LoginService {

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户对象，失败返回null
     */
    User login(String username, String password);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User findByUsername(String username);
}