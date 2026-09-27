package com.siddu.gamesense.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class CsvWriter {

    @Value("${data.path}")
    private String dataPath;

    public void writeEvaluationUsers(
            String fileName,
            List<String> userIds
    ) throws IOException {

        Path path = Paths.get(dataPath, fileName);

        try (
                Writer writer = Files.newBufferedWriter(
                        path,
                        StandardCharsets.UTF_8
                );

                CSVPrinter printer = CSVFormat.DEFAULT.builder()
                        .setHeader("user_id")
                        .build()
                        .print(writer)
        ) {
            for (String userId : userIds) {
                printer.printRecord(userId);
            }
        }
    }
}