package it.pagopa.interop.authorization.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Tenant {
    private String name;
    private ExternalId externalId;
    private String selfcareId;
    private Map<String, String> organizationId;
    private Map<String, List<String>> userRoles;
    private Map<String, String> tenantName;
    private String kind;
}
