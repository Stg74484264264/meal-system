package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.service.ElderService;
import com.meal.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 老人端控制器
 */
@Controller
@RequestMapping("/elder")
public class ElderController {

    @Autowired
    private ElderService elderService;

    /**
     * 老人端首页
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 老人端首页
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        return "elder/index";
    }

    /**
     * 查看当日菜单
     *
     * @param mealType 餐型
     * @param model    模型对象
     * @return 菜单页面
     */
    @GetMapping("/menu")
    public String menu(@RequestParam(required = false, defaultValue = "") String mealType, Model model) {
        List<Food> foods = elderService.getTodayMenu(mealType);
        
        // 获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String today = sdf.format(new Date());
        
        model.addAttribute("foods", foods);
        model.addAttribute("mealType", mealType);
        model.addAttribute("today", today);
        model.addAttribute("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        return "elder/menu";
    }

    /**
     * 跳转到预约页面
     *
     * @param foodId 餐食ID
     * @param model  模型对象
     * @return 预约页面
     */
    @GetMapping("/reserve")
    public String toReserve(@RequestParam("foodId") String foodId, Model model) {
        Food food = elderService.getFoodById(foodId);
        if (food == null) {
            model.addAttribute("error", "餐食不存在");
            return "elder/menu";
        }
        
        // 设置默认预约日期为今天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        
        model.addAttribute("food", food);
        model.addAttribute("today", today);
        
        return "elder/reserve";
    }

    /**
     * 提交预约
     *
     * @param foodId    餐食ID
     * @param quantity  数量
     * @param orderTime 预约时间
     * @param session   会话对象
     * @param model     模型对象
     * @return 预约结果页面
     */
    @PostMapping("/reserve")
    public String reserve(@RequestParam("foodId") String foodId,
                          @RequestParam("quantity") int quantity,
                          @RequestParam("orderTime") String orderTime,
                          HttpSession session,
                          Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            // 解析预约时间
            Date orderDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderTime);
            Date today = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            
            // 检查是否预约过去的日期
            if (orderDate.before(today)) {
                model.addAttribute("error", "不能预约过去的日期");
                Food food = elderService.getFoodById(foodId);
                model.addAttribute("food", food);
                model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(today));
                return "elder/reserve";
            }
            
            // 创建订单
            Order order = elderService.createOrder(currentUser.getId(), foodId, quantity, orderDate);
            
            if (order != null) {
                model.addAttribute("success", "预约成功！");
                model.addAttribute("order", order);
                return "elder/reserve_success";
            } else {
                model.addAttribute("error", "预约失败，请检查餐食库存");
                Food food = elderService.getFoodById(foodId);
                model.addAttribute("food", food);
                model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(today));
                return "elder/reserve";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "预约失败，请稍后重试");
            Food food = elderService.getFoodById(foodId);
            model.addAttribute("food", food);
            model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            return "elder/reserve";
        }
    }

    /**
     * 查看我的订单
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 订单列表页面
     */
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        List<Order> orders = elderService.getUserOrders(currentUser.getId());
        model.addAttribute("orders", orders);
        
        return "elder/orders";
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param session 会话对象
     * @param model   模型对象
     * @return 订单列表页面
     */
    @PostMapping("/cancelOrder")
    public String cancelOrder(@RequestParam("orderId") String orderId, HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        boolean success = elderService.cancelOrder(orderId, currentUser.getId());
        
        if (success) {
            model.addAttribute("success", "订单已成功取消");
        } else {
            model.addAttribute("error", "订单取消失败！只有状态为「待配餐」的订单才能取消");
        }
        
        // 重新获取订单列表
        List<Order> orders = elderService.getUserOrders(currentUser.getId());
        model.addAttribute("orders", orders);
        
        return "elder/orders";
    }

    /**
     * 查看个人信息
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 个人信息页面
     */
    @GetMapping("/personal")
    public String personal(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 获取最新的用户信息
        User user = elderService.getUserInfo(currentUser.getId());
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", currentUser);
        }
        
        return "elder/personal";
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
                return "已确认";
            case "completed":
                return "已完成";
            case "cancelled":
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @param session   会话对象
     * @param model     模型对象
     * @return 个人信息页面
     */
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 验证新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致");
            User user = elderService.getUserInfo(currentUser.getId());
            model.addAttribute("user", user != null ? user : currentUser);
            return "elder/personal";
        }
        
        // 验证新密码长度
        if (newPassword.length() < 6) {
            model.addAttribute("error", "新密码长度不能少于6位");
            User user = elderService.getUserInfo(currentUser.getId());
            model.addAttribute("user", user != null ? user : currentUser);
            return "elder/personal";
        }
        
        // 调用服务修改密码
        boolean success = elderService.changePassword(currentUser.getId(), oldPassword, newPassword);
        
        if (success) {
            // 清除session，强制重新登录
            session.invalidate();
            // 返回密码修改成功页面
            return "passwordChanged";
        } else {
            model.addAttribute("error", "密码修改失败，旧密码不正确");
            User user = elderService.getUserInfo(currentUser.getId());
            model.addAttribute("user", user != null ? user : currentUser);
            return "elder/personal";
        }
    }

    /**
     * 确认领取订单
     *
     * @param orderId 订单ID
     * @param session 会话对象
     * @param model   模型对象
     * @return 订单列表页面
     */
    @PostMapping("/confirmReceiveOrder")
    public String confirmReceiveOrder(@RequestParam("orderId") String orderId,
                                      HttpSession session,
                                      Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        boolean success = elderService.confirmReceiveOrder(orderId, currentUser.getId());
        
        if (success) {
            model.addAttribute("success", "订单已确认领取！");
        } else {
            model.addAttribute("error", "订单确认领取失败，可能订单状态已变更");
        }
        
        // 重新获取订单列表
        List<Order> orders = elderService.getUserOrders(currentUser.getId());
        model.addAttribute("orders", orders);
        
        return "elder/orders";
    }

    /**
     * 获取餐型的中文描述
     *
     * @param mealType 餐型
     * @return 中文描述
     */
    public static String getMealTypeText(String mealType) {
        switch (mealType) {
            case "breakfast":
                return "早餐";
            case "lunch":
                return "午餐";
            case "dinner":
                return "晚餐";
            default:
                return "全部餐食";
        }
    }
}