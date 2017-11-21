package org.mengyun.tcctransaction.sample.redpacket.infrastructure.dao;

import org.mengyun.tcctransaction.sample.redpacket.domain.entity.RedPacketAccount;

/**
 * Created by changming.xie on 4/2/16.
 */
public interface RedPacketAccountDao {

    RedPacketAccount findByUserId(long userId);

    int update(RedPacketAccount redPacketAccount);
}
