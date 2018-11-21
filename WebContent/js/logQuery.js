angular.module('MainApp')
	.controller('logCtrl',['Menu','AjaxService','CommonService','ActionService','$filter','$timeout',function(Menu,AjaxService,CommonService,ActionService,$filter,$timeout){
		var self = this;
		//console.log("angular load success!");
		
		self.menuList = Menu.List;
		
		self.header = [
			{name:'Acount',value:'account',align:"left",width:"100px"},
			{name:'Action Type',value:'action',align:"left",width:"150px"},
			{name:'Execute Time',value:'time',align:"left",width:"200px"},
			{name:'Detail',value:'params',align:"left",width:"200px"}
			];
		self.datas = [];
		self.tableDatas = [];

		
		//資料查詢功能*****************
		self.query = function(){
			self.nowPage = 1;
			self.datas = [];
			self.queryCount();
			
			self.isSelectAll = false;
			self.selectedItem = [];
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
			return condition;
		}
		
		self.queryCount = function(){
			ActionService.block();
			var condition = processCondition();
			var input = angular.toJson(condition);
			AjaxService.query("queryLogCount",{input:input})
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
			AjaxService.query("queryLogDatas",{input:input})
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
		
		self.paramList = [];
		self.showParams = function(params){
			self.paramList = [];
			 try {
		            var data = JSON.parse(params);
		            //console.log("data:"+data);
		            var v ='';
		            
		            angular.forEach(data, function(value, key) {
	            		self.paramList.push({key:key,value:value});
						v = v+key+":"+value+"<br>";
					});

		            $("#paramsContent").html(v);
		           /* //console.log("v"+v);
		            //console.log("paramList"+self.paramList);
					self.message = v;*/

		        } catch(e) {
		        	self.paramList.push({key:'',value:params});
		        	self.message = params;
		        }

			$('#myModal').modal('show');
		};
		
		function isJSON(str) {
		    if (typeof str == 'string') {
		        try {
		            JSON.parse(str);
		            return true;
		        } catch(e) {
		            //console.log(e);
		            return false;
		        }
		    }
		    //console.log('It is not a string!')    
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