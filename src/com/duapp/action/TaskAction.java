package com.duapp.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.helper.StringUtil;

import com.duapp.util.CommonUtil;
import com.duapp.util.DBUtil;
import com.duapp.vo.BaseMessage;
import com.duapp.vo.Task;
import com.duapp.vo.TaskCome;
import com.duapp.vo.TaskRemark;

/**
 * @author	Administrator
 * @date	2018-02-21
 */
public class TaskAction extends BaseAction {

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
		} else if ("updateTask".equals(method)) {
			this.updateTask(request, response);
		//删除任务
		} else if ("deleteTask".equals(method)) {
			this.deleteTask(request, response);
		//加载指定任务的历史详细备注信息
		} else if ("loadHistoryRemark".equals(method)) {
			this.loadHistoryRemark(request, response);
		//Add or update task remark 
		} else if ("addOrUpdateTaskRemark".equals(method)) {
			this.addOrUpdateTaskRemark(request, response);
		} else if ("loadTaskById".equals(method)) {
			this.loadTaskById(request, response);
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
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(result)), response);
	}
	
	/**
	 * 保存任务状态+备注修改数据
	 * 
	 * @param request
	 * @param response
	 */
	public void updateTask(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		String task = request.getParameter("task");
		String taskStatus = request.getParameter("taskStatus");
		String taskId = request.getParameter("taskId");
		boolean result = false;
		try {
			conn = DBUtil.getConnection();
			sql = "update tbl_task set task=?,taskStatus=?,finishTime=now() where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, task);
			pstmt.setInt(2, Integer.parseInt(taskStatus));
			pstmt.setInt(3, Integer.parseInt(taskId));
			pstmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(result)), response);
	}

	/**
	 * Add or update task remark
	 * 
	 * @param request
	 * @param response
	 */
	public void addOrUpdateTaskRemark(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		String remark = request.getParameter("remark");
		String isLatestRemark = request.getParameter("isLatestRemark");
		String taskId = request.getParameter("taskId");
		String remarkId = request.getParameter("remarkId");
		boolean result = false;
		
		try {
			conn = DBUtil.getConnection();
			/** if remark is Latest(new remark = latest remark), then shoud update the remark field in task table*/
			if ("1".equals(isLatestRemark) || StringUtil.isBlank(remarkId)) {
				sql = "update tbl_task set remark=?,finishTime=now() where id=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, remark);
				pstmt.setInt(2, Integer.parseInt(taskId));
				pstmt.executeUpdate();
			}
			
			/** If remarkId exist then update, else add*/
			if (!StringUtil.isBlank(remarkId)) {
				sql = "update tbl_task_remark set remark=? where id=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, remark);
				pstmt.setInt(2, Integer.parseInt(remarkId));
				pstmt.executeUpdate();
			} else {
				sql = "insert into `tbl_task_remark`(`taskId`,`remark`,`createTime`) VALUES(?,?,now());";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(taskId));
				pstmt.setString(2, remark);
				pstmt.executeUpdate();
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(result)), response);
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
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(new BaseMessage(result)), response);
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
		
		Task task = new Task();
		List<TaskRemark> taskRemarks = new ArrayList<TaskRemark>();
		TaskRemark taskRemark = null;
		String taskId = request.getParameter("taskId");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT id,remark,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime FROM `tbl_task_remark` WHERE taskId="+taskId+" ORDER BY id DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				taskRemark = new TaskRemark();
				taskRemark.setId(rs.getInt("id"));
				taskRemark.setRemark(rs.getString("remark"));
				taskRemark.setCreateTime(rs.getString("createTime"));
				taskRemarks.add(taskRemark);
			}
			sql = "SELECT id,task,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i') createTime FROM `tbl_task` WHERE id="+taskId;
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				task.setId(rs.getInt("id"));
				task.setTask(rs.getString("task"));
				task.setCreateTime(rs.getString("createTime"));
			}
			task.setTaskRemarks(taskRemarks);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(task), response);
	}
	
	/**
	 * Load task info by taskId
	 * 
	 * @param request
	 * @param response
	 */
	public void loadTaskById(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		Task task = null;
		String taskId = request.getParameter("taskId");
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			sql = "SELECT id,task,taskStatus FROM `tbl_task` WHERE id="+taskId;
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				task = new Task();
				task.setId(rs.getInt("id"));
				task.setTask(rs.getString("task"));
				task.setTaskStatus(rs.getInt("taskStatus"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, stmt, conn);
		}
		CommonUtil.sendJsonDataToClient(CommonUtil.fromObjctToJson(task), response);
	}
}
