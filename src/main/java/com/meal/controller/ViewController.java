package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.Notice;
import com.meal.service.FoodService;
import com.meal.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 视图控制器，用于处理页面跳转
 */
@Controller
public class ViewController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FoodService foodService;

    @GetMapping("/")
    public String toIndex() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        List<Notice> notices = noticeService.getLatest(3);
        model.addAttribute("notices", notices);
        
        List<Food> recommendedFoods = foodService.getAll().stream()
                .limit(6)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recommendedFoods", recommendedFoods);
        
        return "index";
    }
}