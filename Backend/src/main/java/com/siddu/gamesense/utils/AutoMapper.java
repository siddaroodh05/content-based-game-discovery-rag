package com.siddu.gamesense.utils;

import com.siddu.gamesense.dto.EvalutionInputdto;
import com.siddu.gamesense.dto.GameMetadataCsvRow;
import com.siddu.gamesense.dto.UserGameCsvRow;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;



@Component
public class Csvdtomapper {

    public GameMetadataCsvRow mapper(CSVRecord record) {

        return new GameMetadataCsvRow(
                record.get("parent_asin"),
                record.get("title"),
                record.get("categories"),
                record.get("features"),
                record.get("description"),
                Double.valueOf(record.get("average_rating")),
                Integer.valueOf(record.get("rating_number"))
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

    public EvalutionInputdto EvaluationMapper(CSVRecord record)  {

            return new EvalutionInputdto(
                    record.get("user_id"),
                    record.get("parent_asin"),
                    Double.parseDouble(record.get("rating")),
                    Long.parseLong(record.get("timestamp"))

            );

    }


}
