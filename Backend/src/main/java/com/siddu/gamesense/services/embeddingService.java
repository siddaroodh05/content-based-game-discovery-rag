package com.siddu.gamesense.services;


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class embeddingService {

    private static final String QUERY_INSTRUCTION =
            "Instruct: Given a game search query, retrieve relevant game titles and descriptions\nQuery: ";

    private final EmbeddingModel embeddingModel;

    public embeddingService(
            @Qualifier("ollamaEmbeddingModel")
            EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public  float[] embed(String text){

        return embeddingModel.embed(text);
    }


}
