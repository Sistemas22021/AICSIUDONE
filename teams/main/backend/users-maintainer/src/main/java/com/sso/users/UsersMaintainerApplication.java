package com.sso.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UsersMaintainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersMaintainerApplication.class, args);
    }
}
