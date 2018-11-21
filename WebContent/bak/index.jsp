<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>

    <script src="http://code.jquery.com/jquery-3.3.1.min.js" 
    	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
	<style type="text/css">
    	html, body{
		      height:100%;
		}
		body {
			background-image: url("pic/background_1.jpg");
			background-position:center;
			
			margin:0; padding:0;
			color: white;text-align: center;
			
			overflow: hidden;
		}
		div {
		
		}
    </style>
</head>
<body >
<h1>Sim2Travel M2M Control Portal</h1>
<form method="post" action="login" onsubmit="return beforeSubmit()">
	<div>
		<div>
			<label>帳號：</label>
			<input type="text" name="account">
		</div>
		<div >
			<label>密碼：</label>
			<input id = "pass1" type="password" name="password">
			<input id = "pass2" type="text" name="password2">
		</div>
		<div >
			<input type="checkbox" id = "showPassword" onclick="showPass()">顯示密碼
			<input type="submit" value="登入">
		</div>
		<div>
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
</form>
</body>
<script>
    $(function() {
    	$("#pass2").hide();
    	
    	$("form").keydown(function(){
    			//console.log(event.keyCode);
    			/* if(event.keyCode == 13){
    				$("form").submit();
    			} */
    		});
    });
   
    //顯示密碼功能
   	function showPass(){
   		if($("#showPassword").is(":checked")){
   			$("#pass2").val($("#pass1").val());
   			$("#pass2").show();
   			$("#pass1").hide();
   		}else{
   			$("#pass1").val($("#pass2").val());
   			$("#pass2").hide();
   			$("#pass1").show();
   		}
   	}
   
    function beforeSubmit(){
    	if(!$("#showPassword").is(":checked")){
    		$("#pass2").val($("#pass1").val());
   		}else{
   			$("#pass1").val($("#pass2").val());
   			
   		}
    	console.log("HostName:"+$("input[name='account']").val()+";PassWord:"+$("input[name='password']").val()+";PassWord:"+$("input[name='password2']").val())
    	return true;
    }
   </script>
</html>