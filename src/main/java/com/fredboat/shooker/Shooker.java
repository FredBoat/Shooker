package com.fredboat.shooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Shooker {

    public static void main(String[] args) {
        SpringApplication.run(Shooker.class, args);
    }
}
