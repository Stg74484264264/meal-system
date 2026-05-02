package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.User;
import com.meal.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 餐食控制器
 */
@Controller
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 跳转到餐食列表页面
     */
    @GetMapping("/list")
    public String toList(Model model, @RequestParam(required = false) String category) {
        List<Food> foods;
        if (category != null && !category.isEmpty()) {
            foods = foodService.getByCategory(category);
        } else {
            foods = foodService.getAvailable();
        }
        model.addAttribute("foods", foods);
        model.addAttribute("category", category);
        
        // 获取所有分类
        List<String> categories = foods.stream()
                .map(Food::getCategory)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("categories", categories);
        
        return "food/list";
    }

    /**
     * 跳转到餐食详情页面
     */
    @GetMapping("/detail/{id}")
    public String toDetail(@PathVariable String id, Model model) {
        Food food = foodService.getById(id);
        model.addAttribute("food", food);
        return "food/detail";
    }

    /**
     * 跳转到餐食管理页面（仅管理员和食堂管理员）
     */
    @GetMapping("/manage")
    public String toManage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        List<Food> foods = foodService.getAll();
        model.addAttribute("foods", foods);
        return "food/manage";
    }

    /**
     * 跳转到添加餐食页面（仅管理员和食堂管理员）
     */
    @GetMapping("/add")
    public String toAdd(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        return "food/add";
    }

    /**
     * 添加餐食（仅管理员和食堂管理员）
     */
    @PostMapping("/add")
    public String add(Food food, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        // 处理图片上传
        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = session.getServletContext().getRealPath("/static/images/");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                File file = new File(uploadDir + fileName);
                imageFile.transferTo(file);
                food.setImage("/static/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        food.setStatus("available");
        foodService.add(food);
        return "redirect:/food/manage";
    }

    /**
     * 跳转到编辑餐食页面（仅管理员和食堂管理员）
     */
    @GetMapping("/edit/{id}")
    public String toEdit(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        Food food = foodService.getById(id);
        model.addAttribute("food", food);
        return "food/edit";
    }

    /**
     * 编辑餐食（仅管理员和食堂管理员）
     */
    @PostMapping("/edit")
    public String edit(Food food, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        // 处理图片上传
        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = session.getServletContext().getRealPath("/static/images/");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                File file = new File(uploadDir + fileName);
                imageFile.transferTo(file);
                food.setImage("/static/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        foodService.update(food);
        return "redirect:/food/manage";
    }

    /**
     * 删除餐食（仅管理员和食堂管理员）
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        foodService.delete(id);
        return "redirect:/food/manage";
    }

    /**
     * 更新餐食状态（仅管理员和食堂管理员）
     */
    @GetMapping("/status/{id}/{status}")
    public String updateStatus(@PathVariable String id, @PathVariable String status, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        Food food = foodService.getById(id);
        food.setStatus(status);
        foodService.update(food);
        return "redirect:/food/manage";
    }
}