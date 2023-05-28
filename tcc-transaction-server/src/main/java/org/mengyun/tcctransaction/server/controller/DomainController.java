package org.mengyun.tcctransaction.server.controller;

import org.mengyun.tcctransaction.dashboard.dto.DomainStoreDto;
import org.mengyun.tcctransaction.dashboard.dto.DomainStoreRequestDto;
import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.server.service.DomainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/23 14:53
 */
@RestController
@RequestMapping("domain")
public class DomainController {

    @Autowired
    private DomainServiceImpl domainService;

    @RequestMapping("/allDomainKeys")
    @ResponseBody
    public ResponseDto<List<String>> allDomainKeys() {
        return domainService.getAllDomainKeys();
    }

    @RequestMapping("/all")
    @ResponseBody
    public ResponseDto<List<DomainStoreDto>> all() {
        return domainService.getAllDomains();
    }

    @RequestMapping("/create")
    @ResponseBody
    public ResponseDto<Void> create(@RequestBody DomainStoreRequestDto requestDto) {
        return domainService.create(requestDto);
    }

    @RequestMapping("/modify")
    @ResponseBody
    public ResponseDto<Void> modify(@RequestBody DomainStoreRequestDto requestDto) {
        return domainService.modify(requestDto);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseDto<Void> delete(@RequestBody DomainStoreRequestDto requestDto) {
        return domainService.delete(requestDto);
    }
}
