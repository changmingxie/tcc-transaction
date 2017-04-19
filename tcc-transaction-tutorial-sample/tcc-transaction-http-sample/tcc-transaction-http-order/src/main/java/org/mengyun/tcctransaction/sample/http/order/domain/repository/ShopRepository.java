package org.mengyun.tcctransaction.sample.http.order.domain.repository;

import org.mengyun.tcctransaction.sample.http.order.domain.entity.Shop;
import org.mengyun.tcctransaction.sample.http.order.infrastructure.dao.ShopDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by changming.xie on 4/1/16.
 */
@Repository
public class ShopRepository {

    @Autowired
    ShopDao shopDao;

    public Shop findById(long id) {

        return shopDao.findById(id);
    }
}
