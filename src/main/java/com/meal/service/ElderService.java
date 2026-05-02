package com.meal.service;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;

import java.util.Date;
import java.util.List;

/**
 * 老人端服务接口
 */
public interface ElderService {

    /**
     * 获取当日菜单
     *
     * @param mealType 餐型：breakfast/lunch/dinner
     * @return 餐食列表
     */
    List<Food> getTodayMenu(String mealType);

    /**
     * 创建预约订单
     *
     * @param userId    用户ID
     * @param foodId    餐食ID
     * @param quantity  数量
     * @param orderTime 预约时间
     * @return 创建的订单
     */
    Order createOrder(String userId, String foodId, int quantity, Date orderTime);

    /**
     * 获取用户的所有订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getUserOrders(String userId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否取消成功
     */
    boolean cancelOrder(String orderId, String userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(String userId);

    /**
     * 获取餐食详情
     *
     * @param foodId 餐食ID
     * @return 餐食信息
     */
    Food getFoodById(String foodId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 订单信息
     */
    Order getOrderById(String orderId, String userId);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);

    /**
     * 确认领取订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否领取成功
     */
    boolean confirmReceiveOrder(String orderId, String userId);
}