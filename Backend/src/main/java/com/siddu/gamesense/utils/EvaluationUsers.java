package com.siddu.gamesense.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class EvaluationUsers {
    private final CsvReader csvReader;
    private final CsvWriter csvWriter;
    public EvaluationUsers(CsvReader csvReader, CsvWriter csvWriter) {
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
    }

    public void createEvaluationfile() throws IOException {
        List<String> userIds = new ArrayList<>();

        csvReader.read("history.csv", record ->
                userIds.add(record.get("user_id"))
        );

        List<String> uniqueUserIds = userIds.stream()
                .distinct()
                .collect(Collectors.toList());

        Collections.shuffle(uniqueUserIds, new Random(42));

        List<String> evaluationUsers = uniqueUserIds.stream()
                .limit(200)
                .toList();

        csvWriter.writeEvaluationUsers(
                "evaluation_users.csv",
                evaluationUsers
        );

    }
}
