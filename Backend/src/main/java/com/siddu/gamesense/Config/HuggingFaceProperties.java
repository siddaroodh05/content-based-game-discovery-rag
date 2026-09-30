package com.siddu.gamesense.Config;


import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.huggingface")
@Data
public class HuggingFaceProperties {

    private String apiKey;
    private String baseUrl;

    private Chat chat = new Chat();

    @Data
    public static class Chat {
        private String model;
        private double temperature;
        private int maxTokens;
        private boolean enableThinking;
    }
}