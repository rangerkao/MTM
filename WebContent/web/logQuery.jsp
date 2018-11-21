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
<body ng-controller="logCtrl as ctrl">

	<div id="functionBar" style="min-width: 1650px;">
		<div id="conditionBox">
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
				<!-- <span class="icon ion-android-archive" ng-click="ctrl.export()"></span> -->
			</div>
		</div>
	</div>
	<div >
		<table id = "dataTable" style="min-width: 1550px;">
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
						<span ng-if="h.value!='params'" >{{data[h.value]}}</span>
						<input ng-if="h.value=='params'" class="btn btn-sm btn-primary" type="button" value= "More ... " ng-show="data['params']" ng-click="ctrl.showParams(data['params'])">
					</td>
					<td></td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document" style="color: black;" align="center">
	    <div class="modal-content" style="width:40em;text-align:left;left:-5em;" >
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">Detail</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body" id="paramsContent" >
	      	<!-- <p ng-repeat="param in ctrl.paramList">
	      		{{param}}
	      	</p> -->
	      	<!-- <form>
			  <div class="form-group row" ng-repeat="param in ctrl.paramList">
			    <label class="col-sm-2 col-form-label">{{param.key}}:</label>
			    <div class="col-sm-10">
			    	<input type="text" readonly class="form-control-plaintext" value="{{param.value}}">
			    </div>
			  </div>
			</form> -->
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">OK</button>
	      </div>
	    </div>
	  </div>
	</div>
	<!-- <form action="exportLogDatas" method="post"  id="reportFrom" style="display: none;">
		<input type="text" name="input">
	</form> -->
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
<script src="js/logQuery.js"></script>

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