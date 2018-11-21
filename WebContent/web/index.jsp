<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Sim2Travel M2M Control Portal</title>
<link rel="Shortcut Icon" type="image/x-icon" href="pic/s2t_icon.jpg" />
<script src="http://code.jquery.com/jquery-3.3.1.min.js" 
    	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
    	


<style type="text/css">
    	html, body{
		      height:100%;
		      margin: 0;
		      font-family: Calibri;
		}
		body{
			background-color: white;
			display:flex;
   			align-items:center;
   			justify-content:center;
		}
		#container{
			height : 420px;
			width : 800px;
			/* background-color: gray; */
			background-image: url("pic/S2TCPgreeting2.jpg");
			background-repeat: no-repeat;
			background-position:center;
			background-size: contain;
			
		}
		#left{
			height: 100%;
			width: 70%;
			/* background-color: orange; */
			display: inline-block;
			float: left;
		}
		#right{
			height: 100%;
			width: 30%;
			/* background-color: silver; */
			display: inline-block;
			float: left;
		}
		#form{
			display:flex;
   			align-items :flex-end;
   			justify-content:center;
		}
		#error li{
			list-style-type: none;
		}
		
		#button {
		   /* border-top: 1px solid #96d1f8; */
		   background: #e6a627;
		   background: -webkit-gradient(linear, left top, left bottom, from(#d6ed56), to(#e6a627));
		   background: -webkit-linear-gradient(top, #d6ed56, #e6a627);
		   background: -moz-linear-gradient(top, #d6ed56, #e6a627);
		   background: -ms-linear-gradient(top, #d6ed56, #e6a627);
		   background: -o-linear-gradient(top, #d6ed56, #e6a627);
		   padding: 5px 10px;
		   -webkit-border-radius: 8px;
		   -moz-border-radius: 8px;
		   border-radius: 8px;
		   /* -webkit-box-shadow: rgba(0,0,0,1) 0 1px 0; */
		   /* -moz-box-shadow: rgba(0,0,0,1) 0 1px 0; */
		   /* box-shadow: rgba(0,0,0,1) 0 1px 0; */
		   /* text-shadow: rgba(0,0,0,.4) 0 1px 0; */
		   /* color: #fa3a3a; */
		  /*  font-family: Georgia, serif; */
		   text-decoration: none;
		   vertical-align: middle;
		   
		   margin-right: 5px;
		   }
		#button:hover {
		   /* border-top-color: #de6e18; */
		   background: #de6e18;
		   /* color: #cc3737; */
		   }
		#button:active {
		   border-top-color: #ab592c;
		   background: #ab592c;
		   }
		ul{
			padding: 0;
		}
		
		.titleFontSize{
			font-size: 35px;
		}
		
		.contentFontSize{
			font-size: 20px;
		}
</style>
</head>
<body>

	<div id="container">
		<div id="left">
		</div>
		<div id="right">
				<div style="height: 50%;width: 100%;text-align: center;vertical-align: bottom;">
					<div style="height: 40%;"></div>
					<label class="titleFontSize">Control Portal</label>
				</div>
				<div id="form"	style="height: 25%;width: 100%;">
					<form method="post" action="login" onsubmit="return beforeSubmit()">
						<label class="contentFontSize" style="margin-left: 5px;">Account：</label>
						<div style="text-align: center;">
							<input id="account" class="contentFontSize" type="text" name="account" style="width: 95%;margin: 5px;" >
						</div>
						<label class="contentFontSize" style="margin-left: 5px;">Password：</label>
						<div style="text-align: center;">
							<input id = "pass1" class="contentFontSize" type="password" name="password" style="width: 95%;margin: 5px;">
						</div>
						<div style="text-align: right;">
							<input id="button" class="contentFontSize" type="submit" value="Enter" style="height: 30px;width: 80px;">
						</div>
					</form>
				</div>
				
				<div id="error" class="contentFontSize" style="height: 25%;width: 100%;text-align: center;">
					<s:if test="hasActionMessages()">
					   <div class="welcome">
					      <s:actionmessage/>
					   </div>
					</s:if>
					<s:if test="hasActionErrors()">
					   <div class="errors">
					      <s:actionerror/>
					   </div>
					</s:if>
					<s:if test="hasFieldErrors()">
					   <div class="errors">
					      <s:fielderror/>
					   </div>
					</s:if>
				</div>
		</div>
	</div>
</body>
<script>
$(document).ready(function(){
	console.log($("#form1").serialize());
	$("#account").focus();
})


</script>
</html>