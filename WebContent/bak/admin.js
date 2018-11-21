angular.module('MainApp')
	.controller('adminCtrl',['Menu','AjaxService','CommonService','ActionService',function(Menu,AjaxService,CommonService,ActionService){
		var self = this;
		console.log("angular load success!");
		
		self.menuList = Menu.List;
		
		self.header = ['account','level','owner','password'];
		self.permission = ['search','suspend','resume','terminate'];
		self.level = $("#permission").html();
		self.subsidiaryid = $("#subsidiaryid").html();
		self.tableDatas = [];
		
		self.updateList = [];
		self.newList = [];
		
		self.query = function(){
			ActionService.block();
			AjaxService.query("queryPermission",{})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data['input']){
					CommonService.backHomePage();
				}
				if(data['error']){
					console.log(data['error']);
				}else{
					console.log(data['data']);
					self.tableDatas = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    	ActionService.unblock();
		    	self.queryUserList();
		    });
		}
		
		self.userList = [];
		self.queryUserList = function(){
			ActionService.block();
			AjaxService.query("queryUserList",{})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data['input']){
					CommonService.backHomePage();
				}
				if(data['error']){
					console.log(data['error']);
				}else{
					console.log(data['data']);
					self.userList = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		}
		
		
		self.update = function(){
			ActionService.block();
			var input = angular.toJson({addItem:self.newList,updateItem:self.updateList,dataList:self.tableDatas});
			AjaxService.query("updatePermission",{input:input})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data['input']){
					CommonService.backHomePage();
				}else{
					if(data['error']){
						console.log(data['error']);
					}else{
						console.log(data['data']);
						self.tableDatas = data['data'];
						self.updateList = [];
						self.newList = [];
					}
				}
				
				
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    	
		    	ActionService.unblock();
		    });
		}
		
		self.errorMsg;
		
		function validAddItem(){
			if($.inArray(self.add['account'],self.userList)!=-1){
				self.errorMsg = '重複的使用者名稱';
				return false;
			}
			if(self.add['level']<self.level){
				self.errorMsg = '層級不可小於自身';
				return false;
			}
			return true;
				
		}
		
		//新增使用者
		self.addItem = function(){
			if(!validAddItem())
				return;
			self.tableDatas.push(self.add);
			self.newList.push(self.add['account']);
			self.userList.push(self.add['account']);
			self.iniAddItem();
		}
		//初始化新增項目
		self.iniAddItem = function(){
			self.add = {Account:'',password:'',Level:'',owner:$("#user").html(),subsidiaryid:$("#subsidiaryid").html(),search:true,suspend:false,resume:false,terminate:false};
		}
		//點擊權限按鈕
		self.permClick = function(data,item,type){
			data[item]=type;
			self.dataChange(data['account']);
		}
		// 增加需要更新的帳號
		self.dataChange = function(account){
			if($.inArray(account,self.updateList)==-1 && $.inArray(account,self.newList)==-1)
				self.updateList.push(account);	
			console.log($.inArray(account,self.updateList));
		}
		
		/*function backHomePage(){
			window.location.href= "logout";
		}*/
		
	}]);