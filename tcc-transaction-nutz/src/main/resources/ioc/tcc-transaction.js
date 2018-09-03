var ioc = {
		transactionManager : {
			type : "org.mengyun.tcctransaction.TransactionManager",
			fields : {
				transactionRepository : {refer: 'transactionRepository'}
			}
		},
		resourceCoordinatorInterceptor:{
			type : 'org.mengyun.tcctransaction.interceptor.ResourceCoordinatorInterceptor',
			fields : {
				transactionManager : {refer: 'transactionManager'}
			}
		},
/*		nutzTransactionConfigurator:{
			type : 'org.mengyun.tcctransaction.nutz.support.NutzTransactionConfigurator',
			events : {
				create : "init"
			}
		},*/
		transactionRecovery:{
			type : 'org.mengyun.tcctransaction.recover.TransactionRecovery',
			fields:{
				transactionConfigurator : {
					refer : 'nutzTransactionConfigurator'
				}
		    }
		},
		tccNutzListen :{
			type : "org.mengyun.tcctransaction.nutz.support.TccNutzListen",
			events : {
				create : "init"
			}
		}

};
