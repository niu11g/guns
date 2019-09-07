package com.stylefeng.guns.rest;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
//import org.springframework.cloud.netflix.hystrix.EnableHystrix;
//import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


@SpringBootApplication(scanBasePackages = {"com.stylefeng.guns"})
@EnableDubboConfiguration
//@EnableHystrixDashboard
//@EnableCircuitBreaker
//@EnableHystrix
public class GateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
//        String str = "成都市(成华区)(武侯区)(高新区)";
//        Pattern p = Pattern.compile(".*?(?=\\()");
//        Matcher m = p.matcher(str);
//        if (m.find()) {
//            System.out.println(m.group());
//        }
    }

}


