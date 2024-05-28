package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.LogInDto;
import com.jpms.codinggame.entity.JwtToken;
import com.jpms.codinggame.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody LogInDto logInDto) throws Exception {
        JwtToken jwtToken = userService.LogIn(logInDto);
        return jwtToken;
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }

}
