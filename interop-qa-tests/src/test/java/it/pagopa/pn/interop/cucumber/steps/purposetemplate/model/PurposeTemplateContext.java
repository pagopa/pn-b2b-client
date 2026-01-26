package it.pagopa.pn.interop.cucumber.steps.purposetemplate.model;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import lombok.Data;

@Data
public class PurposeTemplateContext {
    private String actualPurposeTitle;
    private PurposeTemplateState actualState;
    private Integer actualOffset;
    private Integer actualLimit;
}

