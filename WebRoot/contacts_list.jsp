<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>spider</title>
    <!-- 导入框架css和js库 -->
	<jsp:include page="head-ui-1.10.4.jsp"></jsp:include>
	<script type="text/javascript" src="js/jquery-easyui-1.3.6/jquery.form.js"></script>
    <script type="text/javascript" src="js/contacts.js"></script>
    <style type="text/css">
    	 table{
    	 	word-break: break-all;word-wrap: break-word;overflow: auto;
    	 }
    	 .contactsTr:hover {background-color: #FFE45B;}
    	 .contactsTable th{border-bottom:#333 1px dashed;}
    	 .contactsTable td{border-bottom:#333 1px dashed;}
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
							<button id="add_contacts_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
								<span class="ui-button-text">Add Contactor</span>
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	
	<%
		String orderby = request.getParameter("orderby");
		
		//图片是大还是小
		String bigOrSmall = request.getParameter("bigOrSmall");
		int width = 0;
		int height = 0;
		if ((null == bigOrSmall || "".equals(bigOrSmall) || "small".equals(bigOrSmall)) && null == orderby) {
			bigOrSmall = "big";
		} else if(null == orderby) {
			bigOrSmall = "small";
		}
		if ("big".equals(bigOrSmall)) {
			width = 105;
			height = 120;
		} else {
			width = 30;
			height = 40;
		}
		
		//排序字段
		String ascOrDesc = request.getParameter("ascOrDesc");
		if(null == orderby || "".equals(orderby)) {
			orderby = "a.catType desc, a.weight desc,a.createTime";
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
	  					<table width="100%" class="contactsTable" border="0" cellpadding="4" cellspacing="4">
					  		<thead>
					  			<tr>
					  				<!-- 序号 -->
					  				<th width="7%" align="center">A</th>
					  				<!-- 联系人照片 -->
					  				<th width="110px" align="center">
					  					<a target="_self" href="contacts_list.jsp?bigOrSmall=<%=bigOrSmall%>">B</a>
					  				</th>
					  				<!-- 姓名 -->
					  				<th width="7%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.name&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">C</a>
					  				</th>
					  				<!-- 出生地籍贯 -->
					  				<th width="8%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.bornProvince&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">D</a>
					  				</th>
					  				<!-- 工作职称 -->
					  				<th width="10%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.workTitle&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">E</a>
					  				</th>
					  				<!-- 详细工作地址 -->
					  				<th align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.workAddress&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">F</a>
					  				</th>
					  				<!-- 称呼 -->
					  				<th width="5%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.call&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">G</a>
					  					</th>
					  				<!-- 分类 -->
					  				<th width="8%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.catType&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">I</a>
					  				</th>
					  				<!-- 关系权重 -->
					  				<th width="3%" align="center">
					  					<a target="_self" href="contacts_list.jsp?orderby=a.weight&ascOrDesc=<%=ascOrDesc%>&bigOrSmall=<%=bigOrSmall%>">H</a>
					  				</th>
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
								sql = "SELECT a.id,a.logo,a.name,a.sex,a.bornProvince,a.workTitle,a.workAddress,a.call,a.weight,b.typeName,a.remark "
										+ "FROM tbl_contacts a LEFT OUTER JOIN tbl_contacts_type b ON a.catType=b.id" 
										+ " where a.deleteFlag=1 ORDER BY " + orderby + " " + ascOrDesc;
								rs = stmt.executeQuery(sql);
								int i = 0;
								int j = 0;
								String typeName = null;
								while(rs.next()) {
									if (null != typeName  && !typeName.equals(rs.getString("typeName"))) {
										j = 0;
						%>
									<tr>
						  				<td colspan="9" height="90px;">&nbsp;</td>
						  			</tr>
						<%	
									}
									typeName = rs.getString("typeName");
						%>
									<tr class="contactsTr" id="tr<%=rs.getInt("id")%>" height="50px;">
						  				<!-- 序号 -->
						  				<td>
						  					<%
						  						if(null != rs.getString("sex") && "女".equals(rs.getString("sex"))) {
						  							out.print("<font color='red'>♀</font>");
						  						} else {
						  							out.print("<font color='green'>♂</font>");
						  						}
						  					 %>
						  					<span style="cursor: pointer;" class="sequence" contactsId="<%=rs.getInt("id")%>"><%=(++i)+"-"+(++j)%></span>
						  				</td>
						  				<!-- 联系人照片 -->
						  				<td>
						  					<%
						  						if (null != rs.getString("logo") && !"".equals(rs.getString("logo"))) {
						  							out.print("<img style='cursor: pointer;' src='download.do?fileName="+rs.getString("id")+"/"+rs.getString("logo")+"' width='"+width+"px;' height='"+height+"px;' border='0'/>");
						  						} else {
						  							out.print("<img style='cursor: pointer;' src='image/head_icon.png' width='"+width+"px;' height='"+height+"px;' border='0'/>");
						  						}
						  					%>
						  				</td>
						  				<!-- 姓名 -->
						  				<td style="cursor: pointer;" onclick="openEditContactsDialog('<%=rs.getInt("id")%>')">
						  					<%=rs.getString("name")%>
						  				</td>
						  				<!-- 出生地籍贯 -->
						  				<td>
						  					<%=rs.getString("bornProvince")%>
						  				</td>
						  				<!-- 工作职称 -->
						  				<td>
						  					<%=rs.getString("workTitle")%>
						  				</td>
						  				<!-- 详细工作地址 -->
						  				<td style="cursor: pointer;" ondblclick="openDetailViewDialog('<%=rs.getInt("id")%>')">
						  					<%=rs.getString("workAddress")%>
						  				</td>
						  				<!-- 称呼 -->
						  				<td>
						  					<%=rs.getString("call")%>
						  				</td>
						  				<!-- 分类 -->
						  				<td>
						  					<%=rs.getString("typeName")%>
						  				</td>
						  				<!-- 关系权重 -->
						  				<td>
						  					<div onclick="addOrMinusWeight('<%=rs.getInt("id")%>',1)" style="margin-bottom: 10px;font-weight: bold;color: red;cursor: pointer;">＋</div>
						  					<div id="weightDiv<%=rs.getInt("id")%>"><%=rs.getString("weight")%></div>
						  					<div onclick="addOrMinusWeight('<%=rs.getInt("id")%>',-1)" style="margin-top: 10px;font-weight: bold;color: green;cursor: pointer;">－</div>
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
  	<div id="addOrEditContactsDialog" style="display: none;">
  		<form action="#" id="addOrEditForm">
  			<input type="hidden" name="id"/>
  			<table width="100%" class="ui-widget-content" border="0" cellpadding="0" cellspacing="0">
	  			<tr>
	  				<td width="20%" rowspan="6" style="border:#333 1px dashed;">
	  					<img id="uploadLogo" style="cursor: pointer;" src="image/head_icon.png" width="200px;" height="230px;" border='0'/>
	  					<input type="hidden" name="logo" title="logo"/>
	  				</td>
	  				<td width="12%" align="left">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名&nbsp;&nbsp;</td>
	  				<td>
	  					<input title="姓名" style="width: 505px;" name="name"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别&nbsp;&nbsp;</td>
	  				<td>
	  					<select style="width: 505px;" name="sex" title="性别">
	  						<option selected="selected" value="男">男</option>
	  						<option value="女">女</option>
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">手&nbsp;&nbsp;机&nbsp;&nbsp;号&nbsp;&nbsp;码&nbsp;&nbsp;</td>
	  				<td>
	  					<input style="width: 505px;" name="telephone" title="手机号码"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">QQ&nbsp;&nbsp;&nbsp;&nbsp;号&nbsp;&nbsp;&nbsp;&nbsp;码&nbsp;&nbsp;</td>
	  				<td>
	  					<input style="width: 505px;" name="qq" title="QQ号码"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱&nbsp;&nbsp;</td>
	  				<td>
	  					<input style="width: 505px;" name="email" title="邮箱"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">出&nbsp;生&nbsp;地籍&nbsp;贯&nbsp;&nbsp;</td>
	  				<td>
	  					<input style="width: 505px;" name="bornProvince" title="出生地籍贯"/>
	  				</td>
	  			</tr>
	  			
	  			
	  			<tr>
	  				<td align="left">工&nbsp;&nbsp;作&nbsp;&nbsp;职&nbsp;&nbsp;称&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="workTitle" title="工作职称"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">毕&nbsp;&nbsp;业&nbsp;&nbsp;院&nbsp;&nbsp;校&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="school" title="毕业院校"/>
	  				</td>
	  			</tr>
	  			
	  			<tr>
	  				<td align="left">家庭详细地址&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="homeAddress" title="家庭详细地址"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">详细工作地址&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="workAddress" title="详细工作地址"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">相&nbsp;&nbsp;识&nbsp;&nbsp;地&nbsp;&nbsp;点&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="knowAddress" title="相识地点"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">相&nbsp;&nbsp;识&nbsp;&nbsp;时&nbsp;&nbsp;间&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="knowTime" title="相识时间"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">生&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="birthday" title="生日"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;呼&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="call" title="称呼"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">关&nbsp;&nbsp;系&nbsp;&nbsp;权&nbsp;&nbsp;重&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<input style="width: 650px;" name="weight" value="1" title="关系权重"/>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;类&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<select style="width: 650px;" name="catType" title="分类">
	  						
	  					</select>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td align="left">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注&nbsp;&nbsp;</td>
	  				<td colspan="2">
	  					<textarea style="width: 650px;height:150px;" name="remark" title="备注"></textarea>
	  				</td>
	  			</tr>
	  			<tr>
	  				<td>&nbsp;</td>
	  				<td colspan="2" id="changes" ondblclick="checkChange()">
	  					&nbsp;
	  				</td>
	  			</tr>
	  		</table>
  		</form>
  		<form id="uploadLogoForm" enctype="multipart/form-data" method="post" style="width: 0;height:0;display: inline;">
	        <input type="file" id="fileName" name="fileName" style="width: 0;height: 0">
	     </form>
  	</div>
     
     <div id="detailViewDialog" style="display:none">
  		<table width="100%" class="ui-widget-content" style="word-break: break-all;word-wrap: break-word;overflow: auto;margin-bottom: 10px;">
  			<tr height="30px;">
  				<td width="20%" rowspan="7" style="border:#333 1px dashed;">
  					<img name="logo" style="cursor: pointer;" src="image/head_icon.png" width="200px;" height="230px;" border='0'/>
  				</td>
  				<td width="15%">&nbsp;</td>
  				<td>&nbsp;</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名：&nbsp;&nbsp;</td>
  				<td name="name" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别：&nbsp;&nbsp;</td>
  				<td name="sex" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">手&nbsp;&nbsp;机&nbsp;&nbsp;号&nbsp;&nbsp;码：&nbsp;&nbsp;</td>
  				<td name="telephone" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">QQ&nbsp;&nbsp;&nbsp;&nbsp;号&nbsp;&nbsp;&nbsp;&nbsp;码：&nbsp;&nbsp;</td>
  				<td name="qq" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱：&nbsp;&nbsp;</td>
  				<td name="email" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">出&nbsp;生&nbsp;地籍&nbsp;贯：&nbsp;&nbsp;</td>
  				<td name="bornProvince" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			
  			
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">工&nbsp;&nbsp;作&nbsp;&nbsp;职&nbsp;&nbsp;称：&nbsp;&nbsp;</td>
  				<td colspan="2" name="workTitle" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">毕&nbsp;&nbsp;业&nbsp;&nbsp;院&nbsp;&nbsp;校：&nbsp;&nbsp;</td>
  				<td colspan="2" name="school" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">家庭详细地址：&nbsp;&nbsp;</td>
  				<td colspan="2" name="homeAddress" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">详细工作地址：&nbsp;&nbsp;</td>
  				<td colspan="2" name="workAddress" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">相&nbsp;&nbsp;识&nbsp;&nbsp;地&nbsp;&nbsp;点：&nbsp;&nbsp;</td>
  				<td colspan="2" name="knowAddress" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">相&nbsp;&nbsp;识&nbsp;&nbsp;时&nbsp;&nbsp;间：&nbsp;&nbsp;</td>
  				<td colspan="2" name="knowTime" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">生&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日：&nbsp;&nbsp;</td>
  				<td colspan="2" name="birthday" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;呼：&nbsp;&nbsp;</td>
  				<td colspan="2" name="call" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">关&nbsp;&nbsp;系&nbsp;&nbsp;权&nbsp;&nbsp;重：&nbsp;&nbsp;</td>
  				<td colspan="2" name="weight" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">分&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;类：&nbsp;&nbsp;</td>
  				<td colspan="2" name="typeName" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td align="left" style="font-weight: bold;">
  					<span style="color: blue;cursor: pointer;" onclick="loadEditHistory();">备</span>
  					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  					<span style="color: blue;cursor: pointer;" onclick="loadPhotos();">注</span>：&nbsp;&nbsp;
  				</td>
  				<td colspan="2" name="remark" style="border-bottom:#333 1px dashed;">
  					
  				</td>
  			</tr>
  			<tr height="30px;">
  				<td colspan="3"  style="border-bottom:#333 1px dashed;text-align: right;color: red;">
  					<span name="createTime"></span>
  					<img src="image/add.png" border="0" style="cursor: pointer;margin-left: 5px;" onclick="javascript:$('#photoUploadTable').toggle('slow');"/>
  				</td>
  			</tr>
  		</table>
  		
  		<!-- 基本信息修改历史 -->
  		<table width="100%" class="ui-widget-content" id="remarkViewTable"  style="word-break: break-all;word-wrap: break-word;overflow: auto;margin-top:10px;margin-bottom: 10px;">
  		</table>
  		
  		<!-- 照片上传 -->
  		<table width="100%" class="ui-widget-content" id="photoUploadTable"  style="display: none;word-break: break-all;word-wrap: break-word;overflow: auto;margin-top:10px;margin-bottom: 10px;">
			<tr>
				<td>
					<div name="photoDiv" style="width: 500px;height: 300px;border:#333 1px dashed;cursor: pointer;">
						
					</div>
					<div style="margin-top: 5px;">
						<textarea style="width: 850px;height:70px;" name="photoRemark"></textarea>
						<input type="hidden" name="photo"/>
						<form id="uploadPhotoForm" enctype="multipart/form-data" method="post" style="width: 0;height:0;display: inline;">
					        <input type="file" id="photoName" name="photoName" style="width: 0;height: 0">
					    </form>
					</div>
					<div style="margin-top: 5px;text-align: right;">
						<button id="photo_publish_button" style="margin-left: 50px;" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
							<span class="ui-button-text">Publish</span>
						</button>
					</div>
				</td>
			</tr>
  		</table>
  		
  		<!-- 照片展示 -->
  		<table width="100%" class="ui-widget-content" id="photoViewTable"  style="display: none;word-break: break-all;word-wrap: break-word;overflow: auto;margin-top:30px;margin-bottom: 10px;">
  			<!-- 
  			<tr>
  				<td>
  					<div style="width: 100%;height: 400px;border:#333 1px dashed;cursor: pointer;text-align: center;">
		  				
		  			</div>
		  			<div style="border-bottom:#333 1px dashed;margin-top: 15px;">
  						司法鉴定房间卡萨
  					</div>
  					<div style="text-align: right;margin-top: 15px;margin-bottom: 50px;">
  						<span style="border-bottom:#333 1px dashed;">司法鉴定房间卡萨</span>
  					</div>
  				</td>
  			</tr>
  			<tr>
  				<td style="margin-bottom: 20px;">
  					<div style="width: 100%;height: 400px;border:#333 1px dashed;cursor: pointer;text-align: center;">
		  				
		  			</div>
		  			<div style="border-bottom:#333 1px dashed;margin-top: 15px;">
  						司法鉴定房间卡萨
  					</div>
  					<div style="text-align: right;margin-top: 15px;margin-bottom: 50px;">
  						<span style="border-bottom:#333 1px dashed;">司法鉴定房间卡萨</span>
  					</div>
  				</td>
  			</tr>
  			 -->
  		</table>
  	</div>
  	
  	<div id="processDialog" style="display: none;">
  		处理中，请稍后....
  	</div>
  </body>
</html>
