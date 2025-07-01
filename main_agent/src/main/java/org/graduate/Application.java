package org.graduate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan(basePackages = "org.graduate.controller")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
}
