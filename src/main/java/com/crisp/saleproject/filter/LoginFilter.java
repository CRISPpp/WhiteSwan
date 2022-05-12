package com.crisp.saleproject.filter;

import com.alibaba.fastjson.JSON;
import com.crisp.saleproject.common.BaseContext;
import com.crisp.saleproject.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 1 获取请求URI
 * 2 判断是否需要处理
 * 3 不需要处理放行
 * 4 判断登录状态，登录放行
 * 5 未登录返回登录页面
 */
@Slf4j
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //log.info("拦截到请求：{}",request.getRequestURI());
        String uri = request.getRequestURI();
        log.info(String.valueOf(Thread.currentThread()));
        //不需要处理的url
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        String[] urlsBaned = {
                "/redis**",
                "/redis/**"
        };
        if(check(urlsBaned, uri)){
            response.getWriter().write(JSON.toJSONString(R.error("gun")));
            return;
        }
        boolean checkRet = check(urls, uri);
        if(checkRet){
            filterChain.doFilter(request, response);
            log.info("拦截到请求：{} : 放行",request.getRequestURI());
            return;
        }
        String[] urlBack = {
                "/backend/**",
                "/backend*"
        };
        String[] urlFront = {
                "front/**",
                "front*"
        };
        Long id = (Long) request.getSession().getAttribute("employee");
        Long user_id = (Long) request.getSession().getAttribute("user");
        if(id != null){
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request, response);
            log.info("拦截到请求：{} : 放行",request.getRequestURI());
            return;
        }

        //移动端用户
        if(request.getSession().getAttribute("user") != null){
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));
            filterChain.doFilter(request, response);
            log.info("拦截到请求：{} : 放行",request.getRequestURI());
            return;
        }

        //未登录则通过输出流向客户端响应数据，具体看request.js响应拦截器
        log.info("拦截到请求：{} : 拦截",request.getRequestURI());
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String uri){
        for(String url: urls){
            boolean ret = PATH_MATCHER.match(url, uri);
            if(ret) return true;
        }
        return false;
    }
}
