package org.mengyun.tcctransaction.sample.http.redpacket.domain.repository;

import org.mengyun.tcctransaction.sample.http.redpacket.domain.entity.RedPacketAccount;
import org.mengyun.tcctransaction.sample.http.redpacket.infrastructure.dao.RedPacketAccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by changming.xie on 4/2/16.
 */
@Repository
public class RedPacketAccountRepository {

    @Autowired
    RedPacketAccountDao redPacketAccountDao;

    public RedPacketAccount findByUserId(long userId) {

        return redPacketAccountDao.findByUserId(userId);
    }

    public void save(RedPacketAccount redPacketAccount) {
        redPacketAccountDao.update(redPacketAccount);
    }
}
