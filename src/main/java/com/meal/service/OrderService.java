package com.meal.service;

import com.meal.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单对象
     */
    Order getById(String id);

    /**
     * 查询所有订单
     *
     * @return 订单列表
     */
    List<Order> getAll();

    /**
     * 根据用户ID查询订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getByUserId(String userId);

    /**
     * 根据状态查询订单
     *
     * @param status 状态
     * @return 订单列表
     */
    List<Order> getByStatus(String status);

    /**
     * 新增订单
     *
     * @param order 订单对象
     * @return 是否成功
     */
    boolean add(Order order);

    /**
     * 更新订单
     *
     * @param order 订单对象
     */
    void update(Order order);

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 是否成功
     */
    boolean cancel(String id);

    /**
     * 完成订单
     *
     * @param id 订单ID
     */
    void complete(String id);

    /**
     * 获取订单详情（包含用户和餐食信息）
     *
     * @param id 订单ID
     * @return 订单对象
     */
    Order getOrderDetail(String id);

    /**
     * 获取订单列表详情（包含用户和餐食信息）
     *
     * @param orders 订单列表
     * @return 订单列表
     */
    List<Order> getOrderDetails(List<Order> orders);
}