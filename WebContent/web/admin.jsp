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
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/admin.css">
</head>
<body ng-controller="adminCtrl as ctrl" style="padding:0;">
	<div id="functionBar" style="min-width:1250px;">
		<div id="conditionBox">
			<input type="button" class="sbutton" value="Change My Password" data-toggle="modal" data-target="#myModal" style="width:10em;">
			<input type="button" class="sbutton" value="Commit Permission Change" ng-click="ctrl.callModal('確定將權限更改成頁面上的狀態?',ctrl.update,null)" ng-disabled="ctrl.updateList.length == 0" style="width:13em;">
			<font color="black" size="6px" ng-show="ctrl.userdata.level<3" >Account:</font>
			<input id="value" type="text" ng-model="ctrl.newAcc" ng-show="ctrl.userdata.level<3">
			<input type="button" class="sbutton" value="Create New Account" ng-click="ctrl.callModal('確定新增帳號'+ctrl.newAcc+'?',ctrl.addItem,null)" ng-show="ctrl.userdata.level<3" ng-disabled="!ctrl.validAcc()" style="width:10em;border-top-left-radius:0;border-bottom-left-radius:0;position:relative;left:-10px;">
			
			
		</div>
	</div>
	<div>
		<table style="min-width:1250px;width: 100%;top:0;" data-ng-init="ctrl.query()">
			<thead id="head">
				<tr>
					<th ng-repeat="head in ctrl.header" style="width: 16%;">{{head.title}}</th>
					<th></th>
					<th ng-repeat="perm in ctrl.permission" style="width: 16%;">{{perm.title}}</th>
				</tr>
			</thead>
			<tbody  id="content">
				<tr ng-repeat="data in ctrl.tableDatas">
					<td ng-repeat="h in ctrl.header" >
						{{data[h.col]}}
					</td>
					<td>
						<button ng-if="$index!=0" type="button" class="btn btn-sm btn-primary" value="" ng-click="ctrl.callModal('確定進行 Reset Password ?', ctrl.resetPassword,data['account'])">  Reset Password</button>
					</td>
					<!-- <td ng-repeat="p in ctrl.permission">
						<div class="btn-group mr-2" role="group" aria-label="First group" >
							<button type="button" class="btn btn-sm {{data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=true">on</button>
							<button type="button" class="btn btn-sm {{!data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=false">off</button>
						</div>
					</td> -->
					<td ng-repeat="p in ctrl.permission">
						<div>
							<button type="button" class="onbtn btn btn-sm" ng-click="ctrl.permClick(data,p.col,false)" ng-show="data[p.col]" ng-disabled="data.level<=ctrl.userdata.level||!ctrl.userdata[p.col]">On</button>
							<button type="button" class="offbtn btn btn-sm" ng-click="ctrl.permClick(data,p.col,true)" ng-show="!data[p.col]" ng-disabled="data.level<=ctrl.userdata.level||!ctrl.userdata[p.col]">Off</button>
						</div>
					</td>
				</tr>
				<!-- <tr ng-hide="ctrl.level>=2" ng-form name="myForm" id="myForm">
					<td ng-repeat="h in ctrl.header" data-ng-init="ctrl.iniAddItem()">
						<input ng-if="h!='level'" class="inputBox" name="{{h}}" ng-model="ctrl.add[h]" type="text" placeholder="{{h}}" required ng-change="ctrl.errorMsg=''">
						<input ng-if="h=='level'" class="inputBox" name="{{h}}" ng-model="ctrl.add[h]" type=text placeholder="{{h}}" required ng-pattern="/^[0-9]{1}$/"  maxlength="1" ng-change="ctrl.errorMsg=''">
					</td>
					ng-pattern="/^[0-9]{1}$/"
					<td ng-repeat="p in ctrl.permission">
						<div class="btn-group mr-2" role="group" aria-label="First group" >
							<button type="button" class="btn btn-sm {{data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=true">on</button>
							<button type="button" class="btn btn-sm {{!data[p]?'btn-primary':'btn-secondary'}}" ng-click="data[p]=false">off</button>
						</div>
					</td>
					<td ng-repeat="p in ctrl.permission" >
						<div>
							<button type="button" class="onbtn btn btn-sm" ng-click="ctrl.permClick(ctrl.add,p,false)" ng-show="ctrl.add[p]" disabled="disabled">On</button>
							<button type="button" class="offbtn btn btn-sm" ng-click="ctrl.permClick(ctrl.add,p,true)" ng-show="!ctrl.add[p]" disabled="disabled">Off</button>
						</div>
					</td>
				</tr> -->
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
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document" style="color: black;" align="center">
	    <div class="modal-content" style="width: 20em;background-color: rgb(247, 223, 241);">
	      <div class="modal-header">
	        <h5 class="modal-title">Change Password</h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body" align="center" style="">
	      	
	      	<label>Original</label><br>
	      	<input type="password" ng-model="ctrl.change.origin" ><br>
	      	<label>New password</label> <br>
	      	<input type="password" ng-model="ctrl.change.new" ><br>
	      	<label>Check again</label> <br>
	      	<input type="password" ng-model="ctrl.change.new2" ><br>
	      	<label ng-show="ctrl.change.new!=ctrl.change.new2" ><font color="red">新密碼不一致</font></label> <br>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-dismiss="modal" ng-disabled="ctrl.changeValid()" ng-click="ctrl.changePassword()">ok</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">cancel</button>
	      </div>
	    </div>
	  </div>
	</div>
	<div class="modal fade" id="functiobModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
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
	        <button type="button" class="btn btn-secondary" data-dismiss="modal" ng-click="ctrl.modalFunction(ctrl.param)">確定</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal" ng-show="ctrl.showCancel">取消</button>
	      </div>
	    </div>
	  </div>
	</div>
	<div class="modal fade" id="MsgModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
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
<%-- <script src="js/service.js"></script> --%>
<!-- page controller -->
<script src="js/common.js"></script>
<script src="js/admin.js"></script>
</html>