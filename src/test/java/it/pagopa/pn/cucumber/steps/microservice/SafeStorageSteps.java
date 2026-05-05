package it.pagopa.pn.cucumber.steps.microservice;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.IPnCfgClient;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.SafeStorageStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SafeStorageSteps {

    private final ApplicationContext context;
    private final IPnSafeStoragePrivateClient safeStorageClient;
    private final IPnCfgClient cfgClient;
    private final SafeStorageStepsPojo safeStorageStepsPojo;
    private int waitingTime;
    private String clientId;

    @Autowired
    public SafeStorageSteps(ApplicationContext context, IPnSafeStoragePrivateClient safeStorageClient, IPnCfgClient cfgClient) {
        this.context = context;
        this.safeStorageClient = safeStorageClient;
        this.cfgClient = cfgClient;
        safeStorageStepsPojo = new SafeStorageStepsPojo();
    }

    private String computeAndSetSha(String resourceName) {
        try {
            String sha = B2bUtils.computeSha256(context, resourceName);
            safeStorageStepsPojo.setSha256(sha);
            return sha;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage() + " NON è stato possibile computare lo sha");
        }
    }

    @Given("esiste un limite {string} con valore pari a {int}")
    public void setLimit(String limitName, int limitValue) {
        try {
            Field field = safeStorageStepsPojo.getClass().getDeclaredField(limitName);
            field.setAccessible(true);
            field.setInt(safeStorageStepsPojo, limitValue);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private Integer retriveLimitFromPojo(SafeStorageStepsPojo pojo, String fieldName) {
        try {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = pojo.getClass().getMethod(getterName);
            return (int) getter.invoke(pojo);
        } catch (Exception e) {
            log.info(e.getMessage());
            return 0;
        }
    }

    @Given("vengono caricati documenti di tipo {string} in numero {string} a {string}")
    public void uploadMultipleDocuments(String type, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        for (int i = 0; i < quantity; i++) {
            uploadNewDocument(type);
        }
    }

    @Given("vengono caricati documenti di tipo {string} in numero {string} a {string} con tag associati {string}")
    public void uploadMultipleDocumentsWithAssociatedTags(String type, String comparator, String limit, String tagList) {
        int quantity = getLimitValue(comparator, limit);
        Map<String, List<String>> tagMap = new HashMap<>();
        tagMap.put(tagList.split(":")[0], Arrays.asList(tagList.split(":")[1].split(",")));
        uploadDocumentsWithTags(type, tagMap, quantity);
    }

    @Given("vengono caricati documenti di tipo {string} in numero {string} a {string} con associato il tag {string} avente {int} valori diversi")
    public void uploadMultipleDocumentsWithAssociatedTagsWithValues(String type, String comparator, String limit, String tagName, Integer valueNumber) {
        int quantity = getLimitValue(comparator, limit);
        Map<String, List<String>> tagMap = new HashMap<>();
        List<String> values = new LinkedList<>();
        for (int i = 0; i < valueNumber; i++) {
            values.add("test" + (i + 1));
        }
        tagMap.put(tagName, values);
        uploadDocumentsWithTags(type, tagMap, quantity);
    }

    @Given("(il documento viene aggiornato)(i documenti vengono aggiornati) aggiungendo {string} valori per volta al tag {string}, fino a raggiungere il limite di {string}")
    public void singleAddValuesUntilMax(String maxValuesPerTagPerRequest, String tagName, String maxValuesPerTagDocument) {
        int maxValuesPerTagPerRequestInt = retriveLimitFromPojo(safeStorageStepsPojo, maxValuesPerTagPerRequest);
        int maxValuesPerTagDocumentInt = retriveLimitFromPojo(safeStorageStepsPojo, maxValuesPerTagDocument);
        int counterTagsAdded = 0;
        while (counterTagsAdded < maxValuesPerTagDocumentInt) {
            int valuesToAdd = getQuantityToAddInIteration(maxValuesPerTagDocumentInt, maxValuesPerTagPerRequestInt, counterTagsAdded);
            List<String> tagValues = createTagValues(counterTagsAdded, "PARI", String.valueOf(valuesToAdd));
            if (safeStorageStepsPojo.getCreatedFiles().size() == 1) {
                updateSingleContinuativo(tagName, tagValues);
            } else {
                updateMassiveContinuativo(tagName, tagValues);
            }
            counterTagsAdded += valuesToAdd;
        }
    }

    private int getQuantityToAddInIteration(int maxValuesPerTagDocument, int maxValuesPerTagPerRequest, int addedSoFar) {
        return Math.min(maxValuesPerTagDocument - addedSoFar, maxValuesPerTagPerRequest);
    }

    private void uploadDocumentsWithTags(String type, Map<String, List<String>> tagMap, Integer quantity) {
        String resourcePath = type.equals("PN_LEGAL_FACTS_ST") ? "classpath:/long_file.pdf" : "classpath:/multa.pdf";
        String sha256 = computeAndSetSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);
        request.setTags(tagMap);
        for (int i = 0; i < quantity; i++) {
            try {
                FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
                loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
            } catch (HttpClientErrorException httpExc) {
                safeStorageStepsPojo.setHttpException(httpExc);
            }
        }
    }

    @When("il documento viene modificato associandogli il tag {string} con un numero di valori {string} a {string}")
    public void updateSingleWithNValues(String tagName, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        String fileKey = safeStorageStepsPojo.getCreatedFiles().get(0).getKey();
        List<String> tagValues = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            tagValues.add("test" + (i + 1));
        }
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putSETItem(tagName, tagValues);
        try {
            safeStorageStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    @When("i documenti vengono modificati associando al primo il tag {string} con un numero di valori {string} a {string}, mentre al secondo un solo valore")
    public void updateMassiviWithNValues(String tagName, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);

        assertThat(safeStorageStepsPojo.getCreatedFiles().size()).isEqualTo(2);
        String fileKey1 = safeStorageStepsPojo.getCreatedFiles().get(0).getKey();
        String fileKey2 = safeStorageStepsPojo.getCreatedFiles().get(1).getKey();

        List<String> tagValues = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            tagValues.add("test" + (i + 1));
        }

        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        Tags newTag1 = new Tags();
        newTag1.setFileKey(fileKey1);
        newTag1.putSETItem(tagName, tagValues);
        tagsList.add(newTag1);

        Tags newTag2 = new Tags();
        newTag2.setFileKey(fileKey2);
        newTag2.putSETItem(tagName, List.of("test1"));
        tagsList.add(newTag2);

        request.setTags(tagsList);
        try {
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento dei documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    private void updateSingleContinuativo(String tagName, List<String> tagValues) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putSETItem(tagName, tagValues);
        String fileKey = safeStorageStepsPojo.getCreatedFiles().get(0).getKey();
        try {
            safeStorageStepsPojo.setUpdateSingleResponseEntity(
                    safeStorageClient.additionalFileTagsUpdateWithHttpInfo(fileKey, "pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    private void updateMassiveContinuativo(String tagName, List<String> tagValues) {
        List<Tags> tagsList = new LinkedList<>();
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        safeStorageStepsPojo.getCreatedFiles().forEach(file -> {
            Tags newTag = new Tags();
            newTag.setFileKey(file.getKey());
            newTag.putSETItem(tagName, tagValues);
            tagsList.add(newTag);
        });
        request.setTags(tagsList);
        try {
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(
                    safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento dei documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    private List<String> createTagValues(Integer valoriPrecedenti, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        List<String> tagValues = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            tagValues.add("test" + (valoriPrecedenti + (i + 1)));
        }
        return tagValues;
    }

    /**
     * Se limit ha valore numerico viene usato il suo valore, se è una stringa viene usato il valore del campo corrispondente settato nel Pojo.
     * Tale valore viene poi aumentato o decrementato di uno a seconda del valore di comparator ("PARI" lo tiene immutato)
     */
    private int getLimitValue(String comparator, String limit) {
        int quantity = limit.matches("[0-9]+") ?
                Integer.parseInt(limit) : retriveLimitFromPojo(safeStorageStepsPojo, limit);
        if (comparator.equalsIgnoreCase("SUPERIORE")) {
            quantity += 1;
        } else if (comparator.equalsIgnoreCase("INFERIORE")) {
            quantity -= 1;
        }
        return quantity;
    }

    @Given("Viene caricato un nuovo documento di tipo {string}")
    public void uploadNewDocument(String type) {
        String resourcePath = "classpath:/multa.pdf";
        String sha256 = computeAndSetSha(resourcePath);

        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);

        FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
        loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
    }

    @Given("viene caricato su SafeStorage il documento {string} con contentType {string} di tipo {string} e status {string}")
    public void uploadNewDocument(String resourcePath, String contentType, String documentType, String status) {
        String sha256 = computeAndSetSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType(contentType);
        request.setStatus(status != null ? status : "SAVED");
        request.setDocumentType(documentType);
        try {
            // Chiamata al servizio Safe Storage per registrare il file
            FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
            // Upload vero e proprio sulla presigned URL
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath, B2bUtils.APPLICATION_JSON);
        } catch (HttpClientErrorException httpExc) {
            throw new RuntimeException(httpExc);
        }
    }

    @Given("viene caricato un nuovo pdf di 0 byte")
    public void uploadNewEmptyDocument() {
        final String type = "PN_NOTIFICATION_ATTACHMENTS";
        String resourcePath = "classpath:/vuoto.pdf";
        String sha256 = computeAndSetSha(resourcePath);

        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);

        try {
            FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @Given("Vengono caricati {int} nuovi documenti di tipo {string}")
    public void uploadNewPdfDocument(Integer times, String type) {
        for (int i = 0; i < times; i++) {
            uploadNewDocument(type);
        }
    }

    @Given("Viene caricato un nuovo documento di tipo {string} con tag associati")
    public void uploadNewDocumentWithTags(String type, List<String> tagList) {
        String resourcePath = type.equals("PN_LEGAL_FACTS_ST") ? "classpath:/long_file.pdf" : "classpath:/multa.pdf";
        String sha256 = computeAndSetSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);
        if (tagList.contains("tagWithDate")) {
            request.setTags(Map.of("lc_start_date", List.of("2026-02-26T10:00:03Z")));
        } else {
            request.setTags(tagList.stream().collect(Collectors.toMap(
                    tag -> tag.split(":")[0], tag -> Arrays.asList(tag.split(":")[1].split(",")))));
        }
        try {
            FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @Given("Viene caricato un nuovo documento di tipo {string} con un tag avente {int} valori associati")
    public void uploadNewDocumentWithTags(String type, Integer tagNumber) {
        String resourcePath = type.equals("PN_LEGAL_FACTS_ST") ? "classpath:/long_file.pdf" : "classpath:/multa.pdf";
        String sha256 = computeAndSetSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);
        List<String> values = new LinkedList<>();
        for (int i = 0; i < tagNumber; i++) {
            values.add("test" + i);
        }
        request.setTags(new HashMap<>());
        request.getTags().put("global_multivalue", values);
        try {
            FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @Given("Vengono caricati {int} nuovi documenti di tipo {string} con tag associati")
    public void uploadManyNewDocumentsWithTags(Integer documentIndex, String type, List<String> tagList) {
        for (int i = 0; i < documentIndex; i++) {
            uploadNewDocumentWithTags(type, tagList);
        }
    }

    private void loadToPresignedUrl(FileCreationResponse fileCreationResponse, String sha256, String resourcePath) {
        String fileKey = fileCreationResponse.getKey();
        String secret = fileCreationResponse.getSecret();
        String url = fileCreationResponse.getUploadUrl();

        B2bUtils.loadToPresigned(context, url, secret, sha256, resourcePath, B2bUtils.APPLICATION_PDF);
        log.info("FILEKEY: " + fileKey);

        safeStorageStepsPojo.getCreatedFiles().add(fileCreationResponse);
        log.info("File successfully created");
    }

    private void loadToPresignedUrl(FileCreationResponse fileCreationResponse, String sha256, String resourcePath, String contentType) {
        String fileKey = fileCreationResponse.getKey();
        String secret = fileCreationResponse.getSecret();
        String url = fileCreationResponse.getUploadUrl();

        B2bUtils.loadToPresigned(context, url, secret, sha256, resourcePath, contentType);
        log.info("FILEKEY: " + fileKey);

        safeStorageStepsPojo.getCreatedFiles().add(fileCreationResponse);
        log.info("File successfully created");
    }

    @When("Il client {string} tenta di effettuare l'operazione {string} senza essere autorizzato ad accedervi")
    public void utenteNonAutorizzato(String client, String operation) {
        try {
            switch (operation) {
                case "CREATE_FILE" ->
                        safeStorageClient.createFileWithHttpInfo(client, "", "", new FileCreationRequest());
                case "GET_FILE" -> safeStorageClient.getFileWithHttpInfo("test", client, true, true);
                case "UPDATE_SINGLE" ->
                        safeStorageClient.additionalFileTagsUpdateWithHttpInfo("test", client, new AdditionalFileTagsUpdateRequest());
                case "UPDATE_MASSIVE" ->
                        safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(client, new AdditionalFileTagsMassiveUpdateRequest());
                case "GET_TAGS" ->
                        safeStorageClient.additionalFileTagsGetWithHttpInfo("PN_NOTIFICATION_ATTACHMENTS-eabd62ef59444526beeab293b2255ace.pdf", client);
                case "SEARCH_FILE" ->
                        safeStorageClient.additionalFileTagsSearchWithHttpInfo(client, "AND", true, new HashMap<>());
                default -> throw new IllegalArgumentException("Operazione non supportata: " + operation);
            }
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @Then("La chiamata genera un errore con status code {int}")
    public void checkForStatusCode(Integer statusCode) {
        assertThat(safeStorageStepsPojo.getHttpException()).as("Diversamente da quanto atteso la chiamata non ha prodotto alcuna eccezione").isNotNull();
        assertThat(statusCode)
                .as("Il codice di errore non combacia con quanto atteso")
                .isEqualTo(safeStorageStepsPojo.getHttpException().getRawStatusCode());
    }

    @And("Il messaggio di errore riporta la dicitura {string}")
    public void checkForStatusCode(String errorMessage) {
        assertThat(safeStorageStepsPojo.getHttpException()).as("Diversamente da quanto atteso la chiamata non ha prodotto alcuna eccezione").isNotNull();
        assertThat(safeStorageStepsPojo.getHttpException().getMessage())
                .as("Il messaggio di errore riporta la seguente dicitura: ")
                .contains(errorMessage);
    }

    @When("La request presenta una ripetizione della stessa fileKey")
    public void updateDocumentsWrongRequest(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", createWrongMassiveRequest(data));
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    private AdditionalFileTagsMassiveUpdateRequest createWrongMassiveRequest(List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        data.forEach(d -> {
            Tags newTag = new Tags();
            int documentIndex = Integer.parseInt(d.get("documentIndex"));
            newTag.setFileKey(safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey());
            if (d.get("operation").equals("SET")) {
                newTag.putSETItem(d.get("tag").split(":")[0],
                        Arrays.stream(d.get("tag").split(":")[1].split(",")).toList());
            } else if (d.get("operation").equals("DELETE")) {
                newTag.putDELETEItem(d.get("tag").split(":")[0],
                        Arrays.stream(d.get("tag").split(":")[1].split(",")).toList());
            }
            tagsList.add(newTag);
        });
        request.setTags(tagsList);
        return request;
    }

    @When("Si modifica il documento {int} secondo le seguenti operazioni")
    public void updateDocument(Integer documentIndex, DataTable dataTable) {
        Assertions.assertTrue(documentIndex <= safeStorageStepsPojo.getCreatedFiles().size());
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String fileKey = safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        try {
            safeStorageStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(data)));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    @When("Si modifica il documento {int} associando valori a un singolo tag in numero {string} a {string}")
    public void updateDocument(Integer documentIndex, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        Assertions.assertTrue(documentIndex <= safeStorageStepsPojo.getCreatedFiles().size());
        String fileKey = safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        try {
            safeStorageStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(0, quantity)));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    @When("tali documenti vengono modificati simultaneamente associando a ciascuno il tag {string}")
    public void updateAllDocumentsWithSameTag(String tagName) {
        Assertions.assertFalse(safeStorageStepsPojo.getCreatedFiles().isEmpty());
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        for (int i = 0; i < safeStorageStepsPojo.getCreatedFiles().size(); i++) {
            String fileKey = safeStorageStepsPojo.getCreatedFiles().get(i).getKey();
            Tags newTag = new Tags();
            newTag.setFileKey(fileKey);
            newTag.putSETItem(tagName, List.of("test" + (i + 1)));
            tagsList.add(newTag);
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response =
                    safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request);
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    @When("Si modificano i documenti secondo le seguenti operazioni")
    public void updateDocuments(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", createMassiveRequest(data));
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
    }

    private AdditionalFileTagsMassiveUpdateRequest createMassiveRequest(List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        Set<Integer> indexes = data.stream().map(x -> Integer.valueOf(x.get("documentIndex"))).collect(Collectors.toSet());
        indexes.forEach(i -> {
            Tags newTag = new Tags();
            newTag.setFileKey(safeStorageStepsPojo.getCreatedFiles().get(i - 1).getKey());
            List<Map<String, String>> documentMaps = data.stream().filter(map -> Integer.valueOf(map.get("documentIndex")).equals(i)).toList();
            populateTag(newTag, documentMaps);
            tagsList.add(newTag);
        });
        request.setTags(tagsList);
        return request;
    }

    @Given("si prova a fare l'update di {int} documenti inesistenti secondo le seguenti operazioni")
    public AdditionalFileTagsMassiveUpdateRequest createMassiveRequestInesistente(Integer numberOfDocuments, List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        for (int i = 0; i < numberOfDocuments; i++) {
            Tags newTag = new Tags();
            newTag.setFileKey("fileKeyInesistente" + (i + 1));
            int index = i + 1;
            List<Map<String, String>> documentMaps = data.stream().filter(
                    map -> Integer.valueOf(map.get("documentIndex")).equals(index)).toList();
            populateTag(newTag, documentMaps);
            tagsList.add(newTag);
            safeStorageStepsPojo.getFileKeyInesistenti().add(newTag.getFileKey());
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", request);
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
        return request;
    }

    @When("si prova a fare l'update dei documenti creati e di {int} documenti inesistenti secondo le seguenti operazioni")
    public AdditionalFileTagsMassiveUpdateRequest createMassiveRequestEsistenteAndInesistente(Integer numberOfDocuments, List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        for (int i = 0; i < safeStorageStepsPojo.getCreatedFiles().size(); i++) {
            FileCreationResponse document = safeStorageStepsPojo.getCreatedFiles().get(i);
            Tags newTag = new Tags();
            newTag.setFileKey(document.getKey());
            int index = i + 1;
            List<Map<String, String>> documentMaps = data.stream().filter(map -> Integer.valueOf(map.get("documentIndex")).equals(index)).toList();
            populateTag(newTag, documentMaps);
            tagsList.add(newTag);
        }
        for (int i = 0; i < numberOfDocuments; i++) {
            Tags newTag = new Tags();
            newTag.setFileKey("fileKeyInesistente" + (i + 1));
            int index = safeStorageStepsPojo.getCreatedFiles().size() + i + 1;
            List<Map<String, String>> documentMaps = data.stream().filter(
                    map -> Integer.valueOf(map.get("documentIndex")).equals(index)).toList();
            populateTag(newTag, documentMaps);
            tagsList.add(newTag);
            safeStorageStepsPojo.getFileKeyInesistenti().add(newTag.getFileKey());
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", request);
            safeStorageStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            safeStorageStepsPojo.setHttpException(e);
        }
        return request;
    }

    private void populateTag(Tags newTag, List<Map<String, String>> maps) {
        maps.forEach(map -> {
            String tag = map.get("tag");
            String operation = map.get("operation");
            String[] splittedTags = tag.split(":");
            String tagName = splittedTags[0];
            List<String> tagValues = Arrays.stream(splittedTags[1].split(",")).toList();
            if (operation.equals("SET")) {
                newTag.putSETItem(tagName, tagValues);
            } else if (operation.equals("DELETE")) {
                newTag.putDELETEItem(tagName, tagValues);
            }
        });
    }

    @And("I primi {int} documenti vengono modificati secondo le seguenti operazioni")
    public void updateNDocuments(Integer documentIndex, DataTable dataTable) {
        int createdFiles = safeStorageStepsPojo.getCreatedFiles().size();
        assertThat(documentIndex)
                .as("Indice documento (" + documentIndex + ") superiore al numero di documenti creati (" + createdFiles + ")")
                .isLessThanOrEqualTo(createdFiles);
        for (int i = 1; i <= documentIndex; i++) {
            updateDocument(i, dataTable);
        }
    }

    @Then("Il documento {int} è stato correttamente modificato con la seguente lista di tag")
    public void checkDocument(Integer documentIndex, List<String> expectedTags) {
        Map<String, List<String>> tagMap = retrieveDocumentTags(documentIndex);

        if (expectedTags.contains("null")) {
            Assertions.assertTrue(tagMap.isEmpty());
        } else if (expectedTags.contains("tagWithDate")) {
            assert tagMap != null;
            Assertions.assertEquals(expectedTags.size(), tagMap.size());
            Assertions.assertTrue(tagMap.containsKey("lc_start_date"));
            Assertions.assertTrue(tagMap.get("lc_start_date").contains("2026-02-26T10:00:03Z"));
        } else {
            assert tagMap != null;
            Assertions.assertEquals(expectedTags.size(), tagMap.size());

            expectedTags.forEach(tag -> {
                String[] splittedTags = tag.split(":");
                String tagName = splittedTags[0];
                List<String> tagValues = Arrays.stream(splittedTags[1].split(",")).toList();

                Assertions.assertTrue(tagMap.containsKey(tagName));
                Assertions.assertEquals(tagValues.size(), tagMap.get(tagName).size());
                tagValues.forEach(t -> Assertions.assertTrue(tagMap.get(tagName).contains(t)));
            });
        }
    }

    @Then("Il documento {int} non contiene la seguente lista di tag")
    public void checkTagsNotPresent(Integer documentIndex, DataTable dataTable) {
        Assertions.assertNotNull(dataTable);
        Map<String, List<String>> tagMap = retrieveDocumentTags(documentIndex);
        List<String> expectedTags = dataTable.asList();

        expectedTags.forEach(tag -> {
            String[] splittedTags = tag.split(":");
            String tagName = splittedTags[0];
            List<String> tagValues = Arrays.stream(splittedTags[1].split(",")).toList();
            if (tagMap.containsKey(tagName)) {
                Assertions.assertNotEquals(tagValues, tagMap.get(tagName));
            }
        });
    }

    @Then("Il documento {int} è correttamente formato con la seguente lista di tag")
    public void getAndCheckFile(Integer documentIndex, List<String> expectedTags) {
        if (!expectedTags.contains("null")) {
            try {
                Map<String, List<String>> tagMap = safeStorageClient.getFile(
                        safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey(),
                        false, true).getTags();
                assert tagMap != null;
                Assertions.assertEquals(expectedTags.size(), tagMap.size());

                if (expectedTags.contains("tagWithDate")) {
                    assert tagMap != null;
                    Assertions.assertTrue(tagMap.containsKey("lc_start_date"));
                    Assertions.assertTrue(tagMap.get("lc_start_date").contains("2026-02-26T10:00:03Z"));
                } else {
                    expectedTags.forEach(tag -> {
                        String[] splittedTags = tag.split(":");
                        String tagName = splittedTags[0];
                        List<String> tagValues = Arrays.stream(splittedTags[1].split(",")).toList();

                        Assertions.assertTrue(tagMap.containsKey(tagName));
                        Assertions.assertEquals(tagValues.size(), tagMap.get(tagName).size());
                        tagValues.forEach(t -> Assertions.assertTrue(tagMap.get(tagName).contains(t)));
                    });
                }
            } catch (HttpClientErrorException httpExc) {
                safeStorageStepsPojo.setHttpException(httpExc);
            }
        }
    }

    @Then("Il risultato della search contiene le fileKey relative ai seguenti documenti")
    public void checkSearchResult(DataTable dataTable) {
        List<String> searchResult = safeStorageStepsPojo.getAdditionalFileTagsSearchResponseResponseEntity().getBody().getFileKeys()
                .stream().map(AdditionalFileTagsSearchResponseFileKeysInner::getFileKey).toList();
        List<String> documentIndexes = dataTable.asList();
        if (documentIndexes.contains("null")) {
            Assertions.assertTrue(searchResult.isEmpty());
        } else {
            List<String> expectedFileKeys = new LinkedList<>();
            documentIndexes.forEach(x -> expectedFileKeys.add(safeStorageStepsPojo.getCreatedFiles().get(Integer.parseInt(x) - 1).getKey()));
            expectedFileKeys.forEach(x -> Assertions.assertTrue(searchResult.contains(x)));
        }
    }

    private AdditionalFileTagsUpdateRequest createUpdateRequest(Map<String, String> specificationsMap) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        specificationsMap.forEach((tag, operation) -> {
            if (operation.equals("SET")) {
                request.putSETItem(tag.split(":")[0], Arrays.stream(tag.split(":")[1].split(",")).toList());
            } else if (operation.equals("DELETE")) {
                request.putDELETEItem(tag.split(":")[0], Arrays.stream(tag.split(":")[1].split(",")).toList());
            }
        });
        return request;
    }

    private AdditionalFileTagsUpdateRequest createUpdateRequest(Integer iterationNumber, Integer tagNumber) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        List<String> values = new ArrayList<>();
        for (int i = 0; i < tagNumber; i++) {
            int currentValue = iterationNumber * 100;
            values.add("test" + (currentValue + i + 1));
        }
        request.putSETItem("global_multivalue", values);
        return request;
    }

    private Map<String, List<String>> retrieveDocumentTags(Integer documentIndex) {
        return safeStorageClient.additionalFileTagsGet(safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey()).getTags();
    }

    @Then("L'update massivo va in successo con stato {int}")
    public void checkUpdateMassiveStatusCode(Integer statusCode) {
        Assertions.assertNotNull(safeStorageStepsPojo.getUpdateMassiveResponseEntity());
        Assertions.assertEquals(safeStorageStepsPojo.getUpdateMassiveResponseEntity().getStatusCodeValue(), statusCode);
    }

    @When("Vengono ricercate con logica {string} le fileKey aventi i seguenti tag")
    public void searchWithTags(String logic, List<String> tags) {
        if (logic.isEmpty()) {
            logic = null;
        }
        Map<String, String> tagMap = new HashMap<>();
        if (!tags.contains("null")) {
            tagMap = tags.stream().collect(Collectors.toMap(
                    tag -> tag.split(":")[0], tag -> tag.split(":")[1].split(",")[0]));
        }
        try {
            ResponseEntity<AdditionalFileTagsSearchResponse> response = safeStorageClient.additionalFileTagsSearchWithHttpInfo(
                    "pn-test", logic, true, tagMap);
            safeStorageStepsPojo.setAdditionalFileTagsSearchResponseResponseEntity(response);
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @When("Vengono ricercate con logica {string} delle fileKey impostando come filtro di ricerca un numero di tags {string} a {string}")
    public void searchWithCertainAmountOfTags(String logic, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        if (logic.isEmpty()) {
            logic = null;
        }
        Map<String, String> tagMap = new HashMap<>();
        for (int i = 0; i < quantity; i++) {
            tagMap.put("tagInventato" + (i + 1), "test" + (i + 1));
        }
        try {
            ResponseEntity<AdditionalFileTagsSearchResponse> response = safeStorageClient.additionalFileTagsSearchWithHttpInfo(
                    "pn-test", logic, true, tagMap);
            safeStorageStepsPojo.setAdditionalFileTagsSearchResponseResponseEntity(response);
        } catch (HttpClientErrorException httpExc) {
            safeStorageStepsPojo.setHttpException(httpExc);
        }
    }

    @And("La response contiene uno o più errori {string} riportanti la dicitura {string} riguardanti il documento {int}")
    public void checkUpdateMassiveErrors(String errorCode, String errorMessage, Integer documentIndex) {
        Assertions.assertNotNull(safeStorageStepsPojo.getUpdateMassiveResponseEntity());
        Assertions.assertNotNull(safeStorageStepsPojo.getUpdateMassiveResponseEntity().getBody());
        ErrorDetail fileKeyError;
        if (safeStorageStepsPojo.getFileKeyInesistenti().isEmpty()) {
            String faultyFileKey = safeStorageStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
            fileKeyError = safeStorageStepsPojo.getUpdateMassiveResponseEntity().getBody().getErrors()
                    .stream().filter(x -> x.getFileKey().contains(faultyFileKey)).findFirst().orElse(null);
        } else {
            fileKeyError = safeStorageStepsPojo.getUpdateMassiveResponseEntity().getBody().getErrors().get(documentIndex - safeStorageStepsPojo.getCreatedFiles().size() - 1);
        }
        assertThat(fileKeyError).as("Diversamente da quanto atteso la chiamata non ha prodotto alcuna eccezione").isNotNull();
        log.info("Errore sulla filekey " + fileKeyError.getFileKey().get(0));
        Assertions.assertEquals(errorCode, fileKeyError.getResultCode());
        assertThat(fileKeyError.getResultDescription())
                .as("Il messaggio di errore riporta la seguente dicitura: ")
                .contains(errorMessage);
    }

    @Then("Il documento {int} è associato alla seguente lista di tag")
    public void getTagsAndGetFiles(Integer documentIndex, List<String> expectedTags) {
        checkDocument(documentIndex, expectedTags);
        getAndCheckFile(documentIndex, expectedTags);
        if (safeStorageStepsPojo.getHttpException() != null) {
            throw safeStorageStepsPojo.getHttpException();
        }
    }

    @After("@aggiuntaTag")
    public void cleanDocuments() {
        safeStorageStepsPojo.getCreatedFiles().forEach(file -> {
            AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
            Map<String, List<String>> tagMap = safeStorageClient.additionalFileTagsGet(file.getKey()).getTags();
            log.info("PRE-CANCELLAZIONE: " + tagMap.toString());
            if (!tagMap.isEmpty()) {
                int maxValuesLimit = 100;//TODO al variare di MaxValuesPerTagPerRequest questo valore deve cambiare di conseguenza
                for (Map.Entry<String, List<String>> entry : tagMap.entrySet()) {
                    int numberOfValues = entry.getValue().size();
                    int valuesDeleted = 0;
                    while (valuesDeleted < numberOfValues) {
                        int valuesToDelete = Math.min(maxValuesLimit, (numberOfValues - valuesDeleted));
                        List<String> valuesToDeleteList = entry.getValue().subList(valuesDeleted, valuesDeleted + valuesToDelete);
                        request.putDELETEItem(entry.getKey(), valuesToDeleteList);
                        safeStorageClient.additionalFileTagsUpdate(file.getKey(), request);
                        valuesDeleted += valuesToDelete;
                    }
                }
                log.info("POST-CANCELLAZIONE");
            }
        });
    }

    @Given("il client {string} ha il campo {string} valorizzato a {int} minuti")
    public void esisteUnaConfigurazionePerIlClientRelativaAlCampoDiMinuti(String cxId, String fieldName, Integer expectedTiming) {
        UserConfiguration userConfiguration = cfgClient.getCurrentClientConfig(cxId);
        boolean isUpload = fieldName.equalsIgnoreCase("DurationMinutestUpload");
        Integer timing = isUpload ? userConfiguration.getDurationMinutesUpload() : userConfiguration.getDurationMinutesDownload();
        if (expectedTiming != 0) {
            assertThat(timing).as("Il valore del campo " + fieldName + " non coincide con quanto atteso").isEqualTo(expectedTiming);
            waitingTime = expectedTiming;
        } else {
            //in assenza di valori specifici impostati per un client, valgono quelli di default specificati sul properties di safe storage
            assertThat(timing).as("Il valore del campo " + fieldName + " dovrebbe essere null").isNull();
            waitingTime = isUpload ? 2 : 3;
        }
        clientId = cxId;
    }

    @Given("viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload")
    public void getPresignedUrlUpload() {
        if (clientId.equalsIgnoreCase("pn-delivery")) {
            safeStorageClient.setApiKey("pn-delivery_api_key");
        }
        String resourcePath = "classpath:/multa.pdf";
        String sha256 = computeAndSetSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType("PN_NOTIFICATION_ATTACHMENTS");

        // Chiamata a Safe Storage per registrare il file e ottenere la presigned url di upload
        ResponseEntity<FileCreationResponse> responseEntity = safeStorageClient.createFileWithHttpInfo(clientId, sha256, "SHA256", request);
        assertThat(responseEntity).as("La responseEntity non dev'essere null").isNotNull();
        FileCreationResponse fileCreationResponse = responseEntity.getBody();
        assertThat(fileCreationResponse).as("La FileCreationResponse non dev'essere null").isNotNull();
        safeStorageStepsPojo.setFileCreationResponse(fileCreationResponse);
        safeStorageStepsPojo.setResourcePath(resourcePath);
    }

    @Given("viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download")
    public void getPresignedUrlDownload() {
        if (clientId.equalsIgnoreCase("pn-delivery")) {
            safeStorageClient.setApiKey("pn-delivery_api_key");
        }
        String fileKey = safeStorageStepsPojo.getFileCreationResponse().getKey();
        assertThat(fileKey).as("La file key del documento non dev'essere null").isNotNull();

        // Chiamata a Safe Storage per recuperare il file e ottenere la presigned url di download
        ResponseEntity<FileDownloadResponse> responseEntity = safeStorageClient.getFileWithHttpInfo(fileKey, clientId, false, false);
        assertThat(responseEntity.getBody()).as("Il response body non dev'essere null").isNotNull();
        FileDownloadResponse fileDownloadResponse = responseEntity.getBody();
        assertThat(fileDownloadResponse.getDownload()).as("Il FileDownloadInfo non dev'essere null").isNotNull();
        assertThat(fileDownloadResponse.getDownload().getUrl()).as("L'url di FileDownloadInfo non dev'essere null").isNotNull();
        safeStorageStepsPojo.setFileDownloadResponse(fileDownloadResponse);
    }

    @And("si aspetta che la presigned-url scada")
    public void aspettoCheLaPresignedUrlScada() {
        assertThat(waitingTime).as("Il valore di waitingTime non dev'essere null").isNotNull();
        log.info("Attendo " + waitingTime + " minuti per far scadere la presigned-url");
        long delayInMilliseconds = waitingTime * 60000L;
        try {
            Thread.sleep(delayInMilliseconds);
            log.info("Sono trascorsi " + waitingTime + " minuti, la presigned-url ormai è scaduta");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("L'attesa è stata interrotta: " + e.getMessage(), e);
        }
    }

    @When("si effettua un {string} tramite presignedUrl del documento precedentemente (registrato)(caricato)")
    public void caricoIlDocumentoPrecedentementeRegistrato(String operation) {
        switch (operation.toLowerCase()) {
            case "upload" -> {
                FileCreationResponse fileCreationResponse = safeStorageStepsPojo.getFileCreationResponse();
                String sha256 = safeStorageStepsPojo.getSha256();
                String resourcePath = safeStorageStepsPojo.getResourcePath();

                assertThat(fileCreationResponse).as("La fileCreationResponse non dev'essere null").isNotNull();
                assertThat(resourcePath).as("Il resourcePath non dev'essere null").isNotNull();
                assertThat(sha256).as("Lo SHA256 non dev'essere null").isNotNull();
                log.info("Upload presigned url: " + fileCreationResponse.getUploadUrl());
                try {
                    loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
                    log.info("Upload tramite presigned URL riuscito");
                } catch (HttpClientErrorException httpClientErrorException) {
                    safeStorageStepsPojo.setHttpException(httpClientErrorException);
                }
            }
            case "download" -> {
                String downloadUrl = safeStorageStepsPojo.getFileDownloadResponse().getDownload().getUrl();
                log.info("Download presigned url: " + downloadUrl);

                HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).GET().build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    int statusCode = response.statusCode();
                    String body = response.body();
                    log.info("HTTP status code: " + response.statusCode());
                    log.info(response.body());
                    if (statusCode == 200) {
                        log.info("Download tramite presigned URL riuscito");
                    } else {
                        log.info("Download tramite presigned URL fallito");
                        assertThat(statusCode).as("Lo status code d'errore non coincide con quanto atteso").isEqualTo(403);
                        assertThat(response.body()).contains("Request has expired");
                        safeStorageStepsPojo.setHttpException(new HttpClientErrorException(HttpStatus.FORBIDDEN, body));
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante l'esecuzione del test: " + e.getMessage());
                }
            }
            default -> throw new IllegalArgumentException("Operazione non riconosciuta: " + operation);
        }
    }

    @Then("l'operazione di {string} restituisce status code {int}")
    public void checkResponseEntityStatusCode(String operation, int statusCode) {
        switch (statusCode) {
            case 200 -> assertThat(safeStorageStepsPojo.getHttpException())
                    .as("L'operazione di " + operation + " non deve aver prodotto errori")
                    .isNull();
            case 403 -> assertThat(safeStorageStepsPojo.getHttpException().getRawStatusCode())
                    .as("Lo status code dell'operazione di " + operation + " non coincide con quanto atteso")
                    .isEqualTo(statusCode);
        }
    }

    @Given("Viene caricato un nuovo documento {string} di tipo {string}")
    public void uploadNewDocument(String documentName, String type) {
        String resourcePath = "classpath:/" + documentName;
        String sha256 = computeAndSetSha(resourcePath);

        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);

        FileCreationResponse fileCreationResponse = safeStorageClient.createFile(sha256, "SHA256", request);
        loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
    }
}