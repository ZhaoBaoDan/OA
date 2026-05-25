package com.smartauto.oa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.smartauto.oa.*.mapper")
@EnableAsync
@EnableScheduling
public class SmartAutoOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartAutoOaApplication.class, args);
    }
}
