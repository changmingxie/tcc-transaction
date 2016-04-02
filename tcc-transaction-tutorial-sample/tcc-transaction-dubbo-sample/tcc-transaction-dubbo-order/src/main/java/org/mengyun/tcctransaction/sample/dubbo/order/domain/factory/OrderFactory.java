package org.mengyun.tcctransaction.sample.dubbo.order.domain.factory;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.dubbo.order.domain.entity.OrderLine;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by changming.xie on 4/1/16.
 */
public class OrderFactory {


    public static Order buildOrder(long payerUserId, long payeeUserId, List<Pair<Long, Integer>> productQuantities) {

        Order order = new Order(payerUserId, payeeUserId);

        for (Pair<Long, Integer> pair : productQuantities) {
            order.addOrderLine(new OrderLine(pair.getLeft(), pair.getRight(), BigDecimal.valueOf(60)));
        }

        return order;
    }
}
