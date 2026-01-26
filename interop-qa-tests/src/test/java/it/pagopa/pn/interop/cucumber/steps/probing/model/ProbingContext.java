package it.pagopa.pn.interop.cucumber.steps.probing.model;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class ProbingContext{
    static int ESERVICE_SIZE = 20;
    final Integer threadNumber;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private List<SearchEserviceContent> actualResults;
    private EserviceRow actualEserviceRow;
    private EserviceRow expectedEserviceRow;
    private LocalDateTime lastResponseTime;

    public ProbingContext() {
        this.threadNumber = nextEserviceIndex();
    }

    private static int nextEserviceIndex() {
        return COUNTER.updateAndGet(current ->
                current >= ESERVICE_SIZE ? 1 : current + 1
        );
    }
}
