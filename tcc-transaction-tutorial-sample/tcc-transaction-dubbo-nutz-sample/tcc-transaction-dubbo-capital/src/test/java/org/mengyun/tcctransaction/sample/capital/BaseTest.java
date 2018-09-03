package org.mengyun.tcctransaction.sample.capital;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * 测试-基类
 * @author liangcz
 * @Date   2017年11月18日 下午2:16:20
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:config/spring/local/appcontext-*.xml","classpath:tcc-transaction.xml","classpath:tcc-transaction-dubbo.xml"} ,loader = GenericXmlContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class BaseTest extends Assert{
	/**
	 * 基类基础测试，保证每个子类至少有一个可用的测试案列
	 */
	@Test
	public void baseTest(){
		Assert.assertEquals(1, 1);
	}
	/**
	 * 每个测试方法执行前初始化
	 * @throws Exception 
	 */
	@Before
	public void _initMethod() throws Exception{
		MockitoAnnotations.initMocks(this);
		initMethod();
	}
	/**
	 * 每个测试方法执行后操作
	 * @throws Exception 
	 */
	@After
	public void _destroyMethod() throws Exception{
		destroyMethod();
	}
	protected void initMethod() throws Exception {}
	protected void destroyMethod() throws Exception {}
}
