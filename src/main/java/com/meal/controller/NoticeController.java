package com.meal.controller;

import com.meal.entity.Notice;
import com.meal.entity.User;
import com.meal.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 公告控制器
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 跳转到公告列表页面
     */
    @GetMapping("/list")
    public String toList(Model model) {
        List<Notice> notices = noticeService.getAll();
        model.addAttribute("notices", notices);
        return "notice/list";
    }

    /**
     * 跳转到公告详情页面
     */
    @GetMapping("/detail/{id}")
    public String toDetail(@PathVariable String id, Model model) {
        Notice notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }

    /**
     * 跳转到公告管理页面（仅管理员）
     */
    @GetMapping("/manage")
    public String toManage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        List<Notice> notices = noticeService.getAll();
        model.addAttribute("notices", notices);
        return "notice/manage";
    }

    /**
     * 跳转到添加公告页面（仅管理员）
     */
    @GetMapping("/add")
    public String toAdd(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        return "notice/add";
    }

    /**
     * 添加公告（仅管理员）
     */
    @PostMapping("/add")
    public String add(Notice notice, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        noticeService.add(notice);
        return "redirect:/notice/manage";
    }

    /**
     * 跳转到编辑公告页面（仅管理员）
     */
    @GetMapping("/edit/{id}")
    public String toEdit(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        Notice notice = noticeService.getById(id);
        model.addAttribute("notice", notice);
        return "notice/edit";
    }

    /**
     * 编辑公告（仅管理员）
     */
    @PostMapping("/edit")
    public String edit(Notice notice, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        noticeService.update(notice);
        return "redirect:/notice/manage";
    }

    /**
     * 删除公告（仅管理员）
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/user/login";
        }
        noticeService.delete(id);
        return "redirect:/notice/manage";
    }
}