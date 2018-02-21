package com.duapp.action;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.helper.StringUtil;

import com.duapp.util.CommonUtil;
import com.duapp.vo.BaseMessage;
import com.duapp.vo.UserData;

/**
 * @author	Administrator
 * @date	2018-02-21
 */
public class BaseAction extends HttpServlet{

	/**
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

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*try {
			String method = request.getParameter("method");
			if (StringUtil.isBlank(method)) {
				CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(false, "Plz post \"method\" parameter to location action method.")), response);
				return;
			}
			Method serverMethod = this.getClass().getMethod(method, HttpServletRequest.class, HttpServletRequest.class);
			serverMethod.invoke(request, response);
		} catch (Exception ex) {
			ex.printStackTrace();
			CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(false, ex.getMessage())), response);
		}*/
	}
}
