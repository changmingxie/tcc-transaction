var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		},
		jedisPoolConfig : {
			type : "redis.clients.jedis.JedisPoolConfig",
			fields : {
				maxTotal : "1000",
				maxWaitMillis : "1000",
			}
		},
		jedisPool : {
			type : "redis.clients.jedis.JedisPool",
			args : [
			        {refer: 'jedisPoolConfig'},"127.0.0.1","6379","1000"
			]
		},
		transactionRepository : {
			type : "org.mengyun.tcctransaction.repository.RedisTransactionRepository",
			fields : {
				keyPrefix : "TCC:CAP:",
				jedisPool : {refer: 'jedisPool'}
			}
		},
		recoverConfig : {
			type : "org.mengyun.tcctransaction.nutz.recover.DefaultRecoverConfig",
			fields : {
				maxRetryCount : "30",
				recoverDuration : "60",
				/*recoverDuration : "0/30 * * * * ?",*/
				delayCancelExceptions : [
				                         'com.alibaba.dubbo.remoting.TimeoutException',
				                         'org.mengyun.tcctransaction.OptimisticLockException',
				                         'java.net.SocketTimeoutException'
				                         ]
			}
		},
		
		
		
		
		
		
		

};
