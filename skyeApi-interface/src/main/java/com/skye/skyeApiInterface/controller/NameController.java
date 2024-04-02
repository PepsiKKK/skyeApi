package com.skye.skyeApiInterface.controller;

import com.skye.skyeApiClientSdk.model.User;
import com.skye.skyeApiClientSdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {

    //创建三个模拟接口

    @GetMapping("/")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/json")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        //进行API签名认证
        //从请求头中获取ak和sk
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        //进行判断

        // todo 1、实际情况应该是去数据库中查是否已分配给用户
        if (!accessKey.equals("skye")){
            throw new RuntimeException("无权限");
        }

        // 2、校验随机数，模拟一下，直接判断nonce是否大于10000
        System.out.println(Long.parseLong(nonce));
        if (Long.parseLong(nonce) > 10000L) {
            throw new RuntimeException("无权限");
        }

        // todo 3、时间和当前时间不能超过5分钟 需要修改
/*        if (Long.parseLong(timestamp) < (System.currentTimeMillis() - 5 * 60 * 1000)) {
            throw new RuntimeException("无权限");
        }*/

        // todo 4、判断签名是否正确，实际上是通过ak查找sk
        String serverSign = SignUtils.genSign(body, "1234");
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }
        //通过
        String resule = "POST 用户名字是" + user.getUsername();
        return resule;
    }

}
