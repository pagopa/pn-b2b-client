package it.pagopa.interop.authorization.domain;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Tenant {
    private String name;
    private ExternalId externalId;
    private String selfcareId;
    private Map<String, String> organizationId;
    private Map<String, List<String>> userRoles;
}
