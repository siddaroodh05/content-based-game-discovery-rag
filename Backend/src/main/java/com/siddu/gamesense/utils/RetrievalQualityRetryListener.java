package com.siddu.gamesense.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component("retrievalQualityRetryListener")
public class RetrievalQualityRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> void onError(
            RetryContext context,
            RetryCallback<T, E> callback,
            Throwable throwable) {

        int attempt = context.getRetryCount();

        if (throwable.getMessage() != null &&
                throwable.getMessage().contains("429")) {

            log.warn(
                    "LLM returned 429. Waiting 20 seconds before attempt {}",
                    attempt + 2
            );
        } else {
            log.warn(
                    "LLM request failed on attempt {}: {}",
                    attempt + 1,
                    throwable.getMessage()
            );
        }
    }
}