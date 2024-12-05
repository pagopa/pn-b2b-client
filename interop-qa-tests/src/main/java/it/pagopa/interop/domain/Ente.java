package it.pagopa.interop.domain;

import lombok.Data;

import java.util.Map;

@Data
public class Ente {
    private String name;
    private ExternalId externalId;
    private String selfcareId;
    private Map<String, String> organizationId;
    private Map<String, String> userRoles;
}
