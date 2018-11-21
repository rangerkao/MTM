angular.module('MainApp')
	.controller('searchCtrl',['Menu','AjaxService','CommonService','ActionService','$filter',function(Menu,AjaxService,CommonService,ActionService,$filter){
		var self = this;
		console.log("angular load success!");
		
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
			{name:'ICCID',value:'iccid'},
			{name:'IMSI',value:'imsi'},
			{name:'Day',value:'day'},
			{name:'Network',value:'network'},
			{name:'Volume (Bytes)',value:'volume'},
			{name:'Last Usage Time',value:'lastUsageTime'}
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
			return condition;
		}
		
		self.queryCount = function(){
			ActionService.block();
			var condition = processCondition();
			var input = angular.toJson(condition);
			AjaxService.query("queryUsageCount",{input:input})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data['input']){
					CommonService.backHomePage();
				}
				if(data['error']){
					console.log(data['error']);
				}else{
					//console.log(data['data']);
					self.totalData = data['data'];
					self.total = Math.ceil(self.totalData/self.onePage);
					self.queryDatas();
				}
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    });
		}

		
		self.queryDatas = function(){
			var condition = processCondition();
			//ActionService.block();
			var input = angular.toJson({startPage:self.nowPage,onePage:self.onePage,orderby:'',asc:'',condition});
			AjaxService.query("queryUsageDatas",{input:input})
			.success(function(data, status, headers, config) {
				console.log(data);
				if(data['input']){
					CommonService.backHomePage();
				}
				if(data['error']){
					console.log(data['error']);
				}else{
					self.selectedItem = [];
					self.datas = self.datas.concat(data['data']);
					console.log(self.datas);
					self.dataPage = self.nowPage+6;
				}
				ActionService.unblock();
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    	self.pagging();
		    });
		};
		
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
			if(self.tableDatas.length >0){
				for(var i = self.tableDatas.length ; i < self.onePage; i++ ){
					var d = [];
					for(var j = 0 ; j<self.header.length ; j++ ){
						d.push("{"+self.header[j]+":' '}");
					}
					self.tableDatas.push(d);
				}
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
		
		
		
	}]);