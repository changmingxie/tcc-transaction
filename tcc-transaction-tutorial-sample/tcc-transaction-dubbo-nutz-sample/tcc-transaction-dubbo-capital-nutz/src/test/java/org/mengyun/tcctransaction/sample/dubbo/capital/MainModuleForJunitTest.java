package org.mengyun.tcctransaction.sample.dubbo.capital;

import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Ok("json")
@Fail("json")
@IocBy(type=ComboIocProvider.class,args = {"*js", "ioc/",
	"*annotation","org.mengyun",
       "*org.nutz.integration.quartz.QuartzIocLoader"})
@Modules(scanPackage = true) // ,packages = { "org.mengyun.tcctransaction.sample" }
@Encoding(input = "UTF-8", output = "UTF-8")
public class MainModuleForJunitTest {
	
}
