package org.mengyun.tcctransaction.dashboard.controller;

import org.mengyun.tcctransaction.dashboard.dto.ModifyCronDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 14:25
 **/
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/all")
    @ResponseBody
    public ResponseDto all() {
        return taskService.all();
    }

    @RequestMapping("/pause/{domain}")
    @ResponseBody
    public ResponseDto pause(@PathVariable("domain") String domain) {
        return taskService.pause(domain);
    }

    @RequestMapping("/resume/{domain}")
    @ResponseBody
    public ResponseDto resume(@PathVariable("domain") String domain) {
        return taskService.resume(domain);
    }

    @RequestMapping("/delete/{domain}")
    @ResponseBody
    public ResponseDto delete(@PathVariable("domain") String domain) {
        return ResponseDto.returnFail(ResponseCodeEnum.TASK_OPERATE_NOT_SUPPORT);
    }

    @RequestMapping("/modifyCron")
    @ResponseBody
    public ResponseDto modifyCron(@RequestBody ModifyCronDto requestDto) {
        return taskService.modifyCron(requestDto);
    }

}
