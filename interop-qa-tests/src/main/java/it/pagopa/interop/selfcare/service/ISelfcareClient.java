package it.pagopa.interop.selfcare.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ISelfcareClient extends SettableBearerToken {

    ResponseEntity<List<User>> getInstitutionUsers(UUID tenantId, UUID personId, List<String> roles, String query);
}
