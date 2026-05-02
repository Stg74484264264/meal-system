package com.meal.service.impl;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.repository.FoodRepository;
import com.meal.repository.OrderRepository;
import com.meal.repository.UserRepository;
import com.meal.service.ElderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ElderServiceImpl implements ElderService {

    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ElderServiceImpl(FoodRepository foodRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Food> getTodayMenu(String mealType) {
        return foodRepository.findByStatus("available").stream()
                .filter(food -> food.getStock() > 0)
                .filter(food -> {
                    if (mealType == null || mealType.isEmpty()) {
                        return true;
                    }
                    String foodMealType = food.getMealType();
                    if (foodMealType == null) {
                        return false;
                    }
                    return mealType.equals(foodMealType);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Order createOrder(String userId, String foodId, int quantity, Date orderTime) {
        Food food = foodRepository.findById(foodId).orElse(null);
        
        if (food == null || "unavailable".equals(food.getStatus()) || food.getStock() < quantity) {
            return null;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setFoodId(foodId);
        order.setQuantity(quantity);
        order.setTotalPrice(food.getPrice() * quantity);
        order.setOrderTime(orderTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        order.setStatus("pending");

        food.setStock(food.getStock() - quantity);
        foodRepository.save(food);

        orderRepository.save(order);

        return order;
    }

    @Override
    public List<Order> getUserOrders(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<Food> foods = foodRepository.findAll();
        List<User> users = userRepository.findAll();

        return orders.stream()
                .map(order -> {
                    foods.stream()
                            .filter(food -> food.getId().equals(order.getFoodId()))
                            .findFirst()
                            .ifPresent(food -> {
                                order.setFoodName(food.getName());
                                order.setFoodImage(food.getImage());
                            });
                    
                    users.stream()
                            .filter(user -> user.getId().equals(order.getUserId()))
                            .findFirst()
                            .ifPresent(user -> order.setUserName(user.getName()));
                    
                    return order;
                })
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean cancelOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null || !"pending".equals(order.getStatus())) {
            return false;
        }

        Food food = foodRepository.findById(order.getFoodId()).orElse(null);
        if (food != null) {
            food.setStock(food.getStock() + order.getQuantity());
            foodRepository.save(food);
        }

        order.setStatus("cancelled");
        orderRepository.save(order);

        return true;
    }

    @Override
    public User getUserInfo(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public Food getFoodById(String foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }

    @Override
    public Order getOrderById(String orderId, String userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order != null && order.getUserId().equals(userId)) {
            Food food = foodRepository.findById(order.getFoodId()).orElse(null);
            if (food != null) {
                order.setFoodName(food.getName());
                order.setFoodImage(food.getImage());
            }
            
            User user = userRepository.findById(order.getUserId()).orElse(null);
            if (user != null) {
                order.setUserName(user.getName());
            }
        }
        
        return order;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) {
            return false;
        }
        
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        user.setPassword(newPassword);
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean confirmReceiveOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null || !"confirmed".equals(order.getStatus())) {
            return false;
        }

        order.setStatus("completed");
        orderRepository.save(order);

        return true;
    }
}
