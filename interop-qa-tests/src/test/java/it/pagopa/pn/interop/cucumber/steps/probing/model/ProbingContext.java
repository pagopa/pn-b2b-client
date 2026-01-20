package it.pagopa.pn.interop.cucumber.steps.probing.model;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProbingContext{
    List<SearchEserviceContent> actual;
}
