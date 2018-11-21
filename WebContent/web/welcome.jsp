<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="UTF8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="MainApp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Sim2Travel M2M Control Portal</title>


<link rel="stylesheet" href="http://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
<link rel="stylesheet" href="css/common.css">



<link rel="Shortcut Icon" type="image/x-icon" href="pic/s2t_icon.jpg" />

<style type="text/css">
	html,body{
		background-color: rgb(200,200,200);
		margin:0;
		font-family: Calibri;
	}
	h1{
		margin:0;
	}
	
	#page body{
		margin:0;
	}
	
	
	/*Links文章去除連結底線*/
	a,a:link,a:visited{color:#904E0E;text-decoration: none}
	a:hover{color:#F4AB25;background-color: #FFECD9;text-decoration: none;}

	
	#top{
		color:black;
		height: 40px;
		background-color: white;
		display:flex;
 		align-items:center;
 		justify-content:center;
 		margin: 5px;
 		border-radius: 10px;
	}
	#function{
		height: 80px;
		background-color: white;
		margin: 5px 5px 0 5px;
		border-radius: 10px;
	}
	#page{
		width:99%;
		background-color: white;
		border: 0;
		margin: 5px;
		border-radius: 10px;
	}
	.functionBox{
		height: 70px;
		width: 70px;
		display: inline-block;
		float: left;
		margin: 5px;
		background-color: white;
		border-radius: 10px;
		
		/* border:1px rgb(200,200,200) solid; */
		
		text-align:center;
		
		display:flex;
 		align-items:center;
 		justify-content:center;
 		
	}
	.functionBox:hover{
		box-shadow:inset 5px 5px 20px rgba(20%,20%,40%,0.5);
		background-color: rgba(20%,20%,40%,0.3);
	}
	
	.selectedItem{
		box-shadow:inset 5px 5px 20px rgba(10%,10%,10%,0.5);
		background-color: rgba(20%,20%,40%,0.3);
	}
</style>


</head>
<body >
	<div id="top"><h1>Sim2Travel Control Portal</h1></div>
	<div id="function" ng-controller="mainController as ctrl">
	{{ctrl.selectedItem}}
		<a class="functionBox {{$index == ctrl.selectedItem?' selectedItem ':' '}}"  ng-repeat="item in ctrl.menuList" href="{{item.link}}" target="page" style="background-image:url('{{item.pic}}');background-size: contain;" ng-click="ctrl.selectedItem = $index">
			<!-- <div>
				{{item.title}}
			</div>	 -->
		</a>
		<a class="functionBox"  href="logout" style="float: right;background-image:url('pic/logout_icon.jpg');background-size: contain;  " >
			<!-- <div>
				Log out
			</div> -->	
		</a>
	</div>
	<iframe id="page" name="page"></iframe>
</body>
<script src="http://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

<script>
	var pageHeight = $(document ).height()-$("#top").height()-$("#function").height()-25;
	$("#page").height(pageHeight);
</script>
<!-- angularJS -->
<script src="js/angular.min.js"></script>

<script src="js/common.js"></script>
</html>