package com.stylefeng.guns.rest;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication(scanBasePackages = {"com.stylefeng.guns"})
@EnableDubboConfiguration
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


