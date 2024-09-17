package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LegalPersonAuthExpectedResponsePojo {
    BffPublicKeyResponse response;
    String status;
}
