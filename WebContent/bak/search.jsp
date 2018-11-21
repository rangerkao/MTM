<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="MainApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>



<!-- boostrp4 css -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/search.css">

</head>
<body ng-controller="searchCtrl as ctrl">
	<div id="top">
		<div class="nouse"></div>
		<span class="title" ng-bind = "ctrl.menuList[1].title+ctrl.menuList[1].subTitle"></span>
		<span class="bar"></span>
		<span class="detail" ng-bind = "ctrl.menuList[1].detail"></span>
		<!-- {{"nowPage:"+ctrl.nowPage}}
		{{"dataPage:"+ctrl.dataPage}}
		{{"total:"+ctrl.total}}
		{{"totalData:"+ctrl.totalData}}
		{{"onePage:"+ctrl.onePage}}
		{{"selectedItem:"+ctrl.selectedItem}}
		{{"selectedIMSI:"+ctrl.selectedIMSI}}
		{{"selectedStatus:"+ctrl.selectedStatus}} -->
	</div>
	<div id="functionBar">
		
		<div id="conditionBox">
			<!-- <div class="condition">
				<input type="radio" name="condition" id="ICCID">
				<label for="ICCID">ICCID</label>
			</div>
			<div class="condition">
				<input type="radio" name="condition" id="Status" >
				<label for="Status">Status</label>
			</div>
			<div class="condition">
				<input type="radio" name="condition" id="Volume">
				<label for="Volume">Volume</label>
			</div> -->
			<select class="condition" ng-model="ctrl.conditionSelected">
				<option value="">--</option>
				<option ng-repeat="c in ctrl.condition " value="{{c}}">{{c}}</option>
			</select>
			
			<input type="text" id="value" ng-model="ctrl.v" ng-show="ctrl.conditionSelected == 'ICCID' || ctrl.conditionSelected == 'IMSI'">
			
			<select class="condition" ng-model="ctrl.statusSelected" ng-show="ctrl.conditionSelected == 'Status' ">
				<option value="">--</option>
				<option ng-repeat="s in ctrl.status " value="{{s.value}}">{{s.label}}</option>
			</select>
			<input type="button" class="sbutton" value="送出" ng-click="ctrl.query()">
			<div style="display: inline-block;" ng-show="ctrl.selectedItem.length>0">
				<select>
					<option value="">--</option>
					<option ng-repeat="i in ctrl.action" value="{{i.value}}">{{i.label}}</option>
				</select>
				<input type="button" class="sbutton" value="執行" ng-click="">
			</div>
		</div>
		
		<div id="pageBox">
			<button ng-click="ctrl.prePage()" class="btn btn-sm btn-outline-secondary">上一頁</button>
			<span>第<input ng-model="ctrl.nowPage" class="pageN"  disabled="disabled">頁</span>
			<span>共<input ng-model="ctrl.total" class="pageN" disabled="disabled">頁</span>
			<button ng-click="ctrl.aftPage()" class="btn btn-sm btn-outline-secondary">下一頁</button>
			<span>每頁筆數<input type="text" ng-model="ctrl.onePage" class="pageNumber">筆</span>
			<!-- <button ng-click="ctrl.rePagging()" class="btn btn-sm btn-outline-danger">重新分頁</button> -->
		</div>
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
	<div id="head" style="width:100%; z-index: 2;">
		<table style="width:100%;">
			<thead>
				<tr>
					<th ng-repeat="h in ctrl.header">{{h.name}}</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="content">
		<table style="width:100%;">
			<tbody>
				<tr ng-repeat="data in ctrl.tableDatas">
					<td ng-repeat="h in ctrl.header">
						<input ng-if="h.value=='select'" style="height: 20px;width: 50px;" type="checkbox" ng-model="data[h.value]" ng-init="data[h.value]=false" ng-click="ctrl.select(data,data[h.value])">
						<span ng-if="h.value!='select'" >{{data[h.value]}}</span>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
		
	<!-- <div id="buttom">
	</div> -->
</body>
<script src="http://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

<!-- angularJS -->
<script src="js/angular.min.js"></script>
<script src="js/jquery.blockUI.js"></script>
<!-- service -->
<!-- <script src="js/service.js"></script> -->
<!-- page controller -->
<script src="js/common.js"></script>
<script src="js/search.js"></script>

<script>
	$(function(){
		var isroll = false;
		$(window).scroll( function() { 
			var scrollY=$(window).scrollTop(); 
			//$("#images").text(scrollY);
			//$("#images").css("top",scrollY);
			
			if(scrollY>=200){
				$("#functionBar").css("position","fixed").css("top","0");
				$("#head").css("position","fixed").css("top","100px");
				$("#content").css("position","relative").css("bottom","-125px");
				isroll = true;
			}else{
				$("#functionBar").css("position","static").css("top","");
				$("#head").css("position","static").css("top","");
				$("#content").css("position","static").css("bottom","");
				isroll = false;
			}
		})
	})
</script>
	

</html>