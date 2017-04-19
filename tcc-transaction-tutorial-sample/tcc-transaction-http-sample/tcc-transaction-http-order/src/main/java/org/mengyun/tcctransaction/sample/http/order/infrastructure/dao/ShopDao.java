package org.mengyun.tcctransaction.sample.http.order.infrastructure.dao;

import org.mengyun.tcctransaction.sample.http.order.domain.entity.Shop;

/**
 * Created by changming.xie on 4/1/16.
 */
public interface ShopDao {
    Shop findById(long id);
}
