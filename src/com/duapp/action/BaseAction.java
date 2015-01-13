package com.duapp.action;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.duapp.vo.UserData;

/**
 * 
 * 
 * @author Administrator
 *
 */
public class BaseAction extends HttpServlet{

	/**
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public UserData getLoginUser(HttpServletRequest request) {
		Object userData = request.getSession().getAttribute("user");
		if(null != userData) {
			return (UserData)userData;
		} else {
			return null;
		}
	}
	
}
