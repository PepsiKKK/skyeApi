package com.yupi.project.service;

//import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;



@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Autowired
    private UserInterfaceInfoService userInterfaceInfoService;
    @Test
    public void invokeCount() {

        //调用userInterfaceInfoService的invokeCount方法，传入两个参数
        boolean b = userInterfaceInfoService.invokeCount(1, 1);
        //
        Assertions.assertTrue(b);
    }
}