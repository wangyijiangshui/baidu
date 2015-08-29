<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
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
    	 a{color:black;}
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
							<button id="refresh_page_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
								<span class="ui-button-text">Refresh Page</span>
							</button>
							<button id="refresh_db_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
								<span class="ui-button-text">Refresh Database</span>
							</button>
							<button id="refresh_base_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
								<span class="ui-button-text">Refresh Base Info</span>
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	
	<%
		//查询条件
		String where = request.getParameter("where");
		if(null == where || "".equals(where)) {
			where = "where";
		} else {
			where = "where " + where + " and ";
		}
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

  	<div style="overflow: auto;width: 100%">
	  	<table width="100%">
	  		<tr>
	  			<td width="1%"></td>
	  			<td>
	  					<table width="2000" class="gptable" border="0" cellpadding="0" cellspacing="0">
					  		<thead>
					  			<tr>
					  				<th width="6%" align="left">gsmc<!-- 名称 --></th>
					  				
					  				<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=gpjg&ascOrDesc=<%=ascOrDesc%>">gpjg</a><!-- 价格 --></th>
					  				<!--  
					  				<th width="3.5%" align="left"><a target="_self" href="stock_list.jsp?orderby=zde&ascOrDesc=<%=ascOrDesc%>">zde</a></th>
					  				-->
					  				<!-- 涨跌额 -->
					  				<th width="4.5%" align="left"><a target="_self" href="stock_list.jsp?orderby=zdbl&ascOrDesc=<%=ascOrDesc%>">zdbl</a><!-- 涨跌比率 --></th>
					  				<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=huanShou&ascOrDesc=<%=ascOrDesc%>">huanSh</a><!-- 换手 --></th>
					  				<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=zhenFu&ascOrDesc=<%=ascOrDesc%>">zhenFu</a><!-- 振幅 --></th>
					  				<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=liangBi&ascOrDesc=<%=ascOrDesc%>">liangBi</a><!-- 量比 --></th>
					  				
					  				<th width="7%" align="left">gpjzqz<!-- 分类 --></th>
					  				
					  				<th width="6%" align="left"><a target="_self" href="stock_list.jsp?orderby=icbhy&ascOrDesc=<%=ascOrDesc%>">icbhy</a><!-- ICB行业 --></th>
					  				<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=ltag&ascOrDesc=<%=ascOrDesc%>">ltag</a><!-- 流通A股 --></th>
					  				<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=jtsyl&ascOrDesc=<%=ascOrDesc%>">jtsyl</a><!-- 静态市盈率 --></th>
					  				<th width="7%" align="left"><a target="_self" href="stock_list.jsp?orderby=sssj&ascOrDesc=<%=ascOrDesc%>">sssj</a><!-- 上市时间 --></th>
					  				
					  				<th width="14%" align="left"><a target="_self" href="stock_list.jsp?orderby=remarkTime&ascOrDesc=<%=ascOrDesc%>">remarkTime</a><!-- 最后查看时间距离当前天数-备注 --></th>
					  				<th width="10%" align="left">updateType</th><!-- 最后修改变更类型 -->
					  				
					  				<th width="4%" align="left">id<!-- 序号 --></th>
					  				<th width="3%" align="left">gpdm<!-- 代码 --></th>
					  				
					  				<th align="left">&nbsp;</th>
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
								sql = "select id,gpdm,gsmc,gpjzqz,gpjg,zde,zdbl,huanShou,zhenFu,liangBi,icbhy,ltag,jtsyl,sssj,star,remark,updateType,"+
									"DATEDIFF(NOW(),remarkTime) remarkTime from tbl_gp "+where+" (gpjzqz NOT IN (0,1,2,3) or star=1) order by gpjzqz desc," + orderby + " " + ascOrDesc;
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
						  				<td colspan="16" height="50px;">&nbsp;</td>
						  			</tr>
						<%	
									}
									gpjzqz = rs.getInt("gpjzqz");
									zdbl = null == rs.getString("zdbl") ? "" : rs.getString("zdbl");
						%>
									<tr class="gpTr" id="tr<%=rs.getInt("id")%>">
						  				<!-- 名称 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("hexun", rs.getString("gpdm")) %>">
						  						<%=rs.getString("gsmc")%>
						  						<%=rs.getInt("star")>0 ? "<font color='green'>★</font>" : ""%>
						  					</a>
						  				</td>
						  				
						  				<!-- 价格 -->
						  				<td class="hqxx" id="gpjg<%=rs.getString("gpdm")%>">
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("sina", rs.getString("gpdm")) %>">
						  						<%=null == rs.getString("gpjg") ? "" : rs.getString("gpjg")%>
						  					</a>
						  				</td>
						  				<!-- 涨跌额 
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
						  				</td>-->
						  				<!-- 涨跌比率 -->
						  				<td class="hqxx" id="zdbl<%=rs.getString("gpdm")%>">
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("ths", rs.getString("gpdm")) %>">
						  					<%
						  						if (-1 != zdbl.indexOf("-")) {
						  							out.print("<font color=''>↓"+(rs.getString("zdbl"))+"%</font>");
						  						} else if (-1 != zdbl.indexOf("0.00") && -1 == zdbl.indexOf("1")) {
						  							out.print(rs.getString("zdbl")+"%");
						  						} else if(!"".equals(zdbl)){
						  							out.print("<font color='red'>↑+"+(rs.getString("zdbl"))+"%</font>");
						  						}
						  					%>
						  					</a>
						  				</td>
						  				
						  				<!-- 换手 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("qq", rs.getString("gpdm")) %>">
						  					<%=null == rs.getString("huanShou") ? "" : rs.getString("huanShou")%>
						  					</a>
						  				</td>
						  				<!-- 振幅 -->
						  				<td>
						  					<%=null == rs.getString("zhenFu") ? "" : rs.getString("zhenFu")%>
						  				</td>
						  				<!-- 量比 -->
						  				<td>
						  					<%=null == rs.getString("liangBi") ? "" : rs.getString("liangBi")%>
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
						  				<!-- 静态市盈率 -->
						  				<td>
						  					<%=null == rs.getString("jtsyl") ? "" : rs.getString("jtsyl")%>
						  				</td>
						  				<!-- 上市时间 -->
						  				<td>
						  					<%=null == rs.getString("sssj") ? "" : rs.getString("sssj")%>
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
						  					<span id="remark<%=rs.getString("gpdm")%>" onclick="openRemarkWriteDialog('<%=rs.getString("gpdm")%>')" title="<%=rs.getString("remark")%>">
						  					<%
						  						out.print(CommonUtil.subString(rs.getString("remark"), 14));
						  					%>
						  					</span>
						  				</td>
						  				<!-- 最近更新内容描述 -->
						  				<td style="cursor: pointer;" class="read_news_td" nodeId="<%=rs.getInt("id")%>">
						  					<%=null != rs.getObject("updateType") ? rs.getString("updateType"):""%>
						  				</td>
						  				
						  				<!-- 序号 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("sina", rs.getString("gpdm")) %>">
						  						<%=(++i)+"-"+(++j)%>
						  					</a>
						  				</td>
						  				<!-- 代码 -->
						  				<td>
						  					<a target="_blank" href="<%=CommonUtil.getGpUrl("ths", rs.getString("gpdm")) %>">
						  						<%=rs.getString("gpdm")%>
						  					</a>
						  				</td>
						  				
						  				<td>&nbsp;</td>
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
  						
  						<option value="10">10—small</option>
  						<option value="11">11—middle</option>
  						<option value="12">12—bigger</option>
  						
  						<option value="13">13—买入</option>
  						
  						<option value="14">14—卖出</option>
  					</select>
  				</td>
  			</tr>
  			<tr>
  				<td>备注</td>
  				<td>
  					<textarea id="remark1" rows="10" style="width: 510px;"></textarea>
  				</td>
  			</tr>
  			<tr>
  				<td>星级</td>
  				<td>
  					<input type="text" id="star" style="width: 510px;" value="0"/>
  				</td>
  			</tr>
  		</table>
  	</div>
  	
  	<!-- 备注填写对话框   -->
  	<div id="remarkWriteDialog" style="display: none;">
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
  	<div id="remarkViewDialog" style="display: none;">
  		<table width="100%" class="gptable ui-widget-content" id="remarkViewTable" style="word-break: break-all;word-wrap: break-word;overflow: auto;">
  			
  		</table>
  	</div>
  	
  	<div id="processDialog" style="display: none;" class="ui-widget-content">
  		处理中，请稍后....
  	</div>
  </body>
</html>
