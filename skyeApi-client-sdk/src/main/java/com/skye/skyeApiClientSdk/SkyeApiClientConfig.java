package com.skye.skyeApiClientSdk;

import com.skye.skyeApiClientSdk.client.SkyeApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//将该类标记为一个配置类
@Configuration
//能够读取application.yml中的相关配置
//给配置加上前缀
@ConfigurationProperties("skye.api.client")
@Data
//用于自动扫描组件，使spring自动注册对应的Bean
//因为没有了@SpringBootApplication
@ComponentScan
public class SkyeApiClientConfig {

    private String accessKey;

    private String secretKey;

    //创建一个名为skyeApiClient的bean
    @Bean
    public SkyeApiClient skyeApiClient(){
        //使用ak和sk创建实例
        return new SkyeApiClient(accessKey, secretKey);
    }

}
