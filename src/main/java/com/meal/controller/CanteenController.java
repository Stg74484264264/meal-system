package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.service.CanteenService;
import com.meal.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 食堂端控制器
 */
@Controller
@RequestMapping("/canteen")
public class CanteenController {

    @Autowired
    private CanteenService canteenService;

    /**
     * 食堂端首页
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 食堂端首页
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 获取当前日期
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // 获取今日统计信息
        Map<String, Integer> statistics = canteenService.getTodayStatistics(today);
        
        model.addAttribute("user", currentUser);
        model.addAttribute("today", today);
        model.addAttribute("statistics", statistics);
        
        return "canteen/index";
    }

    /**
     * 订单列表页面
     *
     * @param date   日期
     * @param status 订单状态
     * @param model  模型对象
     * @return 订单列表页面
     */
    @GetMapping("/orderList")
    public String orderList(@RequestParam(required = false, defaultValue = "") String date,
                           @RequestParam(required = false, defaultValue = "") String status,
                           HttpSession session,
                           Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 如果没有指定日期，使用当前日期
        if (date.isEmpty()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        
        // 获取订单列表
        List<Order> orders = canteenService.getTodayOrders(date, status);
        
        model.addAttribute("orders", orders);
        model.addAttribute("date", date);
        model.addAttribute("status", status);
        model.addAttribute("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        model.addAttribute("user", currentUser);
        
        return "canteen/orderList";
    }

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @param newStatus  新状态
     * @param date    日期
     * @param model   模型对象
     * @return 重定向到订单列表页面
     */
    @PostMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestParam("orderId") String orderId,
                                   @RequestParam("newStatus") String newStatus,
                                   @RequestParam("date") String date,
                                   HttpSession session,
                                   Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        boolean success = canteenService.updateOrderStatus(orderId, newStatus);
        
        if (success) {
            model.addAttribute("success", "订单状态更新成功");
        } else {
            model.addAttribute("error", "订单状态更新失败");
        }
        
        // 重定向到订单列表页面
        return "redirect:/canteen/orderList?date=" + date;
    }

    /**
     * 批量修改订单状态
     *
     * @param orderIds 订单ID列表
     * @param status   新状态
     * @param date     日期
     * @param model    模型对象
     * @return 重定向到订单列表页面
     */
    @PostMapping("/batchUpdateOrderStatus")
    public String batchUpdateOrderStatus(@RequestParam("orderIds") List<String> orderIds,
                                        @RequestParam("status") String status,
                                        @RequestParam("date") String date,
                                        HttpSession session,
                                        Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        if (orderIds == null || orderIds.isEmpty()) {
            model.addAttribute("error", "请选择要操作的订单");
            return "redirect:/canteen/orderList?date=" + date;
        }
        
        int successCount = canteenService.batchUpdateOrderStatus(orderIds, status);
        
        model.addAttribute("success", "成功更新 " + successCount + " 个订单状态");
        
        // 重定向到订单列表页面
        return "redirect:/canteen/orderList?date=" + date;
    }

    /**
     * 菜品核对页面
     *
     * @param date  日期
     * @param session 会话对象
     * @param model 模型对象
     * @return 菜品核对页面
     */
    @GetMapping("/foodCheck")
    public String foodCheck(@RequestParam(required = false, defaultValue = "") String date,
                           HttpSession session,
                           Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 如果没有指定日期，使用当前日期
        if (date.isEmpty()) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        
        // 获取菜品预约统计
        Map<String, Integer> foodStatistics = canteenService.getFoodOrderStatistics(date);
        
        // 获取所有菜品信息
        List<Food> allFoods = canteenService.getAllFoods();
        
        // 计算总计
        int totalQuantity = 0;
        for (Integer qty : foodStatistics.values()) {
            if (qty != null) {
                totalQuantity += qty;
            }
        }
        
        model.addAttribute("foodStatistics", foodStatistics);
        model.addAttribute("allFoods", allFoods);
        model.addAttribute("date", date);
        model.addAttribute("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        model.addAttribute("user", currentUser);
        model.addAttribute("totalQuantity", totalQuantity);
        
        return "canteen/foodCheck";
    }

    /**
     * 菜品管理页面
     *
     * @param session 会话对象
     * @param model 模型对象
     * @return 菜品管理页面
     */
    @GetMapping("/foodManage")
    public String foodManage(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        List<Food> foods = canteenService.getAllFoods();
        model.addAttribute("foods", foods);
        model.addAttribute("user", currentUser);
        
        return "canteen/foodManage";
    }

    /**
     * 修改菜品状态（上架/下架）
     *
     * @param foodId 菜品ID
     * @param status 新状态
     * @param session 会话对象
     * @param model  模型对象
     * @return 重定向到菜品管理页面
     */
    @PostMapping("/updateFoodStatus")
    public String updateFoodStatus(@RequestParam("foodId") String foodId,
                                   @RequestParam("status") String status,
                                   HttpSession session,
                                   Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        boolean success = canteenService.updateFoodStatus(foodId, status);
        
        if (success) {
            model.addAttribute("success", "菜品状态更新成功");
        } else {
            model.addAttribute("error", "菜品状态更新失败");
        }
        
        return "redirect:/canteen/foodManage";
    }

    /**
     * 删除菜品
     *
     * @param foodId 菜品ID
     * @param session 会话对象
     * @param model  模型对象
     * @return 重定向到菜品管理页面
     */
    @PostMapping("/deleteFood")
    public String deleteFood(@RequestParam("foodId") String foodId,
                             HttpSession session,
                             Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        boolean success = canteenService.deleteFood(foodId);
        
        if (success) {
            model.addAttribute("success", "菜品删除成功");
        } else {
            model.addAttribute("error", "菜品删除失败");
        }
        
        return "redirect:/canteen/foodManage";
    }

    /**
     * 获取订单状态的中文描述
     *
     * @param status 订单状态
     * @return 中文描述
     */
    public static String getOrderStatusText(String status) {
        switch (status) {
            case "pending":
                return "待配餐";
            case "confirmed":
                return "已配餐";
            case "completed":
                return "已领取";
            case "cancelled":
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取订单状态的样式类
     *
     * @param status 订单状态
     * @return 样式类
     */
    public static String getOrderStatusClass(String status) {
        switch (status) {
            case "pending":
                return "bg-yellow-100 text-yellow-800";
            case "confirmed":
                return "bg-blue-100 text-blue-800";
            case "completed":
                return "bg-green-100 text-green-800";
            case "cancelled":
                return "bg-red-100 text-red-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }
}