package com.meal.service;

import com.meal.entity.Food;
import com.meal.entity.Notice;
import com.meal.entity.Order;
import com.meal.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 管理员端服务接口
 */
public interface AdminService {

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(String userId);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 是否新增成功
     */
    boolean addUser(User user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 是否修改成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(String userId);

    /**
     * 获取所有菜品
     *
     * @return 菜品列表
     */
    List<Food> getAllFoods();

    /**
     * 获取菜品信息
     *
     * @param foodId 菜品ID
     * @return 菜品信息
     */
    Food getFoodById(String foodId);

    /**
     * 新增菜品
     *
     * @param food 菜品信息
     * @return 是否新增成功
     */
    boolean addFood(Food food);

    /**
     * 修改菜品信息
     *
     * @param food 菜品信息
     * @return 是否修改成功
     */
    boolean updateFood(Food food);

    /**
     * 删除菜品
     *
     * @param foodId 菜品ID
     * @return 是否删除成功
     */
    boolean deleteFood(String foodId);

    /**
     * 修改菜品状态
     *
     * @param foodId 菜品ID
     * @param status 状态：available/unavailable
     * @return 是否修改成功
     */
    boolean updateFoodStatus(String foodId, String status);

    /**
     * 获取所有公告
     *
     * @return 公告列表
     */
    List<Notice> getAllNotices();

    /**
     * 获取公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    Notice getNoticeById(String noticeId);

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 是否新增成功
     */
    boolean addNotice(Notice notice);

    /**
     * 删除公告
     *
     * @param noticeId 公告ID
     * @return 是否删除成功
     */
    boolean deleteNotice(String noticeId);

    /**
     * 获取所有订单
     *
     * @return 订单列表
     */
    List<Order> getAllOrders();

    /**
     * 获取系统统计信息
     *
     * @return 统计信息：总用户数、总菜品数、总订单数、今日订单数
     */
    Map<String, Integer> getSystemStatistics();

    /**
     * 获取订单趋势统计
     *
     * @param days 统计天数
     * @return 日期 -> 订单数的映射
     */
    Map<String, Integer> getOrderTrend(int days);

    /**
     * 初始化用户密码（重置为默认密码123456）
     *
     * @param userId 用户ID
     * @return 是否初始化成功
     */
    boolean resetPassword(String userId);

    /**
     * 根据用户名查找用户（用于找回密码）
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 重置用户密码为指定密码
     *
     * @param userId   用户ID
     * @param password 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String userId, String password);
}