package com.skye.skyeapigateway;

import com.skye.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableDubbo
@Service
//@DubboComponentScan("com.skye.project.provider")
public class SkyeApiGatewayApplication {

    @DubboReference
    private DemoService demoService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SkyeApiGatewayApplication.class, args);
        SkyeApiGatewayApplication application = context.getBean(SkyeApiGatewayApplication.class);
        String s = application.demoService.sayHello("skye");
        System.out.println(s);
        String s2 = application.demoService.sayHello2("skye2");
        System.out.println(s2);

    }

/*    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("tobaidu", r -> r.path("/baidu")
                        .uri("https://www.baidu.com"))
                .build();
    }*/
}
