package it.pagopa.pn.interop.cucumber.steps.probing.model;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class ProbingContext{
    public static int ESERVICE_OK_COUNT = 10_000;
    public static int ESERVICE_KO_COUNT = 5_000;
    public static int ESERVICE_RANDOM_COUNT = 5_000;
    public static int ESERVICE_SIZE = ESERVICE_OK_COUNT + ESERVICE_KO_COUNT + ESERVICE_RANDOM_COUNT;

    public static int SCHEDULER_INTERVAL = 3;

    final Integer threadNumber;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private List<SearchEserviceContent> actualResults;
    private EserviceRow actualEserviceRow;
    private EserviceRow expectedEserviceRow;
    private OffsetDateTime lastResponseTime;
    private OffsetTime actualStartTime;
    private OffsetTime actualEndTime;
    private List<TelemetryDataEserviceResponse> actualTelemetry = new ArrayList<>();
    private List<TelemetryDataEserviceResponse> expectedTelemetry = new ArrayList<>();

    public ProbingContext() {
        this.threadNumber = nextEserviceIndex();
    }

    private static int nextEserviceIndex() {
        return COUNTER.updateAndGet(current ->
                current >= ESERVICE_SIZE ? 1 : current + 1
        );
    }
}
