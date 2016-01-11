package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.Message;
import com.duapp.vo.StockRemark;
import com.duapp.vo.TaskCome;

public class TaskAction extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public TaskAction() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

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
		//新增任务
		if("addTask".equals(method)) {
			this.addTask(request, response);
		//查询所有的任务来源
		} else if ("queryComes".equals(method)){
			this.queryComes(request, response);
		//保存任务状态+备注修改数据
		} else if ("statusChange".equals(method)) {
			this.statusChange(request, response);
		//删除任务
		} else if ("deleteTask".equals(method)) {
			this.deleteTask(request, response);
		//加载指定任务的历史详细备注信息
		} else if ("loadHistoryRemark".equals(method)) {
			this.loadHistoryRemark(request, response);
		}
	}
	
	/**
	 * 删除任务
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteTask(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		String sql = null;
		String id = request.getParameter("id");
		boolean result = false;
		if (null != id) {
			try {
				conn = DBUtil.getConnection();
				stmt = conn.createStatement();
				sql = "update tbl_task set deleteFlag=2,deleteTime=now() where id="+id;
				if(stmt.executeUpdate(sql) > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(stmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 保存任务状态+备注修改数据
	 * 
	 * @param request
	 * @param response
	 */
	public void statusChange(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		String remark = request.getParameter("remark");
		String status = request.getParameter("status");
		String id = request.getParameter("id");
		boolean result = false;
		if (null != id) {
			try {
				conn = DBUtil.getConnection();
				sql = "update tbl_task set taskStatus='"+status+"',finishTime=now() "+((null != remark && !"".equals(remark)) ? ",remark=?":"")+" where id="+id;
				pstmt = conn.prepareStatement(sql);
				if(null != remark && !"".equals(remark)) {
					pstmt.setString(1, remark);
				}
				if(pstmt.executeUpdate() > 0) {
					if (null != remark && !"".equals(remark)) {
						sql = "insert into `tbl_task_remark`(`taskId`,`remark`,`createTime`) VALUES("+id+",?,now()) ;";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, remark);
						if(pstmt.executeUpdate() > 0) {
							result = true;
						}
					} else {
						result = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}

	/**
	 * 新增任务
	 * 
	 * @param request
	 * @param response
	 */
	public void addTask(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		String task = request.getParameter("task");
		String taskType = request.getParameter("taskType");
		String taskVolume = request.getParameter("taskVolume");
		String taskUrgency = request.getParameter("taskUrgency");
		String taskStatus = request.getParameter("taskStatus");
		String taskCome = request.getParameter("taskCome");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		boolean result = false;
		if (null != task && !"".equals(task)) {
			try {
				conn = DBUtil.getConnection();
				sql = "INSERT INTO `tbl_task`(`task`,`taskType`,`taskVolume`,"
			            	+"`taskUrgency`,`taskStatus`,`taskCome`,`startTime`,`endTime`,`createTime`)"
			            +"VALUES (?,'"+taskType+"','"+taskVolume+"','"+taskUrgency+"','"+taskStatus+"',"
			            		+"'"+taskCome+"','"+startTime+"','"+endTime+"',now());";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, task);
				if(pstmt.executeUpdate() > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, conn);
			}
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new Message(result)), response);
	}
	
	/**
	 * 查询所有的任务来源
	 * 
	 * @param request
	 * @param response
	 */
	public void queryComes(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<TaskCome> comes = new ArrayList<TaskCome>();
		TaskCome come = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT `id`,`taskCome` FROM `tbl_task_come` WHERE STATUS=1 order by id desc";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				come = new TaskCome();
				come.setId(rs.getString("id"));
				come.setTaskCome(rs.getString("taskCome"));
				comes.add(come);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(comes), response);
	}
	
	/**
	 * 加载指定任务的历史详细备注信息
	 * 
	 * @param request
	 * @param response
	 */
	public void loadHistoryRemark(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		List<StockRemark> remarks = new ArrayList<StockRemark>();
		StockRemark remark = null;
		String id = request.getParameter("id");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT remark,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime  FROM `tbl_task_remark` WHERE taskId="+id+" ORDER BY id DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				remark = new StockRemark();
				remark.setRemark(rs.getString("remark"));
				remark.setCreateTime(rs.getString("createTime"));
				remarks.add(remark);
			}
			sql = "SELECT task,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime  FROM `tbl_task` WHERE id="+id;
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				remark = new StockRemark();
				remark.setRemark(rs.getString("task"));
				remark.setCreateTime(rs.getString("createTime"));
				remarks.add(remark);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(remarks), response);
	}
}
