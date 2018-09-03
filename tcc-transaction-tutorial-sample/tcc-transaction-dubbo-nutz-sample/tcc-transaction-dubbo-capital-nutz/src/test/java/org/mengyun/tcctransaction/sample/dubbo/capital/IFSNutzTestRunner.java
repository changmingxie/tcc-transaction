package org.mengyun.tcctransaction.sample.dubbo.capital;

import org.junit.runners.model.InitializationError;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.mock.NutTestRunner;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.ServletValueProxyMaker;
/**
 * nutz junit 测试runner
 * @author liangcz
 * @date 2017-05-22
 */
public class IFSNutzTestRunner  extends NutTestRunner {

	public IFSNutzTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	/**
	 * 创建ioc
	 * @author liangcz
	 * @return Ioc
	 */
	@Override
	protected Ioc createIoc() {
		Ioc ioc = super.createIoc();
		MockServletContext servletContext = new MockServletContext();
		Mvcs.setServletContext(servletContext);
		((Ioc2) ioc).addValueProxyMaker(new ServletValueProxyMaker(servletContext));
		Mvcs.setIoc(ioc);
		return ioc;
	}
	/**
	 * 指定main管理类
	 * @author liangcz
	 * @return Class<?>
	 */
	@Override
	protected Class<?> getMainModule() {
		return MainModuleForJunitTest.class;
	}
	
}
