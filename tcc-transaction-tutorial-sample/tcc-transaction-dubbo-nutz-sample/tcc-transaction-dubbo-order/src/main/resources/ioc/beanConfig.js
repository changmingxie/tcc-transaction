var ioc = {
    
    //系统拦截器申明 开始
	txNONE : {
		type : 'org.nutz.aop.interceptor.TransactionInterceptor',
		args : [ 0 ]
	},
	txREAD_UNCOMMITTED : {
		type : 'org.nutz.aop.interceptor.TransactionInterceptor',
		args : [ 1 ]
	},
	txREAD_COMMITTED : {
		type : 'org.nutz.aop.interceptor.TransactionInterceptor',
		args : [ 2 ]
	},
	txREPEATABLE_READ : {
		type : 'org.nutz.aop.interceptor.TransactionInterceptor',
		args : [ 4 ]
	},
	txSERIALIZABLE : {
		type : 'org.nutz.aop.interceptor.TransactionInterceptor',
		args : [ 8 ]
	},
	//系统拦截器申明 结束
	
	// 数据库配置  开始
	dao : {
			type : "org.nutz.dao.impl.NutDao",
			args : [{refer:"dataSource"}]
		},
		dataSource : {
			type : "com.alibaba.druid.pool.DruidDataSource",
			events : {
				depose : "close"
			},
				fields : {
				driverClassName : {java : '$conf.get("db_driver")'},
				url : {java : '$conf.get("db_url")'},
				username : {java : '$conf.get("db_user")'},
				password : {java : '$conf.get("db_passwd")'},
				filters : "stat",
				initialSize : 30,
                maxActive : 150,
                minIdle : 30
				
			}
     },
	$aop : {
		type : 'org.nutz.ioc.aop.config.impl.ComboAopConfigration',
		fields : {
			aopConfigrations : [
					{
						type : 'org.nutz.ioc.aop.config.impl.AnnotationAopConfigration'
					}, 
					{
						type : 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
						fields : {
							itemList : [

					 				/*	['org\\.mengyun\\.tcctransaction\\.sample\\.dubbo\\.order\\.biz\\..+','.+','ioc:txREAD_COMMITTED'],*/
					 					['org\\.mengyun\\.tcctransaction\\.sample\\.order\\.domain\\.service\\..+','.+','ioc:txREAD_COMMITTED']
					 					]
						}
					}
					
			]
		}
	},
}