package com.mediaAPI;

import com.mediaAPI.auth.AuthenticationService;
import com.mediaAPI.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.mediaAPI.user.Role.ADMIN;
import static com.mediaAPI.user.Role.MANAGER;

@SpringBootApplication
public class MediaAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaAPIApplication.class, args);
    }
}

