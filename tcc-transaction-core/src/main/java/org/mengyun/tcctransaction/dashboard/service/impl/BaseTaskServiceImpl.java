package org.mengyun.tcctransaction.dashboard.service.impl;

import org.mengyun.tcctransaction.dashboard.dto.ModifyCronDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.dto.TaskDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.exception.TransactionException;
import org.mengyun.tcctransaction.dashboard.service.TaskService;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 17:03
 */
public class BaseTaskServiceImpl implements TaskService {

    @Override
    public ResponseDto<List<TaskDto>> all() {
        throw new TransactionException(ResponseCodeEnum.TASK_OPERATE_NOT_SUPPORT);
    }

    @Override
    public ResponseDto<Void> pause(String domain) {
        return executeTaskOperation(domain);
    }

    @Override
    public ResponseDto<Void> resume(String domain) {
        return executeTaskOperation(domain);
    }

    @Override
    public ResponseDto<Void> modifyCron(ModifyCronDto requestDto) {
        throw new TransactionException(ResponseCodeEnum.TASK_OPERATE_NOT_SUPPORT);
    }

    @Override
    public ResponseDto<Void> delete(String domain) {
        return executeTaskOperation(domain);
    }

    private ResponseDto<Void> executeTaskOperation(String domain) {
        throw new TransactionException(ResponseCodeEnum.TASK_OPERATE_NOT_SUPPORT);
    }
}
