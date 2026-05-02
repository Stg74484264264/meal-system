package com.meal.service;

import com.meal.entity.Food;

import java.util.List;

/**
 * 餐食服务接口
 */
public interface FoodService {

    /**
     * 根据ID查询餐食
     *
     * @param id 餐食ID
     * @return 餐食对象
     */
    Food getById(String id);

    /**
     * 查询所有餐食
     *
     * @return 餐食列表
     */
    List<Food> getAll();

    /**
     * 根据分类查询餐食
     *
     * @param category 分类
     * @return 餐食列表
     */
    List<Food> getByCategory(String category);

    /**
     * 查询可用餐食
     *
     * @return 餐食列表
     */
    List<Food> getAvailable();

    /**
     * 新增餐食
     *
     * @param food 餐食对象
     */
    void add(Food food);

    /**
     * 更新餐食
     *
     * @param food 餐食对象
     */
    void update(Food food);

    /**
     * 删除餐食
     *
     * @param id 餐食ID
     */
    void delete(String id);

    /**
     * 根据用户ID获取推荐餐食
     *
     * @param userId 用户ID
     * @return 餐食列表
     */
    List<Food> getRecommended(String userId);
}