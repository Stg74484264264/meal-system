package com.meal.repository;

import com.meal.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {
    List<Food> findByStatus(String status);
    
    List<Food> findByMealType(String mealType);
    
    List<Food> findByStatusAndMealType(String status, String mealType);
    
    List<Food> findByCategory(String category);
    
    boolean existsByName(String name);
}
