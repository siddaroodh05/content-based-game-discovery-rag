package com.siddu.gamesense.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siddu.gamesense.dto.EvalCaseResult;
import com.siddu.gamesense.dto.EvaluatedResult;
import com.siddu.gamesense.dto.RetrievalQuality;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EvalResultReadWriteService {


    private static final Path OUTPUT_PATH = Paths.get("../data/evalResult.jsonl");

    private final ObjectMapper objectMapper;
    private final Object writeLock = new Object();

    public EvalResultReadWriteService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    }


    public void appendResult(EvalCaseResult result) {
        try {
            String jsonLine = objectMapper.writeValueAsString(result);

            synchronized (writeLock) {
                Files.writeString(
                        OUTPUT_PATH,
                        jsonLine + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to append eval result for user ", e);
        }
    }


    public Set<String> loadExistingEvaIds() {

        Set<String> userIds = new HashSet<>();

        if (!Files.exists(OUTPUT_PATH)) {
            return userIds;
        }

        try (var lines = Files.lines(
                OUTPUT_PATH,
                StandardCharsets.UTF_8)) {

            lines.filter(line -> !line.isBlank())
                    .forEach(line -> {
                        try {
                            JsonNode jsonNode =
                                    objectMapper.readTree(line);

                            JsonNode evalNode =
                                    jsonNode.get("userId");

                            if (evalNode != null && !evalNode.isNull()) {
                                userIds.add(evalNode.asText());
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(
                                    "Failed to read evaluation result line",
                                    e
                            );
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load existing evaluation IDs",
                    e
            );
        }

        return userIds;
    }

    public List<EvaluatedResult> loadEvaluatedResults() {

       List<EvaluatedResult> evaluatedResults = new ArrayList<>();

        if (!Files.exists(OUTPUT_PATH)) {
            return evaluatedResults;
        }

        try (var lines = Files.lines(
                OUTPUT_PATH,
                StandardCharsets.UTF_8)) {

            lines.filter(line -> !line.isBlank())
                    .forEach(line -> {
                        try {
                            JsonNode jsonNode =
                                    objectMapper.readTree(line);

                            RetrievalQuality topRatedRetrieval =
                                    objectMapper.treeToValue(
                                            jsonNode.get("TopRatedResult"),
                                            RetrievalQuality.class
                                    );

                            RetrievalQuality recentBasedRetrieval =
                                    objectMapper.treeToValue(
                                            jsonNode.get("RecentRatedResult"),
                                            RetrievalQuality.class
                                    );

                            evaluatedResults.add(
                                    new EvaluatedResult(
                                            topRatedRetrieval,
                                            recentBasedRetrieval
                                    )
                            );

                        } catch (IOException e) {
                            throw new RuntimeException(
                                    "Failed to read evaluation result line",
                                    e
                            );
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load existing evaluation results",
                    e
            );
        }

        return evaluatedResults;
    }


}