package com.siddu.gamesense.utils;

import com.siddu.gamesense.dto.GameMetadataCsvRow;
import com.siddu.gamesense.dto.UserGameCsvRow;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;



@Component
public class AutoMapper {

    public GameMetadataCsvRow mapper(CSVRecord record) {

        return new GameMetadataCsvRow(
                record.get("parent_asin"),
                record.get("title"),
                record.get("categories"),
                record.get("features"),
                record.get("description"),
                Double.valueOf(record.get("average_rating")),
                Integer.valueOf(record.get("rating_number")),
                record.get("thumb")
        );
    }

    public UserGameCsvRow historymapper(CSVRecord record) {
        return new UserGameCsvRow(
                record.get("user_id"),
                record.get("parent_asin"),
                Double.parseDouble(record.get("rating")),
                record.get("review_text"),
                Long.parseLong(record.get("timestamp"))
        );
    }

    public String EvaluationMapper(CSVRecord record)  {
                   return record.get("user_id");

    }


}
