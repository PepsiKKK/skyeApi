package com.skye.skyeApiInterface;

import cn.hutool.json.JSONUtil;
import com.plexpt.chatgpt.util.Proxys;
import com.skye.skyeApiClientSdk.client.SkyeApiClient;
import com.skye.skyeApiClientSdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Proxy;

@SpringBootTest
class SkyeApiInterfaceApplicationTests {

    //自动注入一个skyeApiClient的Bean
    @Autowired
    private SkyeApiClient skyeApiClient;

    @Test
    public void testGenJsonString(){
        User user = User.builder()
                .username("skye").build();
        String s = JSONUtil.toJsonStr(user);
        System.out.println(s);
    }

    @Test
    public void testSign(){
        //1、测试方法1
        String res1 = skyeApiClient.getNameByGet("skye1");

        User user = User.builder()
                .username("skye2").build();
        String res2 = skyeApiClient.getUserNameByPost(user);

        System.out.println("res1 = " + res1);
        System.out.println("res2 = " + res2);
    }

}
