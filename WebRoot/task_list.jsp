<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
  <head>
    <title>spider</title>
    <!-- 导入框架css和js库 -->
	<jsp:include page="head-ui-1.10.4.jsp"></jsp:include>
    <script type="text/javascript" src="js/task.js"></script>
    <style type="text/css">
    	 table{
    	 	word-break: break-all;word-wrap: break-word;overflow: auto;
    	 }
    	 .taskTr:hover {background-color: #FFE45B;}
    	 .taskTable th{border-bottom:#333 1px dashed;}
    	 .taskTable td{border-bottom:#333 1px dashed;}
    </style>
  </head>
  
  <body>
  
  	<div class="ui-widget" style="font-size: 14px;">
		<div class="ui-state-highlight ui-corner-all" style="height: 60px;">
			<div class="menuDiv" style="margin-top: 1px;">
				<table border="0" width="100%">
					<tr>
						<td >
							<jsp:include page="menu.jsp"></jsp:include>
						</td>
						<td align="right">
							<button id="add_task_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Add Task</span>
							</button>
							<button id="task_time_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Task Time</span>
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	
	<%
		//排序字段
		String orderby = request.getParameter("orderby");
		String ascOrDesc = request.getParameter("ascOrDesc");
		if(null == orderby || "".equals(orderby)) {
			orderby = "a.createTime";
		}
		if (null == ascOrDesc || "".equals(ascOrDesc) || "asc".equals(ascOrDesc)) {
			ascOrDesc = "desc";
		} else {
			ascOrDesc = "asc";
		}
	%>

  	<div id="mainDiv" style="overflow: auto;height: 100;width: 100;">
	  	<table width="100%">
	  		<tr>
	  			<td width="1%"></td>
	  			<td>
	  					<table width="100%" class="taskTable" border="0" cellpadding="4" cellspacing="4">
					  		<thead>
					  			<tr>
					  				<th width="6%" align="center">A</th><!-- 序号 -->
					  				<th align="center">B</th><!-- 任务内容-备注 -->
					  				<th width="2%">&nbsp;</th>
					  				<th width="13%" align="center"><a target="_self" href="task_list.jsp?orderby=a.endTime&ascOrDesc=<%=ascOrDesc%>">C</a></th><!-- 任务要求完成时间 -->
					  				<th width="4%" align="center"><a target="_self" href="task_list.jsp?orderby=a.taskType&ascOrDesc=<%=ascOrDesc%>">D</a></th><!-- 类型 -->
					  				<th width="5%" align="center"><a target="_self" href="task_list.jsp?orderby=a.taskVolume&ascOrDesc=<%=ascOrDesc%>">E</a></th><!-- 任务大小 -->
					  				<th width="5%" align="center"><a target="_self" href="task_list.jsp?orderby=a.taskUrgency&ascOrDesc=<%=ascOrDesc%>">F</a></th><!-- 紧急程度 -->
					  				<th width="8%" align="center"><a target="_self" href="task_list.jsp?orderby=a.taskCome&ascOrDesc=<%=ascOrDesc%>">H</a></th><!-- 任务来源 -->
					  				<th width="9%" align="center">G</th><!-- 状态 -->
					  			</tr>
					  		</thead>
					  		<tbody>
					  			
					    <%
							Connection conn = null;
							Statement stmt = null;
							ResultSet rs = null;
							String sql = null;
							try {
								conn = DBUtil.getConnection();
								stmt = conn.createStatement();
								sql = "SELECT a.id,a.task,a.taskType,a.taskVolume,a.taskUrgency,a.taskStatus,b.`taskCome`,a.endTime,a.remark,"
										+ "DATEDIFF(NOW(),a.endTime) behindDay FROM tbl_task a LEFT OUTER JOIN tbl_task_come b ON a.`taskCome`=b.`id`" 
										+ " where deleteFlag=1 ORDER BY a.taskStatus ASC," + orderby + " " + ascOrDesc;
								rs = stmt.executeQuery(sql);
								int i = 0;
								int j = 0;
								int taskStatus = -2;
								while(rs.next()) {
									if (taskStatus != -2 && taskStatus != rs.getInt("taskStatus")) {
										j = 0;
						%>
									<tr>
						  				<td colspan="9" height="90px;">&nbsp;</td>
						  			</tr>
						<%	
									}
									taskStatus = rs.getInt("taskStatus");
						%>
									<tr class="taskTr" id="tr<%=rs.getInt("id")%>"">
						  				<!-- 序号 -->
						  				<td>
						  					<span style="cursor: pointer;" class="sequence" taskId="<%=rs.getInt("id")%>"><%=(++i)+"-"+(++j)%></span>
						  				</td>
						  				<!-- 任务内容 -->
						  				<td>
						  					<%=rs.getString("task")%>
						  					<%=null != rs.getObject("remark") ? "<font style='cursor: pointer;' color='red' onclick='openRemarkViewDialog(\""+(rs.getInt("id"))+"\")'>[备注]</font>":"" %>
						  				</td>
						  				<td>
						  					&nbsp;
						  				</td>
						  				<!-- 任务要求完成时间 -->
						  				<td>
						  					<%=CommonUtil.formatDate(rs.getTimestamp("endTime"), "yyyy-MM-dd")%>
						  					<font color="red"><%=rs.getInt("behindDay")> 0 ? "("+rs.getInt("behindDay")+")":""%></font>
						  				</td>
						  				<!-- 类型 -->
						  				<td>
						  					<%=ConvertUtil.convertTaskType(rs.getInt("taskType"))%>
						  				</td>
						  				<!-- 任务大小 -->
						  				<td>
						  					<%=ConvertUtil.convertTaskVolume(rs.getInt("taskVolume"))%>
						  				</td>
						  				<!-- 紧急程度 -->
						  				<td>
						  					<%=ConvertUtil.convertTaskUrgency(rs.getInt("taskUrgency"))%>
						  				</td>
						  				<!-- 任务来源 -->
						  				<td>
						  					<%=rs.getString("taskCome")%>
						  				</td>
						  				<!-- 状态 -->
						  				<td>
						  					<%
						  						int status = rs.getInt("taskStatus");
						  						String color = null;
						  						if (status == 3 || status == 4 || status == 5) {
						  							color = status == 3 ? "green":(status == 4 ? "black":"red");
						  					%>
						  						<font color="<%=color%>">&nbsp;&nbsp;&nbsp;&nbsp;<%=ConvertUtil.convertTaskStatus(rs.getInt("taskStatus"))%></font>
						  					<%		
						  						} else {
						  					%>
							  					<button id="add_task_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
													<span taskId="<%=rs.getInt("id")%>" class="taskStatus ui-button-text"><%=ConvertUtil.convertTaskStatus(rs.getInt("taskStatus"))%></span>
												</button>
						  					<%	
						  						}
						  					 %>
						  				</td>
						  			</tr>
						<%
								}
							} catch (Exception e) {
								e.printStackTrace(response.getWriter());
							} finally {
								DBUtil.close(rs, stmt, conn);
							}
					    %>
					    </tbody>
					  		<tfoot>
					  		</tfoot>
					  	</table>
	  			</td>
	  			<td width="1%"></td>
	  		</tr>
	  	</table>
  	</div>
  	
  	<!-- 任务新增或修改对话框  -->
  	<div id="addOrEditTaskDialog" style="display: none;">
  		<form action="#" id="addOrEditForm">
  			<input type="hidden" name="id"/>
  			<table width="100%" class="ui-widget-content">
	  			<tr>
	  				<td width="15%">任务内容</td>
	  				<td>
	  					<textarea name="task" rows="5" style="width: 510px;"></textarea>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>类型</td>
	  				<td>
	  					<select style="width: 510px;" name="taskType">
	  						<option value="1">工作</option>
	  						<option value="2">生活</option>
	  						<option value="3">学习</option>
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>任务大小</td>
	  				<td>
	  					<select style="width: 510px;" name="taskVolume">
	  						<option value="1">临时</option>
	  						<option value="2">小型</option>
	  						<option value="3">中型</option>
	  						<option value="4">大型</option>
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>紧急程度</td>
	  				<td>
	  					<select style="width: 510px;" name="taskUrgency">
	  						<option value="1">一般</option>
	  						<option value="2">紧急</option>
	  						<option value="3">特急</option>
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>状态</td>
	  				<td>
	  					<select style="width: 510px;" name="taskStatus">
	  						<option value="1">没开始</option>
	  						<option value="2">进行中</option>
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>任务来源</td>
	  				<td>
	  					<select style="width: 510px;" name="taskCome">
	  						
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>开始时间</td>
	  				<td>
	  					<input style="width: 510px;" name="startTime"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>结束时间</td>
	  				<td>
	  					<input style="width: 510px;" name="endTime"/>
	  				</td>
	  			</tr>
	  		</table>
  		</form>
  	</div>
  	
  	<!-- 状态修改对话框  -->
  	<div id="statusChangeDialog" style="display: none;">
 		<table width="100%" class="ui-widget-content">
  			<tr>
  				<td width="15%">状态</td>
  				<td>
  					<select style="width: 510px;" name="taskStatus">
  						<option value="2">进行中</option>
  						<option value="3">完成</option>
  						<option value="4">取消</option>
  						<option value="5">失败</option>
  					</select>
  				</td>
  			</tr>
  			<tr>
  				<td >备注</td>
  				<td>
  					<textarea name="remark" rows="9" style="width: 510px;"></textarea>
  				</td>
  			</tr>
  		</table>
  	</div>
  	
  	<div id="remarkViewDialog">
  		<table width="100%" class="ui-widget-content" id="taskViewTable" style="word-break: break-all;word-wrap: break-word;overflow: auto;margin-bottom: 25px;">
  			
  		</table>
  		<table width="100%" class="taskTable ui-widget-content" id="remarkViewTable" style="word-break: break-all;word-wrap: break-word;overflow: auto;">
  			
  		</table>
  	</div>
  	
  	<div id="processDialog" style="display: none;">
  		处理中，请稍后....
  	</div>
  	
  	<div id="taskTimeDialog">
  		<iframe  frameborder="0" height="550px" width="1000px" src="task_time.jsp"></iframe>
  	</div>
  </body>
</html>
