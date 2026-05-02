package com.meal.service.impl;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.repository.FoodRepository;
import com.meal.repository.OrderRepository;
import com.meal.service.CanteenService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CanteenServiceImpl implements CanteenService {

    private final OrderRepository orderRepository;
    private final FoodRepository foodRepository;

    public CanteenServiceImpl(OrderRepository orderRepository, FoodRepository foodRepository) {
        this.orderRepository = orderRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public List<Order> getTodayOrders(String date, String status) {
        List<Order> orders = orderRepository.findAll();
        List<Food> foods = foodRepository.findAll();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return orders.stream()
                .filter(order -> order.getOrderTime() != null && 
                        order.getOrderTime().format(formatter).equals(date))
                .filter(order -> {
                    if (status == null || status.isEmpty()) {
                        return true;
                    }
                    return order.getStatus().equals(status);
                })
                .map(order -> {
                    foods.stream()
                            .filter(food -> food.getId().equals(order.getFoodId()))
                            .findFirst()
                            .ifPresent(food -> {
                                order.setFoodName(food.getName());
                                order.setFoodImage(food.getImage());
                            });
                    return order;
                })
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateOrderStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null) {
            return false;
        }
        
        String currentStatus = order.getStatus();
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            return false;
        }
        
        String previousStatus = order.getStatus();
        order.setStatus(newStatus);
        orderRepository.save(order);
        
        if ("confirmed".equals(previousStatus) && "completed".equals(newStatus)) {
            updateFoodStock(order.getFoodId(), order.getQuantity());
        }
        
        return true;
    }

    private void updateFoodStock(String foodId, int quantity) {
        Food food = foodRepository.findById(foodId).orElse(null);
        
        if (food != null) {
            int currentStock = food.getStock();
            food.setStock(currentStock - quantity);
            
            if (food.getStock() <= 0) {
                food.setStock(0);
                food.setStatus("unavailable");
            }
            
            foodRepository.save(food);
        }
    }

    @Override
    public int batchUpdateOrderStatus(List<String> orderIds, String newStatus) {
        List<Order> orders = orderRepository.findAll();
        
        int successCount = 0;
        List<Order> ordersToUpdateStock = new ArrayList<>();
        
        for (String orderId : orderIds) {
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(orderId))
                    .findFirst()
                    .orElse(null);
            
            if (order != null) {
                String currentStatus = order.getStatus();
                if (isValidStatusTransition(currentStatus, newStatus)) {
                    if ("confirmed".equals(currentStatus) && "completed".equals(newStatus)) {
                        ordersToUpdateStock.add(order);
                    }
                    
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                    successCount++;
                }
            }
        }
        
        for (Order order : ordersToUpdateStock) {
            updateFoodStock(order.getFoodId(), order.getQuantity());
        }
        
        return successCount;
    }

    @Override
    public Map<String, Integer> getFoodOrderStatistics(String date) {
        List<Order> orders = orderRepository.findAll();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return orders.stream()
                .filter(order -> order.getOrderTime() != null && 
                        order.getOrderTime().format(formatter).equals(date))
                .filter(order -> !order.getStatus().equals("cancelled"))
                .collect(Collectors.groupingBy(Order::getFoodId, 
                        Collectors.summingInt(Order::getQuantity)));
    }

    @Override
    public Food getFoodById(String foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Map<String, Integer> getTodayStatistics(String date) {
        List<Order> orders = orderRepository.findAll();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        List<Order> todayOrders = orders.stream()
                .filter(order -> order.getOrderTime() != null && 
                        order.getOrderTime().format(formatter).equals(date))
                .filter(order -> !order.getStatus().equals("cancelled"))
                .collect(Collectors.toList());
        
        int totalOrders = todayOrders.size();
        int pendingOrders = (int) todayOrders.stream()
                .filter(order -> order.getStatus().equals("pending"))
                .count();
        int confirmedOrders = (int) todayOrders.stream()
                .filter(order -> order.getStatus().equals("confirmed"))
                .count();
        int completedOrders = (int) todayOrders.stream()
                .filter(order -> order.getStatus().equals("completed"))
                .count();
        
        int totalUsers = (int) todayOrders.stream()
                .map(Order::getUserId)
                .distinct()
                .count();
        
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("confirmedOrders", confirmedOrders);
        statistics.put("completedOrders", completedOrders);
        statistics.put("totalUsers", totalUsers);
        
        return statistics;
    }

    @Override
    public boolean updateFoodStatus(String foodId, String status) {
        Food food = foodRepository.findById(foodId).orElse(null);
        
        if (food == null) {
            return false;
        }
        
        food.setStatus(status);
        foodRepository.save(food);
        
        return true;
    }

    @Override
    public boolean deleteFood(String foodId) {
        Food food = foodRepository.findById(foodId).orElse(null);
        if (food == null) {
            return false;
        }
        foodRepository.deleteById(foodId);
        return true;
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "pending":
                return "confirmed".equals(newStatus);
            case "confirmed":
                return "completed".equals(newStatus);
            default:
                return false;
        }
    }
}
