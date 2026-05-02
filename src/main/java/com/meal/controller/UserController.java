package com.meal.controller;

import com.meal.entity.User;
import com.meal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户控制器
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String toLogin() {
        return "user/login";
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public String login(String username, String password, HttpSession session, Model model) {
        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            // 根据角色跳转到不同页面
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/index";
            } else if ("canteen".equals(user.getRole())) {
                return "redirect:/canteen/index";
            } else {
                return "redirect:/elder/index";
            }
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "user/login";
        }
    }

    /**
     * 用户退出
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    /**
     * 跳转到注册页面
     */
    @GetMapping("/register")
    public String toRegister() {
        return "user/register";
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public String register(User user, Model model) {
        // 检查用户名是否已存在
        List<User> users = userService.getAll();
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                model.addAttribute("error", "用户名已存在");
                return "user/register";
            }
        }
        // 设置默认角色为老人
        user.setRole("elder");
        userService.add(user);
        return "redirect:/user/login";
    }

    /**
     * 跳转到个人信息页面
     */
    @GetMapping("/profile")
    public String toProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "user/profile";
    }

    /**
     * 更新个人信息
     */
    @PostMapping("/update")
    public String update(User user, HttpSession session) {
        User oldUser = (User) session.getAttribute("user");
        user.setId(oldUser.getId());
        user.setUsername(oldUser.getUsername());
        user.setPassword(oldUser.getPassword());
        user.setRole(oldUser.getRole());
        userService.update(user);
        session.setAttribute("user", user);
        return "redirect:/user/profile";
    }

    /**
     * 跳转到用户列表页面（仅管理员）
     */
    @GetMapping("/list")
    public String toList(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        return "user/list";
    }

    /**
     * 跳转到添加用户页面（仅管理员）
     */
    @GetMapping("/add")
    public String toAdd(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        return "user/add";
    }

    /**
     * 添加用户（仅管理员）
     */
    @PostMapping("/add")
    public String add(User user) {
        userService.add(user);
        return "redirect:/user/list";
    }

    /**
     * 跳转到编辑用户页面（仅管理员）
     */
    @GetMapping("/edit/{id}")
    public String toEdit(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        User editUser = userService.getById(id);
        model.addAttribute("user", editUser);
        return "user/edit";
    }

    /**
     * 编辑用户（仅管理员）
     */
    @PostMapping("/edit")
    public String edit(User user) {
        userService.update(user);
        return "redirect:/user/list";
    }

    /**
     * 删除用户（仅管理员）
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        userService.delete(id);
        return "redirect:/user/list";
    }
}