package org.mengyun.tcctransaction.sample.capital.infrastructure.dao;


import org.mengyun.tcctransaction.sample.capital.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.dao.BaseDao;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class TradeOrderDao {
	@Inject
	private BaseDao baseDao;
	@Inject
	protected Dao dao;
	
    public int insert(TradeOrder tradeOrder) {
    	TradeOrder t = baseDao.save(tradeOrder);
    	return t == null ? 0 : 1;
	}

    public int update(TradeOrder tradeOrder) {
    	int i = dao.updateWithVersion(tradeOrder);
		return i;
	}

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo) {
    	TradeOrder tradeOrder = baseDao.findByCondition(TradeOrder.class, Cnd.where("MERCHANT_ORDER_NO", "=", merchantOrderNo));
		return tradeOrder;
	}
}
