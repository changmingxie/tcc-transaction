package org.mengyun.tcctransaction.dashboard.service.impl.local;

import org.mengyun.tcctransaction.dashboard.service.condition.LocalStorageCondition;
import org.mengyun.tcctransaction.dashboard.service.impl.BaseTaskServiceImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 17:03
 **/
@Conditional(LocalStorageCondition.class)
@Service
public class LocalTaskServiceImpl extends BaseTaskServiceImpl {

}
