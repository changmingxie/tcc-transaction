package org.mengyun.tcctransaction.sample.capital.infrastructure.dao;

import org.mengyun.tcctransaction.sample.capital.domain.entity.CapitalAccount;
import org.mengyun.tcctransaction.sample.dao.BaseDao;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created by changming.xie on 4/2/16.
 */
@IocBean
public class CapitalAccountDao {
	@Inject
	private BaseDao baseDao;

    public CapitalAccount findByUserId(long userId) {
    	CapitalAccount capitalAccount = baseDao.findByCondition(CapitalAccount.class, Cnd.where("USER_ID", "=", userId));
		return capitalAccount;
	}

    public int update(CapitalAccount capitalAccount) {
    	boolean b = baseDao.update(capitalAccount);
		return b == true ? 1 : 0;
	}
}
