package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.Message;
import com.duapp.vo.UserData;

public class LoginAction extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String method = request.getParameter("method");
		//修改系统主题
		if("login".equals(method)) {
			this.login(request, response);
		} else if("exit".equals(method)) {
			this.exit(request, response);
		}
	}

	/**
	 *  修改系统主题
	 * 
	 * @param request
	 * @param response
	 */
	public void login(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("userName");
		boolean result = false;
		if (null != userName) {
			UserData user = this.queryTheme(userName);
			if(null != user) {
				result = true;
				request.getSession().setAttribute("user", user);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 *  修改系统主题
	 * 
	 * @param request
	 * @param response
	 */
	public void exit(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute("user");
		request.getSession().invalidate();
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(true)), response);
	}
	
	/**
	 * 验证用户信息的合法性并获取当前用户的主题
	 * 
	 * @param request
	 * @param response
	 */
	public UserData queryTheme(String userName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		UserData user = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT userName,userPass,userTheme FROM tbl_user where deleteFlag != 2 and userName='"+userName+"'";
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				String userNameTemp = rs.getString("userName");
				String userPassTemp = rs.getString("userPass");
				if(null != userNameTemp && null != userPassTemp && userNameTemp.equals(userName) && userPassTemp.equals(userName)) {
					user = new UserData();
					user.setUserName(userNameTemp);
					user.setUserTheme(rs.getString("userTheme"));
					System.out.println("user=="+CommonUtil.fromObjctToJson(user));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		return user;
	}
}
