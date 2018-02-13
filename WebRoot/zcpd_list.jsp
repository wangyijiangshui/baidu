<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
  <head>
    <title>资产盘点</title>
    <!-- 导入框架css和js库 -->
	<jsp:include page="head-ui-1.10.4.jsp"></jsp:include>
    <script type="text/javascript" src="js/zcpd.js"></script>
    <style type="text/css">
    	 table{
    	 	word-break: break-all;word-wrap: break-word;overflow: auto;
    	 }
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
							&nbsp;
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	
  	<div style="overflow: auto;width: 100%;height:500px;">
  		test
  	</div>
  	
  </body>
</html>
