package com.meal.controller;

import com.meal.entity.User;
import com.meal.service.AdminService;
import com.meal.service.LoginService;
import com.meal.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * 登录控制器
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AdminService adminService;

    /**
     * 跳转到登录页面
     *
     * @return 登录页面
     */
    @GetMapping
    public String toLogin() {
        return "login";
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @param session  会话对象
     * @param model    模型对象
     * @return 登录成功跳转到对应角色首页，失败返回登录页面
     */
    @PostMapping
    public String login(String username, String password, HttpSession session, Model model) {
        // 验证用户名和密码
        User user = loginService.login(username, password);
        
        if (user != null) {
            // 登录成功，保存用户到会话
            SessionUtil.saveUser(session, user);
            
            // 根据用户角色跳转到对应首页
            String role = user.getRole();
            switch (role) {
                case "admin":
                    return "redirect:/admin/index";
                case "canteen":
                    return "redirect:/canteen/index";
                case "elder":
                    return "redirect:/elder/index";
                default:
                    // 未知角色，跳转到系统首页
                    return "redirect:/index";
            }
        } else {
            // 登录失败，返回错误信息
            model.addAttribute("error", "账号或密码错误");
            return "login";
        }
    }

    /**
     * 用户退出登录
     *
     * @param session 会话对象
     * @return 跳转到登录页面
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 移除用户会话
        SessionUtil.removeUser(session);
        // 销毁会话
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * 未登录拦截处理
     *
     * @param model 模型对象
     * @return 未登录提示页面
     */
    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        model.addAttribute("message", "请先登录后再访问");
        return "login";
    }

    /**
     * 跳转到忘记密码页面
     *
     * @return 忘记密码页面
     */
    @GetMapping("/forgotPassword")
    public String toForgotPassword() {
        return "forgotPassword";
    }

    /**
     * 忘记密码处理 - 重置密码为默认密码123456
     *
     * @param username 用户名
     * @param model    模型对象
     * @return 忘记密码页面（显示结果）
     */
    @PostMapping("/forgotPassword")
    public String forgotPassword(String username, Model model) {
        // 根据用户名查找用户
        User user = adminService.getUserByUsername(username);
        
        if (user == null) {
            model.addAttribute("error", "用户名不存在");
            return "forgotPassword";
        }
        
        // 重置密码为默认密码123456
        boolean success = adminService.resetPassword(user.getId(), "123456");
        
        if (success) {
            model.addAttribute("success", "密码已重置为默认密码：123456，请使用新密码登录");
        } else {
            model.addAttribute("error", "密码重置失败，请稍后重试");
        }
        
        return "forgotPassword";
    }
}