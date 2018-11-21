angular.module('MainApp',[])
	.controller('mainController',['Menu','AjaxService','CommonService',function(Menu,AjaxService,CommonService){
		var self = this;
		self.menuList = Menu.List;
		
		self.redirect = function(link){
			window.location.href = link;
		}
		
		self.querySessionStatus = function(){
			AjaxService.query("getSessionStatus",{})
			.success(function(data, status, headers, config) {
				if(data['input']){
					CommonService.backHomePage();
				}
		    }).error(function(data, status, headers, config) {   
		    	console.log("error");
		    }).then(function(){
		    });
		}
		
		self.querySessionStatus();
	}])
	.factory('Menu', function() {
		console.log("Get menu");
	  return {
	      List : [
	    	  {title:'Account Management',subTitle:'(帳戶管理)',detail:'detail 1',link:'adminLink',page:'web/admin.jsp',pic:'pic/admin_icon.jpg'},
	    	  {title:'Status Inquiry',subTitle:'(狀態查詢)',detail:'detail 2',link:'searchLink',page:'web/search.jsp',pic:'pic/devices_icon.jpg'},
	    	  {title:'Usage Inquiry',subTitle:'(用量查詢)',detail:'detail 3',link:'search2Link',page:'web/search2.jsp',pic:'pic/data_icon.jpg'},
	    	  {title:'Log Inquiry',subTitle:'(用量查詢)',detail:'detail 4',link:'logQueryLink',page:'web/logQuery.jsp',pic:'pic/Log_icon.jpg'},
	    	  //{title:'功能四',subTitle:' ',detail:'detail 4',link:''},
	      ]
	  };
	})
	.factory('CommonService',[function(){
		return {
			backHomePage: function(){
				window.location.href= "logout";
			}
		}
	}])
	.factory('AjaxService',['$http',function($http){
		var datas={data:'test'};
		return {
			query:function(url,params){
				return $http({
					method:'POST',
					url:url,
					data:params,
				    cache: false,
					headers:{
						'Content-Type':'application/x-www-form-urlencoded',
						'Accept': 'application/json'},
					transformRequest:function(data,headers){
						var requestStr=null;
						for(var key in data){
							if(requestStr){
								requestStr += '&' + key + '=' +data[key];
							}else{
								requestStr = key + '=' + data[key];
							}
						}
						return requestStr;
					},
					transformResponse:function(data,headersGetter){
						//console.log(data);
						//轉兩次才會成為Object
						try {
					        JSON.parse(data);
					    } catch (e) {
					        return data;
					    }
						var temp = angular.fromJson(data);
						var result = angular.fromJson(temp);
						return result;
					}
				
				});  
			}
		};
		$httpProvider.defaults.headers.post['Content-Type']='application/x-www-form-urlencoded';
	}]).factory('ActionService',[function(){
		return {
			block:function(){
				$.blockUI({ css: { 
		            border: 'none', 
		            padding: '15px', 
		            backgroundColor: '#000', 
		            '-webkit-border-radius': '10px', 
		            '-moz-border-radius': '10px', 
		            opacity: .5, 
		            color: '#fff' 
		        } });
			},
			unblock:function(){
				$.unblockUI();
			}
		};
	}]);





/*function block(){
	$.blockUI({ css: { 
	    border: 'none', 
	    padding: '15px', 
	    backgroundColor: '#000', 
	    '-webkit-border-radius': '10px', 
	    '-moz-border-radius': '10px', 
	    opacity: .5, 
	    color: '#fff' 
	} });
}
function unblock(){
	$.unblockUI();
}
function test(){
	console.log("test");
}*/
