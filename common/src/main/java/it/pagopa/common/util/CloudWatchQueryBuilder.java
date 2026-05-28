package it.pagopa.common.util;

import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;

public class CloudWatchQueryBuilder {

    public static FilterLogEventsRequest search(String microservice, String query, long minutes) {
        long startTime = System.currentTimeMillis() - (minutes * 60L * 1000L);
        return FilterLogEventsRequest.builder()
                .logGroupName(microservice)
                .filterPattern(query)
                .startTime(startTime)
                .build();
    }
}
