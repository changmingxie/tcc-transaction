var ioc = {   
	protocol : {
		type : 'com.alibaba.dubbo.config.ProtocolConfig',
		fields : {
			name : 'dubbo',
		    port: '7181',
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
			name : 'order',
			logger: 'log4j'
		}
	},	
	// 系统拦截器配置 结束
	registry : {
			type : 'com.alibaba.dubbo.config.RegistryConfig',
			fields : {
				protocol : 'zookeeper',
				address : 'zookeeper://10.1.1.52:4180',
				file : '../dubbo/dubbo-registry-order.propertie'
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
	}
   
    
    
    
}