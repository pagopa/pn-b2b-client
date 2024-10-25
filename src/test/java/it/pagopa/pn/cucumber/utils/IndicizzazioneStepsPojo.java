package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsMassiveUpdateResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsUpdateRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsUpdateResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileCreationResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@Setter
public class IndicizzazioneStepsPojo {

    public IndicizzazioneStepsPojo() {
        this.createdFiles = new ArrayList<>();
    }

    private String sha256;
    private List<FileCreationResponse> createdFiles;
    private AdditionalFileTagsUpdateRequest updateRequest;
    private ResponseEntity<AdditionalFileTagsSearchResponse> additionalFileTagsSearchResponseResponseEntity;
    private ResponseEntity<AdditionalFileTagsUpdateResponse> updateSingleResponseEntity;
    private ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> updateMassiveResponseEntity;
    private HttpClientErrorException httpException;
}
