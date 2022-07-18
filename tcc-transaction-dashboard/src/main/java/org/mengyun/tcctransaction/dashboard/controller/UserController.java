package org.mengyun.tcctransaction.dashboard.controller;

import org.mengyun.tcctransaction.dashboard.dto.ResponseDto;
import org.mengyun.tcctransaction.dashboard.model.LoginDto;
import org.mengyun.tcctransaction.dashboard.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/6 11:22
 **/
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${spring.tcc.dashboard.connection-mode:embedded}")
    private String connectionMode;

    @PostMapping("/login")
    @ResponseBody
    public ResponseDto login(@RequestBody LoginDto request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        Map<String,Object> loginedResult = new HashMap<>();
        loginedResult.put("token", token);
        loginedResult.put("username", request.getUsername());
        loginedResult.put("connectionMode", connectionMode);
        return ResponseDto.returnSuccess(loginedResult);
    }
}
