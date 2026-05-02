package com.meal.service.impl;

import com.meal.entity.Food;
import com.meal.entity.Notice;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.repository.FoodRepository;
import com.meal.repository.NoticeRepository;
import com.meal.repository.OrderRepository;
import com.meal.repository.UserRepository;
import com.meal.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final NoticeRepository noticeRepository;

    @Value("${meal.upload.path}")
    private String uploadPath;

    public AdminServiceImpl(UserRepository userRepository, FoodRepository foodRepository,
                           OrderRepository orderRepository, NoticeRepository noticeRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
        this.noticeRepository = noticeRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean addUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }
        
        user.setId(UUID.randomUUID().toString());
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || "admin".equals(user.getUsername()) || "canteen".equals(user.getUsername())) {
            return false;
        }
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(String foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }

    @Override
    public boolean addFood(Food food) {
        food.setId(UUID.randomUUID().toString());
        food.setStatus("available");
        foodRepository.save(food);
        return true;
    }

    @Override
    public boolean updateFood(Food food) {
        if (!foodRepository.existsById(food.getId())) {
            return false;
        }
        foodRepository.save(food);
        return true;
    }

    @Override
    public boolean deleteFood(String foodId) {
        Food food = foodRepository.findById(foodId).orElse(null);
        if (food == null) {
            return false;
        }

        if (food.getImage() != null && !food.getImage().isEmpty()) {
            File imageFile = new File(uploadPath + "food/" + food.getImage());
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }

        foodRepository.deleteById(foodId);
        return true;
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
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreateTimeDesc();
    }

    @Override
    public Notice getNoticeById(String noticeId) {
        return noticeRepository.findById(noticeId).orElse(null);
    }

    @Override
    public boolean addNotice(Notice notice) {
        notice.setId(UUID.randomUUID().toString());
        noticeRepository.save(notice);
        return true;
    }

    @Override
    public boolean deleteNotice(String noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            return false;
        }
        noticeRepository.deleteById(noticeId);
        return true;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getSystemStatistics() {
        int totalUsers = (int) userRepository.count();
        int totalFoods = (int) foodRepository.count();
        int totalOrders = (int) orderRepository.count();
        
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int todayOrders = (int) orderRepository.findAll().stream()
                .filter(order -> order.getOrderTime().toString().startsWith(today))
                .count();
        
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("totalFoods", totalFoods);
        statistics.put("totalOrders", totalOrders);
        statistics.put("todayOrders", todayOrders);
        
        return statistics;
    }

    @Override
    public Map<String, Integer> getOrderTrend(int days) {
        Map<String, Integer> trend = new LinkedHashMap<>();
        
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        
        for (int i = days - 1; i >= 0; i--) {
            String date = today.minusDays(i).format(sdf);
            trend.put(date, 0);
        }
        
        orderRepository.findAll().forEach(order -> {
            String orderDate = order.getOrderTime().format(sdf);
            if (trend.containsKey(orderDate)) {
                trend.put(orderDate, trend.get(orderDate) + 1);
            }
        });
        
        return trend;
    }

    @Override
    public boolean resetPassword(String userId) {
        return resetPassword(userId, "123456");
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public boolean resetPassword(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPassword(password);
        userRepository.save(user);
        return true;
    }
}
