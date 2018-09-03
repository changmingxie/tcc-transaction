var ioc = {   
	protocol : {
		type : 'com.alibaba.dubbo.config.ProtocolConfig',
		fields : {
			name : 'dubbo',
		    port: '8181',
		    payload:"52428800"
		}
	},	
	provider : {
		type : 'com.alibaba.dubbo.config.ProviderConfig',
		fields : {
			proxy : 'tccJdk'
		}
	},	
	provider.application : {
		type : 'com.alibaba.dubbo.config.ApplicationConfig',
		fields : {
			name : 'capital',
			logger: 'log4j'
		}
	},	
	// 系统拦截器配置 结束
	registry : {
			type : 'com.alibaba.dubbo.config.RegistryConfig',
			fields : {
				protocol : 'zookeeper',
				address : 'zookeeper://10.1.1.52:4180',
				file : '../dubbo/dubbo-registry-capital.propertie'
			}
	},
	baseService :{
		type : 'com.alibaba.dubbo.config.ServiceConfig',
		fields : {
			proxy : 'tccJdk',
			application : {
				refer : 'provider.application'
			},
			registry : {
				refer : 'registry'
			},
			protocol : {
				refer : 'protocol'
			},
			provider : {
				refer : 'provider',
			}
		}
	},
    reference :{
		type :'com.alibaba.dubbo.config.ReferenceConfig',
		singleton : false,
		fields :{
		    cluster:"failfast",
		    loadbalance:"leastactive",
		    timeout:38000,		   
			application : {
				refer : 'provider.application'
			},
			registry : {
				refer : 'registry',
			},
			proxy : 'tccJdk'
		}
	},	
	service.capital.CapitalAccountService : {
	   parent : 'baseService',
	   fields : {
	      interfaceName : 'org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalAccountService',
		  // version : '1.0.0',
	      ref : {
	         refer:'capitalAccountService'
		  }
	   }
    },
    service.capital.CapitalTradeOrderService : {
 	   parent : 'baseService',
 	   fields : {
 	      interfaceName : 'org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService',
 		  // version : '1.0.0',
 	      ref : {
 	         refer:'capitalTradeOrderService'
 		  }
 	   }
     }
   
}