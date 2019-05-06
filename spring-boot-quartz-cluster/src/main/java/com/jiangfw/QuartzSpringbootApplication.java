package com.jiangfw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @Author: jiangfw
 * @Date: 2019-04-30
 */
@ImportResource(value = {"classpath:applicationContext.xml"})
@SpringBootApplication
public class QuartzSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzSpringbootApplication.class, args);
    }
}