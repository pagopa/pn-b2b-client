package it.pagopa.pn.interop.cucumber.steps.purposetemplate.model;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurposeTemplateContext {
    private String actualPurposeTitle;
    private PurposeTemplateState actualState;
    private Boolean actualHandlesPersonalData;
    private Integer actualOffset;
    private Integer actualLimit;
}
