package com.meal.service;

import com.meal.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名和密码查询用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    User login(String username, String password);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User getById(String id);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> getAll();

    /**
     * 新增用户
     *
     * @param user 用户对象
     */
    void add(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     */
    void update(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void delete(String id);

    /**
     * 根据角色查询用户
     *
     * @param role 角色
     * @return 用户列表
     */
    List<User> getByRole(String role);
}