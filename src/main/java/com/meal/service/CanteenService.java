package com.meal.service;

import com.meal.entity.Food;
import com.meal.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 食堂端服务接口
 */
public interface CanteenService {

    /**
     * 获取当日预约订单
     *
     * @param date 日期，格式：yyyy-MM-dd
     * @param status 订单状态：pending/confirmed/completed
     * @return 订单列表
     */
    List<Order> getTodayOrders(String date, String status);

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param newStatus 新状态：confirmed/completed
     * @return 是否修改成功
     */
    boolean updateOrderStatus(String orderId, String newStatus);

    /**
     * 批量修改订单状态
     *
     * @param orderIds 订单ID列表
     * @param newStatus 新状态：confirmed/completed
     * @return 成功修改的数量
     */
    int batchUpdateOrderStatus(List<String> orderIds, String newStatus);

    /**
     * 获取当日菜品预约统计
     *
     * @param date 日期，格式：yyyy-MM-dd
     * @return 菜品ID -> 预约数量的映射
     */
    Map<String, Integer> getFoodOrderStatistics(String date);

    /**
     * 获取菜品信息
     *
     * @param foodId 菜品ID
     * @return 菜品信息
     */
    Food getFoodById(String foodId);

    /**
     * 获取所有菜品
     *
     * @return 菜品列表
     */
    List<Food> getAllFoods();

    /**
     * 获取当日预约统计信息
     *
     * @param date 日期，格式：yyyy-MM-dd
     * @return 统计信息：总订单数、待配餐数、已配餐数、已领取数
     */
    Map<String, Integer> getTodayStatistics(String date);

    /**
     * 修改菜品状态（上架/下架）
     *
     * @param foodId 菜品ID
     * @param status 新状态：available/unavailable
     * @return 是否修改成功
     */
    boolean updateFoodStatus(String foodId, String status);

    /**
     * 删除菜品
     *
     * @param foodId 菜品ID
     * @return 是否删除成功
     */
    boolean deleteFood(String foodId);
}