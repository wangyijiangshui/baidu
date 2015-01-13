package com.duapp.action;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.duapp.util.CommonUtil;

/**
 * 用户登录和权限验证过滤器
 * 
 * @author JS
 * @date	2013-05-18
 */
public class LoginFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)req;
	    HttpServletResponse response = (HttpServletResponse)rsp; 
		String url = request.getRequestURL().toString();
		Object user = request.getSession().getAttribute("user");
		
		//首先进行登录权限验证，如果登录权限验证通过，则在进行资源访问权限验证，其中登录页面默认自动放行
		if (null != user || url.endsWith("index.jsp") || url.endsWith("login.do")) {
			chain.doFilter(request, response);
		//如果未登陆则踢出
		} else {
			//判断是哪种类型的请求(普通URL、AJAX),如果是AJAX请求，则进行ajax响应，否则直接跳转到登录页面
			String requestType = request.getHeader("X-Requested-With");
			if(requestType != null && requestType.equals("XMLHttpRequest")) {
				CommonUtil.sendJsonDataToClient("{\"noLogin\":true,\"rows\":[]}", response);
            } else {
            	request.getSession().invalidate();
            	response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
}