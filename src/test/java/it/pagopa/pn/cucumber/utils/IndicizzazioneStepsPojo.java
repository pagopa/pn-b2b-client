package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class IndicizzazioneStepsPojo {

    public IndicizzazioneStepsPojo() {
        this.createdFiles = new LinkedList<>();
        this.fileKeyInesistenti = new LinkedList<>();
    }

    private String sha256;
    private List<FileCreationResponse> createdFiles;
    private List<String> fileKeyInesistenti;
    private AdditionalFileTagsUpdateRequest updateRequest;
    private ResponseEntity<AdditionalFileTagsSearchResponse> additionalFileTagsSearchResponseResponseEntity;
    private ResponseEntity<AdditionalFileTagsUpdateResponse> updateSingleResponseEntity;
    private ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> updateMassiveResponseEntity;
    private HttpClientErrorException httpException;
    //Limiti test
    private int maxTagsPerRequest;
    private int maxOperationsOnTagsPerRequest;
    private int maxFileKeys;
    private int maxMapValuesForSearch;
    private int maxFileKeysUpdateMassivePerRequest;
    private int maxTagsPerDocument;
    private int maxValuesPerTagDocument;
    private int maxValuesPerTagPerRequest;
}
