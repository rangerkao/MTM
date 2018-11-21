angular.module('MainApp')
	.controller('searchCtrl',['Menu','AjaxService','CommonService','ActionService','$filter','$timeout',function(Menu,AjaxService,CommonService,ActionService,$filter,$timeout){
		var self = this;
		//console.log("angular load success!");
		
		self.menuList = Menu.List;
		
		self.condition = ['ICCID', 'IMSI'];
		self.status = [
			{label:'normal',value:0},
			{label:'suspended',value:3},
			{label:'terminated',value:4},
			{label:'waitting',value:10}
			]
		
		self.nowPage = 1;
		self.dataPage = 0;
		self.total = 0;
		self.totalData = 0
		self.onePage = 50;		
		//ICCID, IMSI, Day, Network, Volume (Bytes), Last Usage Time
		self.header = [
			{name:'ICCID',value:'iccid',align:"left",width:"190px"},
			{name:'IMSI',value:'imsi',align:"left",width:"150px"},
			{name:'Day',value:'day',align:"left",width:"90px"},
			{name:'Network',value:'network',align:"left",width:"170px"},
			{name:'Volume (Bytes)',value:'volume',align:"right",width:"130px"},
			{name:'Last Usage Time',value:'lastUsageTime',align:"center",width:"170px"},
			{name:'Amount',value:'amount',align:"right",width:"100px"},
			];
		self.datas = [];
		self.tableDatas = [];
		/*for(var i = 0;i<56;i++){
			self.datas.push({ICCID:'data'+i,IMSI:'data'+i,Volume:'data'+i,Action:'data'+i,Button:'data'+i});
		}*/
		
		self.query = function(){
			self.nowPage = 1;
			self.datas = [];
			self.queryCount();
		}
		
		
		
		function processCondition(){
			var condition = [];
			if(self.conditionSelected == 'ICCID'){
				condition.push({ICCID:self.v});
			}else if(self.conditionSelected == 'Status'){
				condition.push({status:self.statusSelected});
			}else if(self.conditionSelected == 'IMSI'){
				condition.push({IMSI:self.v});
			}
			if(self.start){
				condition.push({start:self.start});
			}
			if(self.end){
				condition.push({end:self.end});
			}
			
			if(self.selectedUser){
				condition.push({subsidiaryid:self.selectedUser});
			}
			
			return condition;
		}
		
		self.queryCount = function(){
			ActionService.block();
			var condition = processCondition();
			var input = angular.toJson(condition);
			AjaxService.query("queryUsageCount",{input:input})
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
			AjaxService.query("queryUsageDatas",{input:input})
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
					self.selectedItem = [];
					self.datas = self.datas.concat(data['data']);
					//console.log(self.datas);
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
		
		
		self.rePagging = function(){
			self.query();
		} 
		
		self.prePage = function(){
			if(self.nowPage>1){
				self.nowPage = self.nowPage -1;
				self.pagging();
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
		
		self.order;
		self.asc;
		
		self.titleClicked = function(head){
			
			if(self.totalData==0||head.value=='select')
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