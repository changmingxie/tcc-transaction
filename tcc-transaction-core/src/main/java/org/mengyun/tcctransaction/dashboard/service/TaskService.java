package org.mengyun.tcctransaction.dashboard.service;

import org.mengyun.tcctransaction.dashboard.dto.ModifyCronDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.dto.TaskDto;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 10:08
 */
public interface TaskService {

    /**
     * 所有任务
     *
     * @return
     */
    ResponseDto<List<TaskDto>> all();

    /**
     * 暂停任务
     *
     * @param domain
     * @return
     */
    ResponseDto<Void> pause(String domain);

    /**
     * 恢复任务
     *
     * @param domain
     * @return
     */
    ResponseDto<Void> resume(String domain);

    /**
     * 修改任务cron表达式
     *
     * @param requestDto
     * @return
     */
    ResponseDto<Void> modifyCron(ModifyCronDto requestDto);

    /**
     * 删除任务
     *
     * @param domain
     * @return
     */
    ResponseDto<Void> delete(String domain);
}
