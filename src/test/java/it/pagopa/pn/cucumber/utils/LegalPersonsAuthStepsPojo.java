package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.RestClientException;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Builder
public class LegalPersonsAuthStepsPojo {

    private List<BffPublicKeyResponse> publicKeysResponses;
    private List<BffPublicKeyResponse> publicKeysSearchResult;
    private RestClientException exception;

    LegalPersonsAuthStepsPojo() {
        this.publicKeysResponses = new LinkedList<>();
        this.publicKeysSearchResult = new LinkedList<>();
    }
}
