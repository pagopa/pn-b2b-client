package it.pagopa.pn.interop.cucumber.steps.probing.model;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class ProbingContext{
    private List<SearchEserviceContent> actualResults;
    private Integer actualFrequency;
    private Integer expectedFrequency;
    private OffsetDateTime expectedStartDate;
    private OffsetDateTime expectedEndDate;
}
