package com.siddu.gamesense.services;

import com.siddu.gamesense.dto.GameMetadataCsvRow;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class embeddingService {


    private final EmbeddingModel embeddingModel;

    public embeddingService(
            @Qualifier("ollamaEmbeddingModel")
            EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public  float[] embed(GameMetadataCsvRow row){
        String document = """
        Title: %s
        Categories: %s
        Features: %s
        """.formatted(
                row.title(),
                row.categories(),
                row.features()
        );

        return embeddingModel.embed(document);
    }

    public float[] queryembed(String query){
        return embeddingModel.embed(query);
    }


}
