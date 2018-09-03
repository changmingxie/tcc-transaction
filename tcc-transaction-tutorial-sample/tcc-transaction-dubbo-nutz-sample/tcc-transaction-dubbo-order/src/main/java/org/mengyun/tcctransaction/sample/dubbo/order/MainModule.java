package org.mengyun.tcctransaction.sample.dubbo.order;

import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.view.freemarker.FreemarkerViewMaker;

@Ok("json")
@Fail("json")
@IocBy(type=ComboIocProvider.class,args = {"*js", "ioc/",
	"*annotation","org.mengyun",
       "*org.nutz.integration.quartz.QuartzIocLoader"})
@Modules(scanPackage = true) // ,packages = { "org.mengyun.tcctransaction.sample" }
@Encoding(input = "UTF-8", output = "UTF-8")
@Views(value={FreemarkerViewMaker.class})
@SetupBy(IFSSetup.class)
public class MainModule {

}
