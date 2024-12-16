package it.pagopa.interop.authorization.domain;

import lombok.Data;

import java.util.Map;

@Data
public class Tenant {
    private String name;
    private ExternalId externalId;
    private String selfcareId;
    private Map<String, String> organizationId;
    private Map<String, String> userRoles;
}
