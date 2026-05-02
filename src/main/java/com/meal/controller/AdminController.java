package com.meal.controller;

import com.meal.entity.Food;
import com.meal.entity.Notice;
import com.meal.entity.Order;
import com.meal.entity.User;
import com.meal.service.AdminService;
import com.meal.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员端控制器
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Value("${meal.upload.path}")
    private String uploadPath;

    /**
     * 管理员首页
     *
     * @param session 会话对象
     * @param model   模型对象
     * @return 管理员首页
     */
    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 获取系统统计信息
        Map<String, Integer> statistics = adminService.getSystemStatistics();
        
        // 获取订单趋势
        Map<String, Integer> orderTrend = adminService.getOrderTrend(7);

        model.addAttribute("user", currentUser);
        model.addAttribute("statistics", statistics);
        model.addAttribute("orderTrend", orderTrend);

        return "admin/index";
    }

    /**
     * 用户管理页面
     *
     * @param model 模型对象
     * @return 用户管理页面
     */
    @GetMapping("/userManage")
    public String userManage(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/userManage";
    }

    /**
     * 新增用户页面
     *
     * @param model 模型对象
     * @return 新增用户页面
     */
    @GetMapping("/addUser")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("action", "add");
        return "admin/userForm";
    }

    /**
     * 编辑用户页面
     *
     * @param userId 用户ID
     * @param model  模型对象
     * @return 编辑用户页面
     */
    @GetMapping("/editUser")
    public String editUser(@RequestParam("userId") String userId, Model model) {
        User user = adminService.getUserById(userId);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "redirect:/admin/userManage";
        }
        model.addAttribute("user", user);
        model.addAttribute("action", "edit");
        return "admin/userForm";
    }

    /**
     * 保存用户信息
     *
     * @param user   用户信息
     * @param action 操作类型：add/edit
     * @param model  模型对象
     * @return 重定向到用户管理页面
     */
    @PostMapping("/saveUser")
    public String saveUser(User user, @RequestParam("action") String action, Model model) {
        boolean success;
        
        if ("add".equals(action)) {
            // 设置默认密码
            user.setPassword("123456");
            success = adminService.addUser(user);
            if (success) {
                model.addAttribute("success", "用户新增成功");
            } else {
                model.addAttribute("error", "用户名已存在");
                return "redirect:/admin/addUser";
            }
        } else {
            success = adminService.updateUser(user);
            if (success) {
                model.addAttribute("success", "用户信息更新成功");
            } else {
                model.addAttribute("error", "用户更新失败");
                return "redirect:/admin/editUser?userId=" + user.getId();
            }
        }
        
        return "redirect:/admin/userManage";
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @param model  模型对象
     * @return 重定向到用户管理页面
     */
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") String userId, Model model) {
        boolean success = adminService.deleteUser(userId);
        if (success) {
            model.addAttribute("success", "用户删除成功");
        } else {
            model.addAttribute("error", "用户删除失败，可能是系统预设账号");
        }
        return "redirect:/admin/userManage";
    }

    /**
     * 初始化用户密码（重置为默认密码123456）
     *
     * @param userId 用户ID
     * @param model  模型对象
     * @return 重定向到用户管理页面
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("userId") String userId, Model model) {
        boolean success = adminService.resetPassword(userId);
        if (success) {
            model.addAttribute("success", "密码已重置为默认密码：123456");
        } else {
            model.addAttribute("error", "密码重置失败");
        }
        return "redirect:/admin/userManage";
    }

    /**
     * 菜品管理页面
     *
     * @param model 模型对象
     * @return 菜品管理页面
     */
    @GetMapping("/foodManage")
    public String foodManage(Model model) {
        List<Food> foods = adminService.getAllFoods();
        model.addAttribute("foods", foods);
        return "admin/foodManage";
    }

    /**
     * 新增菜品页面
     *
     * @param model 模型对象
     * @return 新增菜品页面
     */
    @GetMapping("/addFood")
    public String addFood(Model model) {
        model.addAttribute("food", new Food());
        model.addAttribute("action", "add");
        return "admin/foodForm";
    }

    /**
     * 编辑菜品页面
     *
     * @param foodId 菜品ID
     * @param model  模型对象
     * @return 编辑菜品页面
     */
    @GetMapping("/editFood")
    public String editFood(@RequestParam("foodId") String foodId, Model model) {
        Food food = adminService.getFoodById(foodId);
        if (food == null) {
            model.addAttribute("error", "菜品不存在");
            return "redirect:/admin/foodManage";
        }
        model.addAttribute("food", food);
        model.addAttribute("action", "edit");
        return "admin/foodForm";
    }

    /**
     * 保存菜品信息
     *
     * @param food   菜品信息
     * @param image  菜品图片
     * @param action 操作类型：add/edit
     * @param model  模型对象
     * @return 重定向到菜品管理页面
     */
    @PostMapping("/saveFood")
    public String saveFood(@RequestParam(value = "id", required = false) String id,
                          @RequestParam("name") String name,
                          @RequestParam("description") String description,
                          @RequestParam("price") double price,
                          @RequestParam("category") String category,
                          @RequestParam("mealType") String mealType,
                          @RequestParam("stock") int stock,
                          @RequestParam(value = "image", required = false) MultipartFile image,
                          @RequestParam("action") String action, Model model) {
        boolean success;
        Food food = new Food();
        food.setId(id);
        food.setName(name);
        food.setDescription(description);
        food.setPrice(price);
        food.setCategory(category);
        food.setMealType(mealType);
        food.setStock(stock);
        
        // 如果是编辑操作，先获取原有图片路径和其他属性
        if ("edit".equals(action) && !id.isEmpty()) {
            Food existingFood = adminService.getFoodById(id);
            if (existingFood != null) {
                food.setImage(existingFood.getImage());
                food.setStatus(existingFood.getStatus());
                food.setCreateTime(existingFood.getCreateTime());
                if (mealType == null || mealType.isEmpty()) {
                    food.setMealType(existingFood.getMealType());
                }
            }
        } else {
            food.setStatus("available");
            food.setCreateTime(java.time.LocalDateTime.now());
        }
        
        // 处理图片上传
        if (image != null && !image.isEmpty()) {
            try {
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + extension;
                String filePath = uploadPath + "food/" + fileName;
                
                File uploadDir = new File(uploadPath + "food/");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                image.transferTo(new File(filePath));
                food.setImage("food/" + fileName);
            } catch (IOException e) {
                model.addAttribute("error", "图片上传失败");
                return "redirect:/admin/" + ("add".equals(action) ? "addFood" : "editFood?foodId=" + id);
            }
        }
        
        food.setUpdateTime(java.time.LocalDateTime.now());
        
        if ("add".equals(action)) {
            success = adminService.addFood(food);
            if (success) {
                model.addAttribute("success", "菜品新增成功");
            } else {
                model.addAttribute("error", "菜品新增失败");
                return "redirect:/admin/addFood";
            }
        } else {
            success = adminService.updateFood(food);
            if (success) {
                model.addAttribute("success", "菜品信息更新成功");
            } else {
                model.addAttribute("error", "菜品更新失败");
                return "redirect:/admin/editFood?foodId=" + id;
            }
        }
        
        return "redirect:/admin/foodManage";
    }

    /**
     * 删除菜品
     *
     * @param foodId 菜品ID
     * @param model  模型对象
     * @return 重定向到菜品管理页面
     */
    @PostMapping("/deleteFood")
    public String deleteFood(@RequestParam("foodId") String foodId, Model model) {
        boolean success = adminService.deleteFood(foodId);
        if (success) {
            model.addAttribute("success", "菜品删除成功");
        } else {
            model.addAttribute("error", "菜品删除失败");
        }
        return "redirect:/admin/foodManage";
    }

    /**
     * 修改菜品状态
     *
     * @param foodId 菜品ID
     * @param status 状态：available/unavailable
     * @param model  模型对象
     * @return 重定向到菜品管理页面
     */
    @PostMapping("/updateFoodStatus")
    public String updateFoodStatus(@RequestParam("foodId") String foodId, 
                                 @RequestParam("status") String status, Model model) {
        boolean success = adminService.updateFoodStatus(foodId, status);
        if (success) {
            model.addAttribute("success", "菜品状态更新成功");
        } else {
            model.addAttribute("error", "菜品状态更新失败");
        }
        return "redirect:/admin/foodManage";
    }

    /**
     * 公告管理页面
     *
     * @param model 模型对象
     * @return 公告管理页面
     */
    @GetMapping("/noticeManage")
    public String noticeManage(Model model) {
        List<Notice> notices = adminService.getAllNotices();
        model.addAttribute("notices", notices);
        return "admin/noticeManage";
    }

    /**
     * 新增公告页面
     *
     * @param model 模型对象
     * @return 新增公告页面
     */
    @GetMapping("/addNotice")
    public String addNotice(Model model) {
        model.addAttribute("notice", new Notice());
        model.addAttribute("action", "add");
        return "admin/noticeForm";
    }

    /**
     * 保存公告信息
     *
     * @param notice 公告信息
     * @param model  模型对象
     * @return 重定向到公告管理页面
     */
    @PostMapping("/saveNotice")
    public String saveNotice(Notice notice, Model model) {
        boolean success = adminService.addNotice(notice);
        if (success) {
            model.addAttribute("success", "公告发布成功");
        } else {
            model.addAttribute("error", "公告发布失败");
            return "redirect:/admin/addNotice";
        }
        return "redirect:/admin/noticeManage";
    }

    /**
     * 删除公告
     *
     * @param noticeId 公告ID
     * @param model    模型对象
     * @return 重定向到公告管理页面
     */
    @PostMapping("/deleteNotice")
    public String deleteNotice(@RequestParam("noticeId") String noticeId, Model model) {
        boolean success = adminService.deleteNotice(noticeId);
        if (success) {
            model.addAttribute("success", "公告删除成功");
        } else {
            model.addAttribute("error", "公告删除失败");
        }
        return "redirect:/admin/noticeManage";
    }

    /**
     * 订单统计页面
     *
     * @param model 模型对象
     * @return 订单统计页面
     */
    @GetMapping("/orderStatistics")
    public String orderStatistics(Model model) {
        List<Order> orders = adminService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orderStatistics";
    }

    /**
     * 获取用户角色的中文描述
     *
     * @param role 用户角色
     * @return 中文描述
     */
    public static String getUserRoleText(String role) {
        switch (role) {
            case "admin":
                return "系统管理员";
            case "canteen":
                return "食堂管理员";
            case "elder":
                return "老人用户";
            default:
                return "未知角色";
        }
    }

    /**
     * 获取菜品状态的中文描述
     *
     * @param status 菜品状态
     * @return 中文描述
     */
    public static String getFoodStatusText(String status) {
        switch (status) {
            case "available":
                return "在售";
            case "unavailable":
                return "下架";
            default:
                return "未知状态";
        }
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
}