package com.homework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.homework.mapper")
public class HwBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(HwBlogApplication.class, args);
    }

}
