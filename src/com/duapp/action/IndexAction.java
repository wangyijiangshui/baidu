package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.Message;
import com.duapp.vo.UserData;

public class IndexAction extends BaseAction{

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
		if("changeUiTheme".equals(method)) {
			this.changeUiTheme(request, response);
		}
	}

	/**
	 *  修改系统主题
	 * 
	 * @param request
	 * @param response
	 */
	public void changeUiTheme(HttpServletRequest request, HttpServletResponse response) {
		String theme = request.getParameter("theme");
		boolean result = false;
		UserData user = this.getLoginUser(request);
		if (null != user && null != theme) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql = null;
			try {
				conn = DBUtil.getConnection();
				sql = "update tbl_user set userTheme='"+theme+"' where userName='"+user.getUserName()+"'";
				pstmt = conn.prepareStatement(sql);
				if(pstmt.executeUpdate() > 0) {
					result = true;
					user.setUserTheme(theme);
					request.getSession().setAttribute("user", user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
}
