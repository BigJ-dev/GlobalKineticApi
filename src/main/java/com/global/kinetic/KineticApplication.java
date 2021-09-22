package com.global.kinetic;

import com.global.kinetic.models.User;
import com.global.kinetic.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class KineticApplication {

    public static void main(String[] args) {
        SpringApplication.run(KineticApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.saveUser(new User(null, "Tshepo", "0712139561", "1994"));
            userService.saveUser(new User(null, "Sophy", "0712139532", "1994"));

        };
    }

}
