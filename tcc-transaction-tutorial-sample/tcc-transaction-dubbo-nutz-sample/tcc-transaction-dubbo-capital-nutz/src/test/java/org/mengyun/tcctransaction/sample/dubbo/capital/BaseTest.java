package org.mengyun.tcctransaction.sample.dubbo.capital;

import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalTradeOrderService;
import org.mengyun.tcctransaction.spring.support.SpringTransactionConfigurator;
import org.mockito.MockitoAnnotations;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
/**
 * 所有测试类的基类
 * @author liangcz
 * @date 2017-05-21
 */
@RunWith(IFSNutzTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
@IocBean
public class BaseTest extends Assert{
	@Inject("refer:$ioc")
    protected Ioc ioc;
	/**
	 * 基类基础测试，保证每个子类至少有一个可用的测试案列
	 */
	@Test
	public void baseTest(){
		Assert.assertEquals(1, 1);
	}
	
	/**
	 * 每个测试方法执行前初始化
	 */
	@Before
	public void _initMethod() throws Exception{
		ServletContext servletContext = new MockServletContext();
		ActionContext ac = new ActionContext();
        ac.setRequest(new MockHttpServletRequest()).setResponse(new MockHttpServletResponse()).setServletContext(servletContext);
        Mvcs.setActionContext(ac);
		MockitoAnnotations.initMocks(this);
		initApp(ioc);
		initMethod();
	}
	/**
	 * 调用原应用初始化方法，初始化相关必要的数据
	 * @author liangcz
	 * @date   2017年6月14日 下午4:22:06
	 * @return void
	 */
	private void initApp(Ioc ioc) {	
		ioc.get(NutQuartzCronJobFactory.class);
		ioc.get(SpringTransactionConfigurator.class);
	}
	/**
	 * 每个测试方法执行后操作
	 */
	@After
	public void _destroyMethod() throws Exception{
		destroyMethod();
	}
	protected void initMethod() throws Exception {}
	protected void destroyMethod() throws Exception {}

}

