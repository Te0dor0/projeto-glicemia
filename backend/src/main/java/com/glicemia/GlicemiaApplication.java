package com.glicemia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GlicemiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlicemiaApplication.class, args);
    }
}
