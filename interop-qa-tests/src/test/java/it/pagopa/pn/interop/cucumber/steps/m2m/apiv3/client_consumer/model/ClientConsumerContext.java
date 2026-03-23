package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ClientConsumerContext {
    private UUID actualClientId;
    private String actualName;
    private String expectedName;
    private String actualDescription;
    private String expectedDescription;
    private List<UUID> actualMembers;
    private List<UUID> expectedMembers;
}
