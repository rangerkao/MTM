angular.module('MainApp')
	.controller('searchCtrl',['Menu','AjaxService','CommonService','ActionService','$filter','$timeout',function(Menu,AjaxService,CommonService,ActionService,$filter,$timeout){
		var self = this;
		//console.log("angular load success!");
		
		self.menuList = Menu.List;
		self.condition = ['ICCID','IMSI'/*,'Status'*/];
		//0:normal,3:suspended,4:terminated,10:waitting
		self.status = [
			{label:'Activation Ready',value:'0'},
			{label:'Activated',value:'1'},
			{label:'De-activated',value:'3'},
			{label:'Retired',value:'4,10'}
			];

		//self.header = ['select','serviceid','iccid','imsi','status','volume'];
		//ICCID, IMSI, Status, Last Visited Network
		self.header = [
			{name:'',value:'isSelect',align:"left",width:"60px"},
			{name:'ICCID',value:'iccid',align:"left",width:"190px"},
			{name:'IMSI',value:'imsi',align:"left",width:"150px"},
			{name:'Status',value:'status',align:"left",width:"100px"},
			/*{name:'Last Visited Network',value:'lastVisitedNetwork',align:"left",width:"150px"},*/
			{name:'MME Name',value:'mmename',align:"left",width:"450px"},
			{name:'IMEI',value:'imei',align:"left",width:"140px"},
			{name:'SGSN Number',value:'sgsnnumber',align:"left",width:"110px"},
			{name:'PIN1',value:'pin1',align:"left",width:"110px"},
			{name:'PIN2',value:'pin2',align:"left",width:"110px"},
			{name:'PUK1',value:'puk1',align:"left",width:"110px"},
			{name:'PUK2',value:'puk2',align:"left",width:"110px"}
			];
		self.datas = [];
		self.tableDatas = [];
		/*for(var i = 0;i<56;i++){
			self.datas.push({ICCID:'data'+i,IMSI:'data'+i,Volume:'data'+i,Action:'data'+i,Button:'data'+i});
		}*/
		function processCondition(){
			var condition = [];
			if(self.conditionSelected == 'ICCID'){
				condition.push({ICCID:self.v});
			}else if(self.conditionSelected == 'Status'){
				condition.push({Status:self.statusSelected});
			}else if(self.conditionSelected == 'IMSI'){
				condition.push({IMSI:self.v});
			}
			
			if(self.selectedUser){
				condition.push({subsidiaryid:self.selectedUser});
			}
			
			return condition;
		}
		
		//資料查詢功能*****************
		
		self.query = function(){
			self.nowPage = 1;
			self.datas = [];
			self.queryCount();
			
			self.isSelectAll = false;
			self.selectedItem = [];
			flashSelectedStatus();
		}
		
		self.queryCount = function(){
			ActionService.block();
			var condition = processCondition();
			var input = angular.toJson(condition);
			AjaxService.query("queryCount",{input:input})
			.success(function(data, status, headers, config) {
				//console.log(data);
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
					ActionService.unblock();
				}else{
					////console.log(data['data']);
					self.totalData = data['data'];
					self.total = Math.ceil(self.totalData/self.onePage);
					self.queryDatas();
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	
		    });
		}

		
		self.queryDatas = function(){
			var condition = processCondition();
			//ActionService.block();
			var input = angular.toJson({startPage:self.nowPage,onePage:self.onePage,orderby:self.order,asc:self.asc,condition:condition});
			AjaxService.query("queryDatas",{input:input})
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
					self.datas = self.datas.concat(data['data']);
					//console.log("datas:("+self.datas.length+")");
					////console.log(self.datas);
					self.dataPage = self.nowPage+6;
				}
				ActionService.unblock();
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	self.pagging();
		    });
		};
		
		
		//撈取帳號
		self.userList = {};
		self.queryUserList = function(){
			ActionService.block();
			var input = {queryLevel:'1'};
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
					self.userList = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		}();
		//self.queryUserList();
		
		//撈取帳號
		self.user = {};
		self.queryUser = function(){
			ActionService.block();
			AjaxService.query("queryUser",{})
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
					self.user = data['data'];
				}
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	ActionService.unblock();
		    });
		}();
		
		//self.queryUser();
		
		
		//項目選擇*************
		self.select = function(obj,status){
			////console.log(status);
			if(status){
				self.selectedItem.push(obj);
			}else{
				var log = [];
				var index = -1;
				angular.forEach(self.selectedItem, function(value, key) {
					if(value.imsi == obj.imsi)
						index = key;
				}, log);
				/*var index = self.selectedItem.indexOf(obj);*/
				//console.log(index);
				if(index!=-1){
					self.selectedItem.splice(index, 1)
				}
			}
			flashSelectedStatus();
		}
		self.selectedItem = [];
		self.selectedStatus = [];
		self.selectedIMSI = [];
		

		
		self.isSelectAll = false;
		self.selectAllselected = function(status){
			if(status){
				self.selectedItem = self.tableDatas;
				flashSelectedStatus();
				self.selectAll(self.selectedItem,status);
			}else{
				self.selectAll(self.selectedItem,status);
				self.selectedItem = [];
				flashSelectedStatus();
			}		
		}
		self.selectAll = function(list,status){
			angular.forEach(list, function(value, key) {
				value['isSelect'] = status;
			});
		}
		
		function pageAllselected(){
			self.isSelectAll = true;
			angular.forEach(self.tableDatas, function(value, key) {
				if(!value['isSelect']) self.isSelectAll = false;
			});
		}
		
		
		
		//供裝動作*****************
		
		self.userAction = [
			{label:'Reset-speed',value:'00'},
			{label:'Activated',value:'02'},
			{label:'De-activated',value:'01'},
			{label:'Retired',value:'99'}
			];
		
		self.queryUserAction = function(){
			ActionService.block();
			var condition = processCondition();
			var input = angular.toJson(condition);
			AjaxService.query("queryUserAction",{input:input})
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
					////console.log(data['data']);
					self.userAction = data['data'];
					//console.log("self.userAction:"+self.userAction);
				}
				
				ActionService.unblock();
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	
		    });
		}();

		//self.queryUserAction();
		
		
		
		//執行動作
		self.action = [];
		function flashSelectedStatus(){
			//console.log("self.selectedItem:"+self.selectedItem.length);
			//console.log(self.selectedItem);
			
			self.selectedIMSI = [];
			self.selectedStatus = [];
			
			var log = [];
			
			//還原所有狀態
			self.action = self.userAction.slice();
			
			angular.forEach(self.selectedItem, function(value, key) {
				self.selectedIMSI.push(value.imsi);
				
				var status = value.status;
				
				if(self.selectedStatus.indexOf(value.status)==-1){
					self.selectedStatus.push(status);
				}
				
				//如果有Terminated，禁止勾選任何動作
				if('Terminated' == status || 'Retired' == status){ 
					self.action = [];
				}
				
				//如果有Activation Ready，
				if('Activation Ready' == status || 'Retired' == status){ 
					//self.action = [];
				}
				//key 是 key or index
				//如果有Normal，禁止勾選Resume
				else if('Normal' == status || 'Activated' == status){
					angular.forEach(self.action, function(value, key) {
						//if(value.label=='Resume') self.action.splice(key, 1);
						if(value.label=='Activated') self.action.splice(key, 1);
					});
				}
				
				//如果有Suspend，禁止Suspend，禁止Reset
				else if('Suspend'== status || 'De-activated' == status){
					angular.forEach(self.action, function(value, key) {
						//if(value.label=='Suspend') self.action.splice(key, 1);
						if(value.label=='De-activated') self.action.splice(key, 1);
						if(value.label=='Reset-speed') self.action.splice(key, 1);
					});
				}
				
			}, log);

			//如果個數超過1個，禁止選擇Reset
			/*if(self.selectedItem.length>=2){
				angular.forEach(self.action, function(value, key) {
					//key 是 key or index
					if(value.label=='Reset-speed'){
						self.action.splice(key, 1);
					}
				});
			}*/
			////console.log('log:'+log);
		}
		
		flashSelectedStatus();
		
		//供裝**************
		self.message = "";
		self.provision = function(){			
			if(self.selectedIMSI.length==0||!self.actionSelected)
				return;
			else{
				$('#myModal').modal('show');
				self.message = "確定要對此"+self.selectedIMSI.length+"筆執行"+self.action[self.actionSelected].label+"?"+(self.actionSelected==0?'(Reset-speed 約需10分鐘後生效)':'');
			}
			
		}
		
		self.executeProvision = function(){			
			ActionService.block();
			var input = angular.toJson({action:self.action[self.actionSelected].value,imsis:self.selectedIMSI});
			AjaxService.query("executeProvision",{input:input})
			.success(function(data, status, headers, config) {
				if(data['input']){
					//CommonService.backHomePage();
					window.parent.location.href= "logout";
				}
				if(data['error']){
					//console.log(data['error']);
					alert(data['error']);
				}else{
					$('#myModal2').modal('show');
					self.message = "Provision finished.";
			    	self.query();
				}
				ActionService.unblock();
				
		    }).error(function(data, status, headers, config) {   
		    	alert("error");
		    }).then(function(){
		    	
		    });
			
			
		}
		
		
		
		
		//分頁功能*******************
		
		self.nowPage = 1;
		self.dataPage = 0;
		self.total = 0;
		self.totalData = 0
		self.onePage = 50;	
		
		self.rePagging = function(){
			self.query();
		} 
		
		self.prePage = function(){
			if(self.nowPage>1){
				self.nowPage = self.nowPage -1;
				self.pagging();
				pageAllselected();
			}
		}
		self.aftPage = function(){
			if(self.nowPage<self.total){
				self.nowPage = self.nowPage +1;
				if(self.nowPage==self.dataPage){
					ActionService.block();
					self.queryDatas();
				}else{
					self.pagging();
				}
				pageAllselected();
			}
		}
		
		self.pagging = function(){
			self.tableDatas = [];
			for(var i = (self.nowPage-1)*self.onePage ; i<self.nowPage*self.onePage && i < self.datas.length ;i++){
				self.tableDatas.push(self.datas[i]);
			}
			//填空白
			/*if(self.tableDatas.length >0){
				for(var i = self.tableDatas.length ; i < self.onePage; i++ ){
					var d = [];
					for(var j = 0 ; j<self.header.length ; j++ ){
						d.push("{"+self.header[j]+":' '}");
					}
					self.tableDatas.push(d);
				}
			}*/
		}
		
		
		//報表下載功能***********
		self.export = function(){
			//console.log("exporting");
			
			var condition = processCondition();
			var input = angular.toJson({startPage:self.nowPage,onePage:self.onePage,orderby:self.order,asc:self.asc,condition:condition,head:self.header});

			$("#reportFrom [name='input']").val(input);
			$("#reportFrom ").submit();
			
			ActionService.block();
			downloadfinashed = false;
			//run!!
	        $timeout(timer, 1000);
		}
		
		var downloadfinashed = true;
		 //timer callback
       var timer = function() {
           if(!downloadfinashed){
           	AjaxService.query("getDownloadFlag",{})
   			.success(function(data, status, headers, config) {
   				//console.log(data);
   				if(data['input']){
   				//CommonService.backHomePage();
					window.parent.location.href= "logout";
   				}
   				if(data['error']){
   					//console.log(data['error']);
   					alert(data['error']);
   					downloadfinashed = true;
   				}else{
   					downloadfinashed = data['data'];
   				}
   		    }).error(function(data, status, headers, config) {   
   		    	alert("error");
   		    }).then(function(){

   		    });
           	
           	$timeout(timer, 1000);
           }else{
           	ActionService.unblock();
           }
       }
		
		function mergeSort(array,start,end,key,asc){
			if(start == end)
				return;
			
			var mid = Math.ceil((start+end)/2);
			
			mergeSort(array,start,mid-1,key,asc);
			mergeSort(array,mid,end,key,asc);
			
			var a = 0;
			var b = mid+1;
			
			while(a <= start && b <= end){
				if((asc && array[a][key]>array[b][key]) || (!asc && array[a][key]<array[b][key])){
					var temp = array[a];
					array[a] = array[b];
					array[b] = temp;
					b++;
				}
				a++;
			}
			
			return array;
		}
		
		//排序功能*************
		self.order;
		self.asc;
		
		self.titleClicked = function(head){
			
			if(self.totalData==0||head.value=='isSelect')
				return;

			var status = head.order;
			
			angular.forEach(self.header, function(value, key) {
				value.order = '';
			});
			
			self.nowPage = 1;
			self.datas = [];
			
			if(!status||status==''){
				head.order = 'asc';
				self.order = head.value;
				self.asc = head.order;
				ActionService.block();
				self.queryDatas();
			}else if(status=='asc'){
				head.order = 'desc';
				self.order = head.value;
				self.asc = head.order;
				ActionService.block();
				self.queryDatas();
			}else if(status=='desc'){
				head.order = '';
				self.order = '';
				self.asc = head.order;
				ActionService.block();
				self.queryDatas();
			}
		} ;
		
		
		
	}]);