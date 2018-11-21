<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="MainApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>


<!-- Bootstrap datepicker 來源 -->
<!-- https://github.com/uxsolutions/bootstrap-datepicker/blob/master/docs/index.rst -->


<!-- boostrp4 css -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="css/bootstrap-datepicker.standalone.css">
<link rel="stylesheet" href="css/bootstrap-datepicker.css">


<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/search2.css">

</head>
<body ng-controller="searchCtrl as ctrl">

	<div id="top">
		<div class="nouse"></div>
		<span class="title" ng-bind = "ctrl.menuList[2].title+ctrl.menuList[2].subTitle"></span>
		<span class="bar"></span>
		<span class="detail" ng-bind = "ctrl.menuList[2].detail"></span>
	</div>
	<div id="functionBar">
		
		<div id="conditionBox">
			<select class="condition" ng-model="ctrl.conditionSelected">
				<option value="">--</option>
				<option ng-repeat="c in ctrl.condition " value="{{c}}">{{c}}</option>
			</select>
			
			<input type="text" id="value" class="value" ng-model="ctrl.v" ng-show="ctrl.conditionSelected == 'ICCID' || ctrl.conditionSelected == 'IMSI'">
			
			<div style="display: inline-block;margin-left: 20px;" class="input-daterange" id="datepicker">
				Date Period:
			    <input type="text" class="date" ng-model="ctrl.start" />
			    <span >to</span>
			    <input type="text" class="date" ng-model="ctrl.end" />
			</div>
			<input type="button" class="sbutton" value="送出" ng-click="ctrl.query()">
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
<script src="js/bootstrap-datepicker.js"></script>
<!-- service -->
<!-- <script src="js/service.js"></script> -->
<!-- page controller -->
<script src="js/common.js"></script>
<script src="js/search2.js"></script>

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
		
		$('#sandbox-container .input-daterange').datepicker({
		    clearBtn: true,
		    daysOfWeekHighlighted: "0,6",
		    autoclose: true,
		    todayHighlight: true
		});
	})
	
	$('.input-daterange').datepicker({
		format: "yyyy/mm/dd",
	    clearBtn: true,
	    daysOfWeekHighlighted: "0,6",
	    autoclose: true,
	    todayHighlight: true
	});
</script>
	

</html>