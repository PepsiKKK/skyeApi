package com.skye.skyeApiInterface.controller;

import com.skye.skyeApiClientSdk.model.User;
import com.skye.skyeApiClientSdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {

    //创建三个模拟接口

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/json")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        //通过
        String resule = "(json)POST 用户名字是" + user.getUsername();
        return resule;
    }

}
