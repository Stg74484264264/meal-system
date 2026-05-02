package com.meal.service.impl;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.repository.FoodRepository;
import com.meal.repository.OrderRepository;
import com.meal.repository.UserRepository;
import com.meal.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, FoodRepository foodRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order getById(String id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            fillOrderDetail(order);
        }
        return order;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = orderRepository.findAll();
        return fillOrderDetails(orders);
    }

    @Override
    public List<Order> getByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return fillOrderDetails(orders);
    }

    @Override
    public List<Order> getByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return fillOrderDetails(orders);
    }

    @Override
    public boolean add(Order order) {
        Food food = foodRepository.findById(order.getFoodId()).orElse(null);
        if (food == null || !"available".equals(food.getStatus()) || food.getStock() < order.getQuantity()) {
            return false;
        }

        order.setTotalPrice(food.getPrice() * order.getQuantity());
        order.setId(UUID.randomUUID().toString());
        order.setStatus("pending");
        
        orderRepository.save(order);
        
        food.setStock(food.getStock() - order.getQuantity());
        foodRepository.save(food);
        
        return true;
    }

    @Override
    public void update(Order order) {
        orderRepository.save(order);
    }

    @Override
    public boolean cancel(String id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || !"pending".equals(order.getStatus())) {
            return false;
        }

        order.setStatus("cancelled");
        orderRepository.save(order);

        Food food = foodRepository.findById(order.getFoodId()).orElse(null);
        if (food != null) {
            food.setStock(food.getStock() + order.getQuantity());
            foodRepository.save(food);
        }

        return true;
    }

    @Override
    public void complete(String id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null && "pending".equals(order.getStatus())) {
            order.setStatus("completed");
            orderRepository.save(order);
        }
    }

    @Override
    public Order getOrderDetail(String id) {
        return getById(id);
    }

    @Override
    public List<Order> getOrderDetails(List<Order> orders) {
        return fillOrderDetails(orders);
    }

    private void fillOrderDetail(Order order) {
        User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            order.setUserName(user.getName());
        }

        Food food = foodRepository.findById(order.getFoodId()).orElse(null);
        if (food != null) {
            order.setFoodName(food.getName());
            order.setFoodImage(food.getImage());
        }
    }

    private List<Order> fillOrderDetails(List<Order> orders) {
        for (Order order : orders) {
            fillOrderDetail(order);
        }
        return orders;
    }
}
