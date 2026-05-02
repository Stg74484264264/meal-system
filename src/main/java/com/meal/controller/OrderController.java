package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.service.FoodService;
import com.meal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 订单控制器
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private FoodService foodService;

    /**
     * 跳转到创建订单页面
     */
    @GetMapping("/create/{foodId}")
    public String toCreate(@PathVariable String foodId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        Food food = foodService.getById(foodId);
        model.addAttribute("food", food);
        return "order/create";
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public String create(Order order, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        order.setUserId(user.getId());
        boolean result = orderService.add(order);
        if (result) {
            return "redirect:/order/list";
        } else {
            model.addAttribute("error", "创建订单失败，请检查餐食库存");
            Food food = foodService.getById(order.getFoodId());
            model.addAttribute("food", food);
            return "order/create";
        }
    }

    /**
     * 跳转到个人订单列表页面
     */
    @GetMapping("/list")
    public String toList(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        List<Order> orders = orderService.getByUserId(user.getId());
        model.addAttribute("orders", orders);
        return "order/list";
    }

    /**
     * 跳转到订单详情页面
     */
    @GetMapping("/detail/{id}")
    public String toDetail(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        Order order = orderService.getOrderDetail(id);
        // 检查权限，只有订单所属用户、管理员和食堂管理员可以查看
        if (!order.getUserId().equals(user.getId()) && !"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    /**
     * 取消订单
     */
    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable String id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        Order order = orderService.getById(id);
        // 检查权限，只有订单所属用户、管理员和食堂管理员可以取消
        if (!order.getUserId().equals(user.getId()) && !"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        orderService.cancel(id);
        return "redirect:/order/list";
    }

    /**
     * 跳转到订单管理页面（仅管理员和食堂管理员）
     */
    @GetMapping("/manage")
    public String toManage(Model model, HttpSession session, @RequestParam(required = false) String status) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.getByStatus(status);
        } else {
            orders = orderService.getAll();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("status", status);
        return "order/manage";
    }

    /**
     * 完成订单（仅管理员和食堂管理员）
     */
    @GetMapping("/complete/{id}")
    public String complete(@PathVariable String id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole()) && !"canteen".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        orderService.complete(id);
        return "redirect:/order/manage";
    }
}