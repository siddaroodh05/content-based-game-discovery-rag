package com.siddu.gamesense.dto;

import java.util.Map;

public record EvalCaseResult(
        String userId,
        ChannelResult RecentRatedResult,
        ChannelResult TopRatedResult,
        int overallGoodRetrieval,
        int overallBadRetrieval
) {
}
