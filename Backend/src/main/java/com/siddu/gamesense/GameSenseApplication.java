package com.siddu.gamesense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class GameSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameSenseApplication.class, args);
    }

}
