<%@page import="org.apache.commons.lang.time.DateFormatUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
: )) 当前时间 : <br /> <b><%=DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss SSS") %></b>
<%System.out.println("请求一次啊！"); %>
</body>
</html>