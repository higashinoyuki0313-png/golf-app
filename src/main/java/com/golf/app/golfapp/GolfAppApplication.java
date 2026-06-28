package com.golf.app.golfapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.golf.app.golfapp.mapper")
public class GolfAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GolfAppApplication.class, args);
    }
}

