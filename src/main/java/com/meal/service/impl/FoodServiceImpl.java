package com.meal.service.impl;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.repository.FoodRepository;
import com.meal.repository.OrderRepository;
import com.meal.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;

    public FoodServiceImpl(FoodRepository foodRepository, OrderRepository orderRepository) {
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Food getById(String id) {
        return foodRepository.findById(id).orElse(null);
    }

    @Override
    public List<Food> getAll() {
        return foodRepository.findAll();
    }

    @Override
    public List<Food> getByCategory(String category) {
        return foodRepository.findByCategory(category);
    }

    @Override
    public List<Food> getAvailable() {
        return foodRepository.findByStatus("available").stream()
                .filter(food -> food.getStock() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Food food) {
        food.setId(UUID.randomUUID().toString());
        foodRepository.save(food);
    }

    @Override
    public void update(Food food) {
        foodRepository.save(food);
    }

    @Override
    public void delete(String id) {
        foodRepository.deleteById(id);
    }

    @Override
    public List<Food> getRecommended(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<Order> completedOrders = orders.stream()
                .filter(order -> "completed".equals(order.getStatus()))
                .collect(Collectors.toList());

        Map<String, Integer> foodCountMap = new HashMap<>();
        for (Order order : completedOrders) {
            foodCountMap.put(order.getFoodId(), foodCountMap.getOrDefault(order.getFoodId(), 0) + order.getQuantity());
        }

        List<String> topFoodIds = foodCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Food> availableFoods = getAvailable();

        List<Food> recommendedFoods = availableFoods.stream()
                .filter(food -> topFoodIds.contains(food.getId()))
                .collect(Collectors.toList());

        if (recommendedFoods.size() < 3) {
            List<Food> additionalFoods = availableFoods.stream()
                    .filter(food -> !topFoodIds.contains(food.getId()))
                    .limit(3 - recommendedFoods.size())
                    .collect(Collectors.toList());
            recommendedFoods.addAll(additionalFoods);
        }

        return recommendedFoods;
    }
}
