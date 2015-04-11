<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>stock</title>
    <!-- 导入框架css和js库 -->
	<jsp:include page="head-ui-1.10.4.jsp"></jsp:include>
    <script type="text/javascript" src="js/stock.js"></script>
    <style type="text/css">
    	 .gptable th{border-bottom:#333 1px dashed;}
    	 .gptable td{border-bottom:#333 1px dashed;}
    	 .remarkTime{font-size: 20px;font-weight: bold;}
    	 .gpTr:hover {background-color: #FFE45B;}
    </style>
  </head>
  
  <body>
  
  	<div class="ui-widget" style="font-size: 14px;">
		<div class="ui-state-highlight ui-corner-all" style="height: 60px;">
			<div class="menuDiv" style="margin-top: 1px;">
				<table border="0" width="100%">
					<tr>
						<td >
							<ul id="menu" style="width: 80px;height: 45px; margin: 0 0 0 0">
							  <li>
							    <a href="#"><img id="menu" border="0" src="image/child.jpg" style="width: 48px;height: 44px;"/></a>
							    <ul>
							      <li><a href="index.jsp">Index Page</a></li>
							      <li><a href="stock_list.jsp">Stock Detail Infomation</a></li>
							      <li><a href="task_list.jsp">My Task</a></li>
							      <li><a href="contacts_list.jsp">My Contacts</a></li>
							    </ul>
							  </li>
							</ul>
						</td>
						<td align="right">
							<button id="refresh_page_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Refresh Page</span>
							</button>
							<button id="refresh_db_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Refresh Database</span>
							</button>
							<button id="refresh_base_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Refresh Base Info</span>
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
			orderby = "zdbl";
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
	  					<table width="100%" class="gptable" border="0" cellpadding="0" cellspacing="0">
					  		<thead>
					  			<tr>
					  				<th width="7%" align="left">A<!-- 序号 --></th>
					  				<th width="7%" align="left">B<!-- 代码 --></th>
					  				<th width="9%" align="left">C<!-- 名称 --></th>
					  				
					  				<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=gpjg&ascOrDesc=<%=ascOrDesc%>">D</a><!-- 价格 --></th>
					  				<th width="6%" align="left"><a target="_self" href="stock_list.jsp?orderby=zde&ascOrDesc=<%=ascOrDesc%>">E</a><!-- 涨跌额 --></th>
					  				<th width="9%" align="left"><a target="_self" href="stock_list.jsp?orderby=zdbl&ascOrDesc=<%=ascOrDesc%>">F</a><!-- 涨跌比率 --></th>
					  				<th width="10%" align="left">G<!-- 分类 --></th>
					  				
					  				<th width="15%" align="left"><a target="_self" href="stock_list.jsp?orderby=icbhy&ascOrDesc=<%=ascOrDesc%>">I</a><!-- ICB行业 --></th>
					  				<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=ltag&ascOrDesc=<%=ascOrDesc%>">H</a><!-- 流通A股 --></th>
					  				
					  				<th align="left"><a target="_self" href="stock_list.jsp?orderby=remarkTime&ascOrDesc=<%=ascOrDesc%>">J</a><!-- 最后查看时间距离当前天数-备注 --></th>
					  				<th width="4%" align="left">K</th>
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
								/*至此连接已完全建立，就可对当前数据库进行相应的操作了*/
								/* 3. 接下来就可以使用其它标准mysql函数操作进行数据库操作*/
								//创建一个数据库表   id 主键	gpdm 股票代码	gsmc 公司名称	gpjzqz 股票价值权重
								sql = "select id,gpdm,gsmc,gpjzqz,gpjg,zde,zdbl,icbhy,ltag,remark,updateType,DATEDIFF(NOW(),remarkTime) remarkTime from tbl_gp order by gpjzqz desc," + orderby + " " + ascOrDesc;
								rs = stmt.executeQuery(sql);
								int i = 0;
								int j = 0;
								int gpjzqz = -2;
								String zdbl = null;
								while(rs.next()) {
									if (gpjzqz != -2 && gpjzqz != rs.getInt("gpjzqz")) {
										j = 0;
						%>
									<tr>
						  				<td colspan="9" height="100px;">&nbsp;</td>
						  			</tr>
						<%	
									}
									gpjzqz = rs.getInt("gpjzqz");
									zdbl = null == rs.getString("zdbl") ? "" : rs.getString("zdbl");
						%>
									<tr class="gpTr" id="tr<%=rs.getInt("id")%>">
						  				<!-- 序号 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("sina", rs.getString("gpdm")) %>">
						  						<%=(++i)+"-"+(++j)%>
						  					</a>
						  				</td>
						  				<!-- 代码 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("hexun", rs.getString("gpdm").replace("sz","")) %>">
						  						<%=rs.getString("gpdm")%>
						  					</a>
						  				</td>
						  				<!-- 名称 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("ths", rs.getString("gpdm").replace("sz","")) %>">
						  						<%=rs.getString("gsmc")%>
						  					</a>
						  				</td>
						  				
						  				<!-- 价格 -->
						  				<td class="hqxx" id="gpjg<%=rs.getString("gpdm")%>">
						  					<%=null == rs.getString("gpjg") ? "" : rs.getString("gpjg")%>
						  				</td>
						  				<!-- 涨跌额 -->
						  				<td class="hqxx" id="zde<%=rs.getString("gpdm")%>">
						  					<%
						  						if (-1 != zdbl.indexOf("-")) {
						  							out.print("<font color=''>↓"+(rs.getString("zde"))+"</font>");
						  						} else if (-1 != zdbl.indexOf("0.00") && -1 == zdbl.indexOf("1")) {
						  							out.print(rs.getString("zde"));
						  						} else if(!"".equals(zdbl)) {
						  							out.print("<font color=''>↑+"+(rs.getString("zde"))+"</font>");
						  						}
						  					%>
						  				</td>
						  				<!-- 涨跌比率 -->
						  				<td class="hqxx" id="zdbl<%=rs.getString("gpdm")%>">
						  					<%
						  						if (-1 != zdbl.indexOf("-")) {
						  							out.print("<font color=''>↓"+(rs.getString("zdbl"))+"%</font>");
						  						} else if (-1 != zdbl.indexOf("0.00") && -1 == zdbl.indexOf("1")) {
						  							out.print(rs.getString("zdbl")+"%");
						  						} else if(!"".equals(zdbl)){
						  							out.print("<font color=''>↑+"+(rs.getString("zdbl"))+"%</font>");
						  						}
						  					%>
						  				</td>
						  				<!-- 股票价值权重及类型 -->
						  				<td style="cursor: pointer;" onclick="openQzChangeDialog('<%=rs.getInt("id")%>')">
						  					<%=ConvertUtil.convertGpfl(rs.getInt("gpjzqz"))%>
						  				</td>
						  				
						  				<!-- ICB行业 -->
						  				<td>
						  					<%=null == rs.getString("icbhy") ? "" : rs.getString("icbhy")%>
						  				</td>
						  				<!-- 流通A股 -->
						  				<td>
						  					<%=null == rs.getString("ltag") ? "" : rs.getString("ltag")%>
						  				</td>
						  				
						  				<!-- 备注 -->
						  				<td style="cursor: pointer;" ondblclick="openRemarkViewDialog('<%=rs.getString("gpdm")%>')">
						  					<!-- 最后一次股票查看时间距离当前的天数 -->
						  					<span class="remarkTime" id="remarkTime<%=rs.getString("gpdm")%>" onclick="submitRemarkTime('<%=rs.getString("gpdm")%>')">
						  					<%
						  						if (null==rs.getString("remarkTime")) {
						  							out.print("X");
						  						} else {
						  							out.print((rs.getString("remarkTime")));
						  						}
						  					%>
						  					</span>
						  					<!-- 股票备注 -->
						  					<span id="remark<%=rs.getString("gpdm")%>" onclick="openRemarkWriteDialog('<%=rs.getString("gpdm")%>')">
						  					<%
						  						out.print(CommonUtil.subString(rs.getString("remark"), 12));
						  					%>
						  					</span>
						  				</td>
						  				<!-- 最近更新内容描述 -->
						  				<td style="text-align: right;cursor: pointer;" class="read_news_td" nodeId="<%=rs.getInt("id")%>">
						  					<%=null != rs.getObject("updateType") ? rs.getString("updateType"):""%>
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
  	
  	<!-- 权重修改对话框  -->
  	<div id="weightChangeDialog" style="display: none;">
  		<table width="100%" class="ui-widget-content">
  			<tr>
  				<td width="15%">权重-类型</td>
  				<td>
  					<select style="width: 510px;" id="gpjzqz">
  						<option value="0">0—垃圾中的垃圾</option>
  						<option value="1">1—垃圾</option>
  						<option value="2">2—业绩差</option>
  						<option value="3">3—业绩好</option>
  						
  						<option value="4">4—业绩差待观察</option>
  						<option value="5">5—业绩好待观察</option>
  						
  						<option value="6">6—业绩差趋势好</option>
  						<option value="7">7—业绩差趋势极好</option>
  						<option value="8">8—业绩好趋势好</option>
  						<option value="9">9—业绩好趋势极好</option>
  						
  						<option value="10">10—买入</option>
  					</select>
  				</td>
  			</tr>
  			<tr>
  				<td>备注</td>
  				<td>
  					<textarea id="remark1" rows="10" style="width: 510px;"></textarea>
  				</td>
  			</tr>
  		</table>
  	</div>
  	
  	<!-- 备注填写对话框   -->
  	<div id="remarkWriteDialog">
  		<table width="100%" class="ui-widget-content">
  			<tr>
  				<td width="10%">备注</td>
  				<td>
  					<textarea id="remark" rows="10" style="width: 510px;"></textarea>
  				</td>
  			</tr>
  		</table>
  	</div>
  	
  	<!-- 历史备注详细查看对话框   -->
  	<div id="remarkViewDialog">
  		<table width="100%" class="gptable ui-widget-content" id="remarkViewTable" style="word-break: break-all;word-wrap: break-word;overflow: auto;">
  			
  		</table>
  	</div>
  	
  	<div id="processDialog" style="display: none;" class="ui-widget-content">
  		处理中，请稍后....
  	</div>
  </body>
</html>
