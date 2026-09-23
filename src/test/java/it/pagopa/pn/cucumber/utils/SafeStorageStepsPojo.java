package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
public class SafeStorageStepsPojo {

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
    //irrobustimento gestione documentale
    private FileCreationResponse fileCreationResponse;
    private FileDownloadResponse fileDownloadResponse;
    private String resourcePath;
    //disponibilita documenti
    private Integer fileMetadataUpdateStatusCode;
    private Integer fileDownloadStatusCode;
    private String issuedDownloadUrl;
    private Integer issuedDownloadStatusCode;
    private Integer informationAccessStatusCodeBeforeExpiration;
    private Integer informationAccessStatusCode;
    // Ultima fine disponibilita impostata tramite l'API, usata per confrontarla con quanto
    // la risposta di lettura riporta.
    private OffsetDateTime lastAvailableUntilSet;
    // Ultima retention impostata esplicitamente tramite l'API (indipendente dalla fine
    // disponibilita), usata con lo stesso scopo.
    private OffsetDateTime lastRetentionUntilSet;
    // Conservazione garantita registrata come riferimento indipendente PRIMA di una lettura,
    // per verificare che la data restituita coincida con quella nota (non dedotta dalla
    // risposta stessa).
    private OffsetDateTime capturedRetentionUntil;

    public SafeStorageStepsPojo() {
        this.createdFiles = new LinkedList<>();
        this.fileKeyInesistenti = new LinkedList<>();
    }
}
