angular.module('MainApp')
	.controller('adminCtrl',['Menu','AjaxService','CommonService','ActionService',function(Menu,AjaxService,CommonService,ActionService){
		var self = this;
		//console.log("angular load success!");
		
		self.menuList = Menu.List;
		
		//self.header = ['account','level','owner','password'];
		self.header = [{title:'Account',col:'account'}];
		//self.permission = ['search','suspend','resume','terminate','reset'];
		self.permission = [
			{title:'De-activated',col:'suspend'},
			{title:'Activated',col:'resume'},
			{title:'Retired',col:'terminate'},
			{title:'Reset-speed',col:'reset'}];
		self.tableDatas = [];
		
		self.updateList = [];
		self.newList = [];
		
		self.userdata;
		
		
		self.query = function(){
			ActionService.block();
			AjaxService.query("queryPermission",{})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
				}else{
					//console.log(data['data']);
					self.tableDatas = data['data'];
					self.userdata = self.tableDatas[0];
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    	self.queryUserList();
		    });
		}
		
		//點擊權限按鈕
		self.permClick = function(data,item,type){
			data[item]=type;
			self.dataChange(data['account']);
		}
		
		// 增加需要更新的帳號
		self.dataChange = function(account){
			if($.inArray(account,self.updateList)==-1)
				self.updateList.push(account);	
			//console.log($.inArray(account,self.updateList));
		}
		//更新
		self.update = function(){
			ActionService.block();
			var input = angular.toJson({addItem:self.newList,updateItem:self.updateList,dataList:self.tableDatas});
			AjaxService.query("updatePermission",{input:input})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}else{
					if(data['error']){
						//console.log(data['error']);
						alert(data['error']);
					}else{
						//console.log(data['data']);
						self.tableDatas = data['data'];
						self.updateList = [];
					}
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	
		    	ActionService.unblock();
		    });
		}
		
		self.validAcc = function(){
			if(!self.newAcc)
				return false;
			
			if(self.newAcc == '')
				return false;
			
			var log = [];
			var tag = true;
			angular.forEach(self.accList, function(value, key) {
				if(key.toUpperCase() == self.newAcc.toUpperCase()){
					tag = false;
				}
			}, log);
			/*var index = self.selectedItem.indexOf(obj);*/
			
			return tag;
		}
		
		//新增使用者
		self.addItem = function(){
			ActionService.block();
			var input = angular.toJson({account:self.newAcc});
			AjaxService.query("newAccount",{input:input})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}else{
					if(data['error']){
						//console.log(data['error']);
						alert(data['error']);
					}else{
						self.newAcc = '';
						//console.log(data['data']);
						self.tableDatas = data['data'];
						self.updateList = [];
					}
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	
		    	ActionService.unblock();
		    });
		}
		
		//撈取帳號
		self.accList = {};
		self.queryUserList = function(){
			ActionService.block();
			var input = {};
			AjaxService.query("queryUserList",input)
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
				}else{
					//console.log(data['data']);
					self.accList = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		}
		
		self.changePassword = function(){
			ActionService.block();
			var input = angular.toJson(self.change);
			AjaxService.query("changePassword",{input:input})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
				}else{
					//console.log(data['data']);
					$('#MsgModal').modal('show');
					self.message = data['data'];
					self.change={origin:'',new:'',new2:''};
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		};
		
		self.param;
		self.callModal=function(msg,fun,param){
			self.modalFunction = fun;
			self.param = param;
			self.message = msg;
			$('#functiobModal').modal('show');
		}
		
		
		self.resetPassword = function(account){
			ActionService.block();
			var input = angular.toJson({account:account});
			AjaxService.query("changePassword",{input:input})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
				}else{
					//console.log(data['data']);
					$('#MsgModal').modal('show');
					self.message = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		};
		
		
		
		
		
		
		
		self.change={origin:'',new:'',new2:''};
		
		self.changeValid = function(){
			if(self.change.origin && self.change.new && self.change.new2 && self.change.new == self.change.new2)
				return false;
			
			return true;
		};



		
		
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
		
		
		

		
		
		/*function backHomePage(){
			window.location.href= "logout";
		}*/
		
	}]);