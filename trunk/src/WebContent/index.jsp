<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
</head>
<body>
<a href="accessSessiion.jsp">访问一个session</a><br/>
<a href="createSessiion.jsp">创建一个session</a><br/>
<a href="destorySessiion.jsp">销毁一个session</a><br/>

<br/>随意访问JSPy页面<br/>
<a href="page1.jsp">访问page1.jsp</a><br/>
<a href="demo.jsp">访问经过过滤器拦截的demo.jsp页面</a><br/>
<a href="demo2.jsp">访问具有同样内容的demo2.jsp页面</a><br/>


<br/>随意访问JSPy页面<br/>
<a href="context.jsp">访问上下文 context.jsp</a><br/>
<a href=""createContextAttr.jsp"">设置一个context 属性页面</a><br/>
<a href=""destroyContextAttr.jsp"">取消一个context 属性页面</a><br/>
</body>
</html>