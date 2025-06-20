package com.expis.user.controller;

import com.expis.user.dto.UserDto;
import com.expis.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/userInfo")
    public String getUserInfo(String userId, Model model) {

        System.out.println("UserController userId:" + userId);

        UserDto loginUserDto = userService.getUserInfo(userId);
        model.addAttribute("userName", loginUserDto.getUserName());

        return "user/userInfo";

    }

}
