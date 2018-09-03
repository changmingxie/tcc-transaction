package org.mengyun.tcctransaction.nutz.support;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Scheduler;

/**
 * tcc nutz 监听
 * @author liangcz
 * @Date   2018年8月17日 下午5:49:54
 * @version 1.0
 */
@IocBean
public class TccNutzListen {
	Logger logger = Logger.getLogger(getClass());
	/**
	 * 初始化tcc nutz 必要资源
	 * @author liangcz
	 * @date   2018年8月17日 下午5:50:22
	 * @return void
	 */
	public void init(){
		try {
			FactoryBuilder.registerBeanFactory(IocBeanUtil.getBean(NutzBeanFactory.class)); // 注册bean 工厂
			 IocBeanUtil.getBean(NutQuartzCronJobFactory.class); // 初始化quartz定时任务
			 IocBeanUtil.getBean(NutzTransactionConfigurator.class); // 初始化配置信息
			 IocBeanUtil.getBean(Scheduler.class); 
		} catch (Exception e) {
			logger.warn("初始化失败", e);
		}
		 
	}
}
