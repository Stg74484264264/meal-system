package com.meal.config;

import com.meal.util.SessionUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器，用于拦截未登录用户访问需要权限的页面
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 预处理请求，判断用户是否已登录
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @return 是否继续处理请求
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 排除不需要拦截的路径
        if (requestURI.equals("/login") || 
            requestURI.equals("/login/") ||
            requestURI.equals("/login/forgotPassword") ||
            requestURI.startsWith("/static/") ||
            requestURI.startsWith("/favicon.ico")) {
            return true;
        }
        
        // 检查用户是否已登录
        if (!SessionUtil.isLoggedIn(request.getSession())) {
            // 未登录，重定向到登录页面
            response.sendRedirect("/login");
            return false;
        }
        
        // 已登录，继续处理请求
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理后执行，此处不需要处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后执行，此处不需要处理
    }
}