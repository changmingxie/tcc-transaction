package org.mengyun.tcctransaction.sample.dubbo.order;

//import io.swagger.nutz.listing.ApiDeclarationNutz;

import org.mengyun.tcctransaction.nutz.support.TccNutzListen;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.quartz.Scheduler;

import com.alibaba.dubbo.config.ServiceConfig;

public class IFSSetup implements Setup {

	public void destroy(NutConfig config) {

	}
	@SuppressWarnings("rawtypes")
	public void init(NutConfig config) {
		
		final Ioc ioc = config.getIoc();
		// ioc.get(SpringTransactionConfigurator.class, "transactionConfigurator")
		// ioc.get(SpringTransactionConfigurator.class)
		// ioc.get(NutQuartzCronJobFactory.class);
		// ioc.get(null, "placeOrderService")
		// ioc.get(TccNutzListen.class).setup(); // 初始化tcc transaction 启动资源
		ioc.get(TccNutzListen.class); // 初始化nutz资源
		// System.out.println(ioc.get(Scheduler.class));
		Thread dubboServerExportThread = new Thread(new Runnable() {
			public void run() {
				String[] names = ioc.getNames();
				for (String name : names) {
					if (name.startsWith("service.")) {
						try {
							
							ServiceConfig service = ioc.get(null, name);
							service.export();
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(0);
						}
					}
				}
			}
		},"dubboServerExportThread");
		dubboServerExportThread.start();

	}

}
