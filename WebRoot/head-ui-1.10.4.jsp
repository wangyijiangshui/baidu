<%@ page language="java" contentType="text/html; charset=UTF-8" import="com.duapp.vo.UserData" pageEncoding="UTF-8"%>
<%
	String theme = null;
	String userName = "";
	Object user = request.getSession().getAttribute("user");
	if(null != user) {
		theme = ((UserData)user).getUserTheme();
		userName = ((UserData)user).getUserName();
	} 
	if (null == theme || "".equals(theme)) {
		theme = "blitzer";
	}
%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/jquery-ui-1.10.4.custom/css/<%=theme.toString()%>/jquery-ui-1.10.4.custom.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.10.4.custom/js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common.js"></script>
<script type="text/javascript" >
	var basePath="${pageContext.request.contextPath}";
	var userName = "<%=userName%>";
</script>