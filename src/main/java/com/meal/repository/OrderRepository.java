package com.meal.repository;

import com.meal.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    
    List<Order> findByStatus(String status);
    
    List<Order> findByUserIdAndStatus(String userId, String status);
    
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Order> findByFoodId(String foodId);
    
    List<Order> findByStatusAndOrderTimeBetween(String status, LocalDateTime startTime, LocalDateTime endTime);
    
    int countByStatus(String status);
    
    int countByUserId(String userId);
}
