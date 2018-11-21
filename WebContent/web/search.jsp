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

<link rel="stylesheet" href="http://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">


</head>
<body ng-controller="searchCtrl as ctrl">
	<div id="functionBar" style="min-width: 1660px;">
		<div id="conditionBox">
			<select class="condition" ng-model="ctrl.conditionSelected">
				<option value="">--</option>
				<option ng-repeat="c in ctrl.condition " value="{{c}}">{{c}}</option>
			</select>
			
			<input type="text" id="value" ng-model="ctrl.v" ng-show="ctrl.conditionSelected == 'ICCID' || ctrl.conditionSelected == 'IMSI'">
			
			<select class="condition" ng-model="ctrl.selectedUser" ng-show="ctrl.user.USER_LEVEL == 0 ">
				<option value="">--</option>
				<option ng-repeat="(key, value) in ctrl.userList " value="{{value}}">{{key}}</option>
			</select>
			<select class="condition" ng-model="ctrl.statusSelected" ng-show="ctrl.conditionSelected == 'Status' ">
				<option value="">--</option>
				<option ng-repeat="s in ctrl.status " value="{{s.value}}">{{s.label}}</option>
			</select>
			<input type="button" class="sbutton" value="send" ng-click="ctrl.query()">
			<div style="display: inline-block;" ng-show="ctrl.selectedItem.length>0">
				<select class="condition" ng-model="ctrl.actionSelected">
					<option value="">--</option>
					<option ng-repeat="i in ctrl.action" value="{{$index}}">{{i.label}}</option>
				</select>
				<input type="button" class="sbutton" value="execute" ng-disabled="ctrl.actionSelected==null ||ctrl.actionSelected==''|| ctrl.selectedItem.length>10 " ng-click="ctrl.provision()">
			</div>
			<div id = "pageBox" >
				<!-- <button ng-click="ctrl.prePage()" class="btn btn-sm btn-outline-secondary">pre</button> -->
				<span class="icon ion-android-arrow-dropleft" ng-click="ctrl.prePage()"></span>
				<span class="icon ion-android-arrow-dropright" ng-click="ctrl.aftPage()"></span>
				<span>page<input ng-model="ctrl.nowPage" class="pageN"  disabled="disabled"></span>
				<span>total<input ng-model="ctrl.total" class="pageN" disabled="disabled"></span>
				<!-- <button ng-click="ctrl.aftPage()" class="btn btn-sm btn-outline-secondary">next</button> -->
				<span>one page:<input type="text" ng-model="ctrl.onePage" class="pageN"></span>
				<!-- <button ng-click="ctrl.rePagging()" class="btn btn-sm btn-outline-danger">pagging</button> -->
				<span class="icon ion-ios-refresh-empty" ng-click="ctrl.rePagging()"></span>
				<span class="icon ion-android-archive" ng-click="ctrl.export()"></span>
			</div>
		</div>
	</div>
	<div >
		<table style="min-width: 1660px;">
			<thead id="head">
				<tr>
					<th style="width:20px;min-width:20px ;max-width:20px ;">
					</th>
					<th ng-repeat="h in ctrl.header" ng-click="ctrl.titleClicked(h)" 
						style="width:{{h.width}};min-width: {{h.width}};max-width:{{h.width}} ;text-align:{{h.align}};">
						<!-- 20180528 select all -->
						<input ng-if="h.value=='isSelect'" type="checkbox" ng-model="ctrl.isSelectAll" ng-click="ctrl.selectAllselected(ctrl.isSelectAll=!ctrl.isSelectAll)">				
						
						<div ng-if="h.value!='isSelect'">
							{{h.name}}
							<span class="ion-android-arrow-dropup" ng-show="h.order=='asc'"></span>
							<span class="ion-android-arrow-dropdown" ng-show="h.order=='desc'"></span>
						</div>
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody id="content">
				<tr ng-repeat="data in ctrl.tableDatas">
					<td>{{$index +1 }}</td>
					<td ng-repeat="h in ctrl.header" style="text-align:{{h.align}};">
						<input ng-if="h.value=='isSelect'" type="checkbox" ng-model="data[h.value]" ng-click="ctrl.select(data,data[h.value]=!data[h.value])">
						<span ng-if="h.value!='isSelect'" >{{data[h.value]}}</span>
					</td>
					<td></td>
				</tr>
			</tbody>
		</table>
	</div>
	<form action="exportDatas" method="post"  id="reportFrom" style="display: none;">
		<input type="text" name="input">
	</form>
	<!-- Button trigger modal -->
	<!-- <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
	  Launch demo modal
	</button> -->
	<!-- Modal -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document" style="color: black;">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">Tip Message</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	      	{{ctrl.message}}
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-primary" data-dismiss="modal"  ng-click="ctrl.executeProvision()">確定</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
	      </div>
	    </div>
	  </div>
	</div>
	<div class="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document" style="color: black;">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">Tip Message</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	      	{{ctrl.message}}
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">確定</button>
	      </div>
	    </div>
	  </div>
	</div>
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
	/* $(function(){
		var isroll = false;
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
		})
	}) */
</script>
	

</html>