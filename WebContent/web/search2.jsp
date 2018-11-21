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

<link rel="stylesheet" href="http://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">

</head>
<body ng-controller="searchCtrl as ctrl">

	<div id="functionBar" style="min-width: 1650px;">
		<div id="conditionBox">
			<select class="condition" ng-model="ctrl.conditionSelected">
				<option value="">--</option>
				<option ng-repeat="c in ctrl.condition " value="{{c}}">{{c}}</option>
			</select>
			
			<input type="text" id="value" class="value" ng-model="ctrl.v" ng-show="ctrl.conditionSelected == 'ICCID' || ctrl.conditionSelected == 'IMSI'">
			<select class="condition" ng-model="ctrl.selectedUser" ng-show="ctrl.user.USER_LEVEL == 0 ">
				<option value="">--</option>
				<option ng-repeat="(key, value) in ctrl.userList " value="{{value}}">{{key}}</option>
			</select>
			<div id="datepicker">
				Date Period:
			    <input type="text" class="date" ng-model="ctrl.start" />
			    <span >to</span>
			    <input type="text" class="date" ng-model="ctrl.end" />
			</div>
			<input type="button" class="sbutton" value="send" ng-click="ctrl.query()">
			<div id = "pageBox" >
				<!-- <button ng-click="ctrl.prePage()" class="btn btn-sm btn-outline-secondary">pre</button> -->
				<span class="icon ion-android-arrow-dropleft" ng-click="ctrl.prePage()"></span>
				<span class="icon ion-android-arrow-dropright" ng-click="ctrl.aftPage()"></span>
				<span>page<input ng-model="ctrl.nowPage" class="pageN"  disabled="disabled"></span>
				<span>total<input ng-model="ctrl.total" class="pageN" disabled="disabled"></span>
				<!-- <button ng-click="ctrl.aftPage()" class="btn btn-sm btn-outline-secondary">next</button> -->
				<span>one page<input type="text" ng-model="ctrl.onePage" class="pageN"></span>
				<span class="icon ion-ios-refresh-empty" ng-click="ctrl.rePagging()"></span>
				<span class="icon ion-android-archive" ng-click="ctrl.export()"></span>
			</div>
		</div>
	</div>
	<div >
		<table id = "dataTable" style="min-width: 1650px;">
			<thead id="head">
				<tr>
					<th style="width:20px;min-width:20px ;max-width:20px ;"></th>
					<th ng-repeat="h in ctrl.header"   ng-click="ctrl.titleClicked(h)" 
						style="width:{{h.width}};min-width:{{h.width}} ;max-width:{{h.width}} ;text-align:{{h.align}};">
						&nbsp;{{h.name}}
						<span class="ion-android-arrow-dropup" ng-show="h.order=='asc'"></span>
						<span class="ion-android-arrow-dropdown" ng-show="h.order=='desc'"></span>
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody id="content">
				<tr ng-repeat="data in ctrl.tableDatas">
					<td>{{$index+1}}</td>
					<td ng-repeat="h in ctrl.header" style="text-align:{{h.align}};">
						<input ng-if="h.value=='select'" style="height: 20px;width: 50px;" type="checkbox" ng-model="data[h.value]" ng-init="data[h.value]=false" ng-click="ctrl.select(data,data[h.value])">
						<span ng-if="h.value!='select'" >{{data[h.value]}}</span>
					</td>
					<td></td>
				</tr>
			</tbody>
		</table>
	</div>
	<form action="exportUsageDatas" method="post"  id="reportFrom" style="display: none;">
		<input type="text" name="input">
	</form>
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
		/* var isroll = false;
		$(window).scroll( function() { 
			var scrollY=$(window).scrollTop(); 
			//$("#images").text(scrollY);
			//$("#images").css("top",scrollY);
			var functionbarHeight = $("#functionBar").height();
			var headerHeight = $("#head").height();
			
			if(scrollY>=functionbarHeight){
				$("#functionBar").css("position","fixed").css("top","0");
				$("#head").css("position","fixed").css("top",functionbarHeight);
				$("#content").css("position","relative").css("bottom",0-functionbarHeight-headerHeight);
				isroll = true;
			}else{
				$("#functionBar").css("position","static").css("top","");
				$("#head").css("position","static").css("top","");
				$("#content").css("position","static").css("bottom","");
				isroll = false;
			}
		}) */
		
		$('#sandbox-container .input-daterange').datepicker({
		    clearBtn: true,
		    daysOfWeekHighlighted: "0,6",
		    autoclose: true,
		    todayHighlight: true
		});
	})
	
	$('.date').datepicker({
		format: "yyyy/mm/dd",
	    clearBtn: true,
	    daysOfWeekHighlighted: "0,6",
	    autoclose: true,
	    todayHighlight: true
	});
</script>
	

</html>