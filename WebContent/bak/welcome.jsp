<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="MainApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
	<link rel="stylesheet" href="css/welcome.css">
</head>
<body ng-controller="mainController as ctrl">
<div id="top" >
	<div class="nouse"></div>
	<div class="nouse"></div>
	<h1>Welcome <s:property value="%{#session.account}"/> !!</h1>
</div>
<div id="middle">
	<%-- <div id = "box1" class = "box" onclick="location.href='adminLink';" >
		<div class="nouse"></div>
		<div class="content">
			<span class="title">Account Management (帳戶管理)</span>
			<span class="bar"></span>
			<span class="detail">detail 1</span>
		</div>
	</div>
	<div id = "box2" class = "box" onclick="location.href='searchLink';">
		<div class="nouse"></div>
		<div class="content">
			<span class="title">Status Inquiry (狀態查詢)</span>
			<span class="bar"></span>
			<span class="detail">detail 2</span>
		</div>
	</div>
	<div id = "box3" class = "box">
		<div class="nouse"></div>
		<div class="content">
			<span class="title">Usage Inquiry (用量查詢)</span>
			<span class="bar"></span>
			<span class="detail">detail 3</span>
		</div>
	</div>
	<div id = "box4" class = "box">
		<div class="nouse"></div>
		<div class="content">
			<span class="title">功能四</span>
			<span class="bar"></span>
			<span class="detail">detail 4</span>
		</div>
	</div> --%>
	<div ng-repeat="item in ctrl.menuList" id = "box{{$index+1}}" class = "box" ng-click="ctrl.redirect(item.link)">
		<div class="nouse"></div>
		<div class="content">
			<span class="title">{{item.title}}</span>
			<span class="title">{{item.subTitle}}</span>
			<span class="bar"></span>
			<span class="detail">{{item.detail}}</span>
		</div>
	</div>
</div>

</body>

<!-- angularJS -->
<script src="js/angular.min.js"></script>
<script src="js/common.js"></script>
</html>