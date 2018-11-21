<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="MainApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>Insert title here</title>
<!-- boostrp4 css -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
	z
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/admin.css">
</head>
<body ng-controller="adminCtrl as ctrl">
	<p id="user" style="visibility: hidden;position: absolute;"><s:property value="%{#session.account}"/></p>
	<p id="permission" style="visibility: hidden;position: absolute;"><s:property value="%{#session.permission['USER_LEVEL']}"/></p>
	<p id="subsidiaryid" style="visibility: hidden;position: absolute;"><s:property value="%{#session.permission['SUBSIDIARYID']}"/></p>
	<div id="top">
		<div class="nouse"></div>
		<span class="title" ng-bind = "ctrl.menuList[0].title+ctrl.menuList[0].subTitle"></span>
		<span class="bar"></span>
		<span class="detail" ng-bind = "ctrl.menuList[0].detail"></span>
		<span ng-show="ctrl.errorMsg!=null && ctrl.errorMsg !=''" style="color:red;">{{ctrl.errorMsg}}</span>
	</div>
	<div id="functionBar">
		<input type="button" class="sbutton" value="送出" ng-click="ctrl.update()" ng-disabled="(ctrl.updateList.length+ctrl.newList.length)==0">
		<input type="button" class="sbutton" value="新增" ng-click="ctrl.addItem()" ng-hide="ctrl.level>=2" ng-disabled="!myForm.$valid">
		<div class="dropleft">
			<div class = "menubox" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				<div class = "menu"></div>
				<div class = "menu"></div>
				<div class = "menu"></div>
			</div>
			<div class="dropdown-menu">
				<a ng-repeat = "item in ctrl.menuList" class="dropdown-item" ng-href="{{item.link}}">{{item.title}}{{item.subTitle}}</a>
			    <!-- <a class="dropdown-item item1" href="adminLink">用戶管理</a>
			    <a class="dropdown-item item2" href="">基本查詢</a>
			    <a class="dropdown-item item3" href="welcomeLink">回首頁</a> -->
			    <div class="dropdown-divider"></div>
			    <a class="dropdown-item item4" href="logout">登出</a>
			</div>
		</div>
	</div>
	<div>
		<table style="width: 100%" data-ng-init="ctrl.query()">
			<thead style="">
				<tr>
					<th ng-repeat="head in ctrl.header" style="width: 16%;">{{head}}</th>
					<th ng-repeat="perm in ctrl.permission" style="width: 16%;">{{perm}}</th>
				</tr>
			</thead>
			<tbody>
				<tr ng-repeat="data in ctrl.tableDatas">
					<td ng-repeat="h in ctrl.header" >
						<input ng-if="h=='password'" class="inputBox" ng-model="data[h]" type="text" placeholder="{{h}}" ng-change="ctrl.dataChange(data['account'])">
						<p ng-if="h!='password'">{{data[h]}}</p>
					</td>
					<!-- <td ng-repeat="p in ctrl.permission">
						<div class="btn-group mr-2" role="group" aria-label="First group" >
							<button type="button" class="btn btn-sm {{data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=true">on</button>
							<button type="button" class="btn btn-sm {{!data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=false">off</button>
						</div>
					</td> -->
					<td ng-repeat="p in ctrl.permission">
						<div>
							<button type="button" class="onbtn btn btn-sm" ng-click="ctrl.permClick(data,p,false)" ng-show="data[p]">On</button>
							<button type="button" class="offbtn btn btn-sm" ng-click="ctrl.permClick(data,p,true)" ng-show="!data[p]">Off</button>
						</div>
					</td>
				</tr>
				<tr ng-hide="ctrl.level>=2" ng-form name="myForm" id="myForm">
					<td ng-repeat="h in ctrl.header" data-ng-init="ctrl.iniAddItem()">
						<input ng-if="h!='level'" class="inputBox" name="{{h}}" ng-model="ctrl.add[h]" type="text" placeholder="{{h}}" required ng-change="ctrl.errorMsg=''">
						<input ng-if="h=='level'" class="inputBox" name="{{h}}" ng-model="ctrl.add[h]" type=text placeholder="{{h}}" required ng-pattern="/^[0-9]{1}$/"  maxlength="1" ng-change="ctrl.errorMsg=''">
					</td>
					<!-- ng-pattern="/^[0-9]{1}$/" -->
					<!-- <td ng-repeat="p in ctrl.permission">
						<div class="btn-group mr-2" role="group" aria-label="First group" >
							<button type="button" class="btn btn-sm {{data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=true">on</button>
							<button type="button" class="btn btn-sm {{!data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=false">off</button>
						</div>
					</td> -->
					<td ng-repeat="p in ctrl.permission" >
						<div>
							<button type="button" class="onbtn btn btn-sm" ng-click="ctrl.permClick(ctrl.add,p,false)" ng-show="ctrl.add[p]" disabled="disabled">On</button>
							<button type="button" class="offbtn btn btn-sm" ng-click="ctrl.permClick(ctrl.add,p,true)" ng-show="!ctrl.add[p]" disabled="disabled">Off</button>
						</div>
					</td>
				</tr>
				<!-- Test Code -->
				<!-- <tr ng-repeat="data in ctrl.tableDatas">
					<td ng-repeat="h in ctrl.header">{{data[h]}}</td>
					<td ng-repeat="p in ctrl.permission">{{data[p]}}</td>
				</tr>
				<tr>
					<td ng-repeat="h in ctrl.header">{{ctrl.add[h]}}</td>
					<td ng-repeat="p in ctrl.permission">{{ctrl.add[p]}}</td>
				</tr> -->
			</tbody>
		</table>	
	</div>
</body>
<script src="http://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
<!-- angularJS -->
<script src="js/angular.min.js"></script>
<script src="js/jquery.blockUI.js"></script>
<!-- service -->
<%-- <script src="js/service.js"></script> --%>
<!-- page controller -->
<script src="js/common.js"></script>
<script src="js/admin.js"></script>
</html>