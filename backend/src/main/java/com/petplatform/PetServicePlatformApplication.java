package com.petplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.petplatform.mapper")
@SpringBootApplication
public class PetServicePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetServicePlatformApplication.class, args);
    }
}
