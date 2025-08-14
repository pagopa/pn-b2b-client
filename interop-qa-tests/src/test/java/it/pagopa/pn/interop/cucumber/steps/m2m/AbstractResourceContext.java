package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@ScenarioScope
public class AbstractResourceContext<RESOURCE> implements ResourceContext<RESOURCE> {
    private RESOURCE originalResource;
    private RESOURCE expectedResource;
    private RESOURCE returnedResource;
}
