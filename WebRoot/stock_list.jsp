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
		<div class="ui-state-highlight ui-corner-all" style="height: 75px;">
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
							<button id="way_to_stock_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
								<span class="ui-button-text">Way to stock</span>
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
			where = "1=1";
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
	
	<table id="listTable" width="100%" class="gptable" border="0" cellpadding="0" cellspacing="0" style="margin-top: 5px;margin-bottom: 5px;font-size: 20px;">
		<tr>
			<th width="6%" align="left" style="height:50px;">gsmc<!-- 名称 --></th>
			
			<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=gpjg&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">gpjg</a><!-- 价格 --></th>
			<!-- 涨跌额 -->
			<th width="4.5%" align="left"><a target="_self" href="stock_list.jsp?orderby=zdbl&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">zdbl</a><!-- 涨跌比率 --></th>
			<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=huanShou&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">huanSh</a><!-- 换手 --></th>
			<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=zhenFu&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">zhenFu</a><!-- 振幅 --></th>
			<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=liangBi&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">liangBi</a><!-- 量比 --></th>
			
			<th width="7%" align="left">gpjzqz<!-- 分类 --></th>
			
			<th width="7%" align="left"><a target="_self" href="stock_list.jsp?orderby=icbhy&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">icbhy</a><!-- ICB行业 --></th>
			<th width="4%" align="left"><a target="_self" href="stock_list.jsp?orderby=ltag&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">ltag</a><!-- 流通A股 --></th>
			<th width="4%" align="left"><a target="_self" href="stock_list.jsp?orderby=mgsy&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">mgsy</a><!-- 每股收益 --></th>
			<th width="3%" align="left"><a target="_self" href="stock_list.jsp?orderby=jtsyl&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">jtsyl</a><!-- 静态市盈率 --></th>
			<th width="5%" align="left"><a target="_self" href="stock_list.jsp?orderby=sssj&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">sssj</a><!-- 上市时间 --></th>
			
			<th align="left"><a target="_self" href="stock_list.jsp?orderby=remarkTime&ascOrDesc=<%=ascOrDesc%>&where=<%=where%>">remarkTime</a><!-- 最后查看时间距离当前天数-备注 --></th>
			<th width="10%" align="left">updateType</th><!-- 最后修改变更类型 -->
			
			<th width="4%" align="left">id<!-- 序号 --></th>
			<th width="4%" align="left">gpdm<!-- 代码 --></th>
		</tr>
	</table>

  	<div id="listDiv" style="overflow: auto;width: 100%;height:500px;">
		<table width="100%" class="gptable" border="0" cellpadding="0" cellspacing="0">
		    <%
				Connection conn = null;
				Statement stmt = null;
				ResultSet rs = null;
				String sql = null;
				try {
					conn = DBUtil.getConnection();
					stmt = conn.createStatement();
					sql = "select id,gpdm,gsmc,gpjzqz,gpjg,zde,zdbl,huanShou,zhenFu,liangBi,icbhy,ltag,mgsy,jtsyl,sssj,star,remark,updateType,"+
						"DATEDIFF(NOW(),remarkTime) remarkTime from tbl_gp where ts!=1 and "+where+" order by gpjzqz desc," + orderby + " " + ascOrDesc;
					rs = stmt.executeQuery(sql);
					int i = 0;
					int j = 0;
					int gpjzqz = -100;
					int height = 50;
					String zdbl = null;
					while(rs.next()) {
						if (-100 != gpjzqz && gpjzqz != rs.getInt("gpjzqz")) {
							j = 0;
							height = (7 == gpjzqz) ? 200 : 50;
			%>
						<tr>
			  				<td colspan="16" height="<%=height%>px;">&nbsp;</td>
			  			</tr>
			<%	
						}
						gpjzqz = rs.getInt("gpjzqz");
						zdbl = null == rs.getString("zdbl") ? "" : rs.getString("zdbl");
			%>
						<tr class="gpTr" id="tr<%=rs.getInt("id")%>">
			  				<!-- 名称 -->
			  				<td width="6%">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("hexun", rs.getString("gpdm")) %>">
			  						<%=rs.getString("gsmc")%>
			  						<%=rs.getInt("star")>0 ? "<font color='green'>★</font>" : ""%>
			  					</a>
			  				</td>
			  				
			  				<!-- 价格 -->
			  				<td width="3%" class="hqxx" id="gpjg<%=rs.getString("gpdm")%>">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("sina", rs.getString("gpdm")) %>">
			  						<%=null == rs.getString("gpjg") ? "" : rs.getString("gpjg")%>
			  					</a>
			  				</td>
			  				
			  				<!-- 涨跌比率 -->
			  				<td width="4.5%" class="hqxx" id="zdbl<%=rs.getString("gpdm")%>">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("ths", rs.getString("gpdm")) %>">
			  					<%
			  						if (-1 != zdbl.indexOf("-")) {
			  							out.print("<font color='green'>↓"+(rs.getString("zdbl"))+"%</font>");
			  						} else if (-1 != zdbl.indexOf("0.00") && -1 == zdbl.indexOf("1")) {
			  							out.print(rs.getString("zdbl")+"%");
			  						} else if(!"".equals(zdbl)){
			  							out.print("<font color='red'>↑+"+(rs.getString("zdbl"))+"%</font>");
			  						}
			  					%>
			  					</a>
			  				</td>
			  				
			  				<!-- 换手 -->
			  				<td width="5%">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("qq", rs.getString("gpdm")) %>">
			  					<%=null == rs.getString("huanShou") ? "" : rs.getString("huanShou")%>
			  					</a>
			  				</td>
			  				<!-- 振幅 -->
			  				<td width="5%">
			  					<%=null == rs.getString("zhenFu") ? "" : rs.getString("zhenFu")%>
			  				</td>
			  				<!-- 量比 -->
			  				<td width="5%">
			  					<%=null == rs.getString("liangBi") ? "" : rs.getString("liangBi")%>
			  				</td>
		  				
			  				<!-- 股票价值权重及类型 -->
			  				<td width="7%" style="cursor: pointer;" onclick="openQzChangeDialog('<%=rs.getInt("id")%>')">
			  					<%=ConvertUtil.convertGpfl(rs.getInt("gpjzqz"))%>
			  				</td>
			  				
			  				<!-- ICB行业 -->
			  				<td width="7%">
			  					<%=null == rs.getString("icbhy") ? "" : rs.getString("icbhy")%>
			  				</td>
			  				<!-- 流通A股 -->
			  				<td width="4%">
			  					<%=null == rs.getString("ltag") ? "" : rs.getString("ltag")%>
			  				</td>
			  				<!-- 每股收益 -->
			  				<td width="4%">
			  					<%=null == rs.getString("mgsy") ? "" : rs.getString("mgsy")%>
			  				</td>
			  				<!-- 静态市盈率 -->
			  				<td width="3%">
			  					<%=null == rs.getString("jtsyl") ? "" : rs.getString("jtsyl")%>
			  				</td>
			  				<!-- 上市时间 -->
			  				<td width="5%">
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
			  					<span id="remark<%=rs.getString("gpdm")%>" onclick="openRemarkWriteDialog('<%=rs.getString("gpdm")%>')" title="<%=CommonUtil.delHTMLTag(rs.getString("remark"))%>">
			  					<%
			  						out.print(CommonUtil.subString(CommonUtil.delHTMLTag(rs.getString("remark")), 24));
			  					%>
			  					</span>
			  				</td>
			  				<!-- 最近更新内容描述 -->
			  				<td width="10%" style="cursor: pointer;" class="read_news_td" nodeId="<%=rs.getInt("id")%>">
			  					<%=null != rs.getObject("updateType") ? rs.getString("updateType"):""%>
			  				</td>
			  				
			  				<!-- 序号 -->
			  				<td width="4%">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("sina", rs.getString("gpdm")) %>">
			  						<%=(++i)+"-"+(++j)%>
			  					</a>
			  				</td>
			  				<!-- 代码 -->
			  				<td width="4%">
			  					<a target="_blank" href="<%=CommonUtil.getGpUrl("ths", rs.getString("gpdm")) %>">
			  						<%=rs.getString("gpdm")%>
			  					</a>
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
  	
  	<!-- 投资策略对话框   -->
  	<div id="wayToStockDialog" style="display: none;">
  		<table width="100%" class="gptable ui-widget-content" style="word-break: break-all;word-wrap: break-word;overflow: auto;">
  			<tr>
  				<td width="18%">投资策略</td>
  				<td>
  					1、长期投资、周期为3到5年<br/>
  					2、不做短线交易<br/>
  					3、重价值投资，辅之趋势<br/>
  					4、分批买入<br/>
  					5、只买熟悉的股票<br/>
  				</td>
  			</tr>
  			<tr>
  				<td>铁的纪律</td>
  				<td>
  					1、不轻易换股<br/>
  					2、不轻易买卖<br/>
  					4、不追高<br/>
  					5、不贪婪、不恐惧<br/>
  					6、大跌大涨时不要急买<br/>
  				</td>
  			</tr>
  			<tr>
  				<td>资金总额及分配</td>
  				<td></td>
  			</tr>
  			<tr>
  				<td>标的选择</td>
  				<td>
  					1、业绩中上、无亏损<br/>
  					2、公司稳定，很难倒闭，行业前途好<br/>
  					3、上市时间长，业绩、行业落后直接干掉；近五年上市的较好<br/>
  					4、避免容易暴雷的行业：医药、食品，一旦曝光，永劫不复，可以基金代之<br/>
  					5、避免严重依赖进口，依赖国外市场的公司（比如中兴公司,政治影响严重）<br/>
  					5、可能实施的注册制利好券商<br/>
  					<table style="margin-top: 20px;" width="100%" style="word-break: break-all;word-wrap: break-word;overflow: auto;" border="1" cellspacing="0" cellpadding="0">
  						<tr>
  							<td width="12%">行业</td>
  							<td width="35%">精选股票</td>
  							<td>备注</td>
  						</tr>
  						<tr>
  							<td>银行</td>
  							<td></td>
  							<td></td>
  						</tr>
  						<tr>
  							<td>证券</td>
  							<td>国泰君安，中信证券</td>
  							<td></td>
  						</tr>
  						<tr>
  							<td>保险</td>
  							<td></td>
  							<td></td>
  						</tr>
  						<tr>
  							<td>医药</td>
  							<td>葵花，敖东</td>
  							<td></td>
  						</tr>
  						<tr>
  							<td>消费</td>
  							<td>格力，美的，老板</td>
  							<td></td>
  						</tr>
  						<tr>
  							<td>不动产</td>
  							<td>张江高科，陆家嘴</td>
  							<td></td>
  						</tr>
  					</table>
  				</td>
  			</tr>
  			<tr>
  				<td>买入</td>
  				<td></td>
  			</tr>
  			<tr>
  				<td>卖出</td>
  				<td></td>
  			</tr>
  			<tr>
  				<td>风险控制</td>
  				<td>
  					1、首次ST择机卖出止损<br/>
  				</td>
  			</tr>
  		</table>
  	</div>
  	
  	<div id="processDialog" style="display: none;" class="ui-widget-content">
  		处理中，请稍后....
  	</div>
  </body>
</html>
