package it.pagopa.pn.cucumber.steps.microservice;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import it.pagopa.pn.cucumber.utils.IndicizzazioneStepsPojo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SafeStorageSteps {

    private final IPnSafeStoragePrivateClient safeStorageClient;
    private final PnPaB2bUtils b2bUtils;
    private final IndicizzazioneStepsPojo indicizzazioneStepsPojo;

    @Autowired
    public SafeStorageSteps(IPnSafeStoragePrivateClient safeStorageClient, PnPaB2bUtils b2bUtils) {
        this.safeStorageClient = safeStorageClient;
        this.b2bUtils = b2bUtils;
        this.indicizzazioneStepsPojo = new IndicizzazioneStepsPojo();
    }

    private String computeSha(String resourceName) {
        try {
            this.indicizzazioneStepsPojo.setSha256(this.b2bUtils.computeSha256(resourceName));
            return this.indicizzazioneStepsPojo.getSha256();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage() + " NON è stato possibile computare lo sha");
        }
    }

    @Given("esiste un limite {string} con valore pari a {int}")
    public void setLimit(String limitName, int limitValue) {
        try {
            Field field = this.indicizzazioneStepsPojo.getClass().getDeclaredField(limitName);
            field.setAccessible(true);
            field.setInt(this.indicizzazioneStepsPojo, limitValue);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private Integer retriveLimitFromPojo(IndicizzazioneStepsPojo pojo, String fieldName) {
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
        int maxValuesPerTagPerRequestInt = retriveLimitFromPojo(this.indicizzazioneStepsPojo, maxValuesPerTagPerRequest);
        int maxValuesPerTagDocumentInt = retriveLimitFromPojo(this.indicizzazioneStepsPojo, maxValuesPerTagDocument);
        int counterTagsAdded = 0;
        while (counterTagsAdded < maxValuesPerTagDocumentInt) {
            int valuesToAdd = getQuantityToAddInIteration(maxValuesPerTagDocumentInt, maxValuesPerTagPerRequestInt, counterTagsAdded);
            List<String> tagValues = createTagValues(counterTagsAdded, "PARI", String.valueOf(valuesToAdd));
            if (this.indicizzazioneStepsPojo.getCreatedFiles().size() == 1) {
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
        String sha256 = computeSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);
        request.setTags(tagMap);
        for (int i = 0; i < quantity; i++) {
            try {
                FileCreationResponse fileCreationResponse = this.safeStorageClient.createFile(sha256, "SHA256", request);
                loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
            } catch (HttpClientErrorException httpExc) {
                this.indicizzazioneStepsPojo.setHttpException(httpExc);
            }
        }
    }

    @When("il documento viene modificato associandogli il tag {string} con un numero di valori {string} a {string}")
    public void updateSingleWithNValues(String tagName, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(0).getKey();
        List<String> tagValues = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            tagValues.add("test" + (i + 1));
        }
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putSETItem(tagName, tagValues);
        try {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    @When("i documenti vengono modificati associando al primo il tag {string} con un numero di valori {string} a {string}, mentre al secondo un solo valore")
    public void updateMassiviWithNValues(String tagName, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);

        assertThat(this.indicizzazioneStepsPojo.getCreatedFiles().size()).isEqualTo(2);
        String fileKey1 = this.indicizzazioneStepsPojo.getCreatedFiles().get(0).getKey();
        String fileKey2 = this.indicizzazioneStepsPojo.getCreatedFiles().get(1).getKey();

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
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento dei documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    private void updateSingleContinuativo(String tagName, List<String> tagValues) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putSETItem(tagName, tagValues);
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(0).getKey();
        try {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(
                    safeStorageClient.additionalFileTagsUpdateWithHttpInfo(fileKey, "pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    private void updateMassiveContinuativo(String tagName, List<String> tagValues) {
        List<Tags> tagsList = new LinkedList<>();
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        this.indicizzazioneStepsPojo.getCreatedFiles().forEach(file -> {
            Tags newTag = new Tags();
            newTag.setFileKey(file.getKey());
            newTag.putSETItem(tagName, tagValues);
            tagsList.add(newTag);
        });
        request.setTags(tagsList);
        try {
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(
                    safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento dei documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
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
                Integer.parseInt(limit) : retriveLimitFromPojo(this.indicizzazioneStepsPojo, limit);
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
        String sha256 = computeSha(resourcePath);

        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);

        FileCreationResponse fileCreationResponse = this.safeStorageClient.createFile(sha256, "SHA256", request);
        loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
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
        String sha256 = computeSha(resourcePath);
        FileCreationRequest request = new FileCreationRequest();
        request.setContentType("application/pdf");
        request.setStatus("SAVED");
        request.setDocumentType(type);
        request.setTags(tagList.stream().collect(Collectors.toMap(
                tag -> tag.split(":")[0], tag -> Arrays.asList(tag.split(":")[1].split(",")))));
        try {
            FileCreationResponse fileCreationResponse = this.safeStorageClient.createFile(sha256, "SHA256", request);
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
        }
    }

    @Given("Viene caricato un nuovo documento di tipo {string} con un tag avente {int} valori associati")
    public void uploadNewDocumentWithTags(String type, Integer tagNumber) {
        String resourcePath = type.equals("PN_LEGAL_FACTS_ST") ? "classpath:/long_file.pdf" : "classpath:/multa.pdf";
        String sha256 = computeSha(resourcePath);
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
            FileCreationResponse fileCreationResponse = this.safeStorageClient.createFile(sha256, "SHA256", request);
            loadToPresignedUrl(fileCreationResponse, sha256, resourcePath);
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
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

        this.b2bUtils.loadToPresigned(url, secret, sha256, resourcePath);
        log.info("FILEKEY: " + fileKey);

        this.indicizzazioneStepsPojo.getCreatedFiles().add(fileCreationResponse);
        log.info("File successfully created");
    }

    @When("Il client {string} tenta di effettuare l'operazione {string} senza essere autorizzato ad accedervi")
    public void utenteNonAutorizzato(String client, String operation) {
        try {
            switch (operation) {
                case "CREATE_FILE" ->
                        this.safeStorageClient.createFileWithHttpInfo(client, "", "", new FileCreationRequest());
                case "GET_FILE" -> this.safeStorageClient.getFileWithHttpInfo(
                        "test", client, true, true);
                case "UPDATE_SINGLE" ->
                        this.safeStorageClient.additionalFileTagsUpdateWithHttpInfo("test", client, new AdditionalFileTagsUpdateRequest());
                case "UPDATE_MASSIVE" -> this.safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                        client, new AdditionalFileTagsMassiveUpdateRequest());
                case "GET_TAGS" -> this.safeStorageClient.additionalFileTagsGetWithHttpInfo(
                        "PN_NOTIFICATION_ATTACHMENTS-eabd62ef59444526beeab293b2255ace.pdf", client);
                case "SEARCH_FILE" -> this.safeStorageClient.additionalFileTagsSearchWithHttpInfo(
                        client, "AND", true, new HashMap<>());
                default -> throw new IllegalArgumentException("Operazione non supportata: " + operation);
            }
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
        }
    }

    @Then("La chiamata genera un errore con status code {int}")
    public void checkForStatusCode(Integer statusCode) {
        assertThat(this.indicizzazioneStepsPojo.getHttpException()).as("Diversamente da quanto atteso la chiamata non ha prodotto alcuna eccezione").isNotNull();
        assertThat(statusCode)
                .as("Il codice di errore non combacia con quanto atteso")
                .isEqualTo(this.indicizzazioneStepsPojo.getHttpException().getRawStatusCode());
    }

    @And("Il messaggio di errore riporta la dicitura {string}")
    public void checkForStatusCode(String errorMessage) {
        assertThat(this.indicizzazioneStepsPojo.getHttpException()).as("Diversamente da quanto atteso la chiamata non ha prodotto alcuna eccezione").isNotNull();
        assertThat(this.indicizzazioneStepsPojo.getHttpException().getMessage())
                .as("Il messaggio di errore riporta la seguente dicitura: ")
                .contains(errorMessage);
    }

    @When("La request presenta una ripetizione della stessa fileKey")
    public void updateDocumentsWrongRequest(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", createWrongMassiveRequest(data));
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    private AdditionalFileTagsMassiveUpdateRequest createWrongMassiveRequest(List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        data.forEach(d -> {
            Tags newTag = new Tags();
            int documentIndex = Integer.parseInt(d.get("documentIndex"));
            newTag.setFileKey(this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey());
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
        Assertions.assertTrue(documentIndex <= this.indicizzazioneStepsPojo.getCreatedFiles().size());
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        try {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(data)));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    @When("Si modifica il documento {int} associando valori a un singolo tag in numero {string} a {string}")
    public void updateDocument(Integer documentIndex, String comparator, String limit) {
        int quantity = getLimitValue(comparator, limit);
        Assertions.assertTrue(documentIndex <= this.indicizzazioneStepsPojo.getCreatedFiles().size());
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        try {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(0, quantity)));
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    @When("tali documenti vengono modificati simultaneamente associando a ciascuno il tag {string}")
    public void updateAllDocumentsWithSameTag(String tagName) {
        Assertions.assertFalse(this.indicizzazioneStepsPojo.getCreatedFiles().isEmpty());
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        for (int i = 0; i < this.indicizzazioneStepsPojo.getCreatedFiles().size(); i++) {
            String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(i).getKey();
            Tags newTag = new Tags();
            newTag.setFileKey(fileKey);
            newTag.putSETItem(tagName, List.of("test" + (i + 1)));
            tagsList.add(newTag);
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response =
                    safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo("pn-test", request);
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    @When("Si modificano i documenti secondo le seguenti operazioni")
    public void updateDocuments(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", createMassiveRequest(data));
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
    }

    private AdditionalFileTagsMassiveUpdateRequest createMassiveRequest(List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        Set<Integer> indexes = data.stream().map(x -> Integer.valueOf(x.get("documentIndex"))).collect(Collectors.toSet());
        indexes.forEach(i -> {
            Tags newTag = new Tags();
            newTag.setFileKey(this.indicizzazioneStepsPojo.getCreatedFiles().get(i - 1).getKey());
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
            this.indicizzazioneStepsPojo.getFileKeyInesistenti().add(newTag.getFileKey());
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", request);
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
        }
        return request;
    }

    @When("si prova a fare l'update dei documenti creati e di {int} documenti inesistenti secondo le seguenti operazioni")
    public AdditionalFileTagsMassiveUpdateRequest createMassiveRequestEsistenteAndInesistente(Integer numberOfDocuments, List<Map<String, String>> data) {
        AdditionalFileTagsMassiveUpdateRequest request = new AdditionalFileTagsMassiveUpdateRequest();
        List<Tags> tagsList = new LinkedList<>();
        for (int i = 0; i < this.indicizzazioneStepsPojo.getCreatedFiles().size(); i++) {
            FileCreationResponse document = this.indicizzazioneStepsPojo.getCreatedFiles().get(i);
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
            int index = this.indicizzazioneStepsPojo.getCreatedFiles().size() + i + 1;
            List<Map<String, String>> documentMaps = data.stream().filter(
                    map -> Integer.valueOf(map.get("documentIndex")).equals(index)).toList();
            populateTag(newTag, documentMaps);
            tagsList.add(newTag);
            this.indicizzazioneStepsPojo.getFileKeyInesistenti().add(newTag.getFileKey());
        }
        request.setTags(tagsList);
        try {
            ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> response = safeStorageClient.additionalFileTagsMassiveUpdateWithHttpInfo(
                    "pn-test", request);
            this.indicizzazioneStepsPojo.setUpdateMassiveResponseEntity(response);
        } catch (HttpClientErrorException e) {
            log.info("Errore durante l'aggiornamento del documento: {}", e.getMessage());
            this.indicizzazioneStepsPojo.setHttpException(e);
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
        int createdFiles = this.indicizzazioneStepsPojo.getCreatedFiles().size();
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
                        this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey(),
                        false, true).getTags();
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
            } catch (HttpClientErrorException httpExc) {
                this.indicizzazioneStepsPojo.setHttpException(httpExc);
            }
        }
    }

    @Then("Il risultato della search contiene le fileKey relative ai seguenti documenti")
    public void checkSearchResult(DataTable dataTable) {
        List<String> searchResult = this.indicizzazioneStepsPojo.getAdditionalFileTagsSearchResponseResponseEntity().getBody().getFileKeys()
                .stream().map(AdditionalFileTagsSearchResponseFileKeys::getFileKey).toList();
        List<String> documentIndexes = dataTable.asList();
        if (documentIndexes.contains("null")) {
            Assertions.assertTrue(searchResult.isEmpty());
        } else {
            List<String> expectedFileKeys = new LinkedList<>();
            documentIndexes.forEach(x -> expectedFileKeys.add(this.indicizzazioneStepsPojo.getCreatedFiles().get(Integer.parseInt(x) - 1).getKey()));
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
        return safeStorageClient.additionalFileTagsGet(indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey()).getTags();
    }

    @Then("L'update massivo va in successo con stato {int}")
    public void checkUpdateMassiveStatusCode(Integer statusCode) {
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity());
        Assertions.assertEquals(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getStatusCodeValue(), statusCode);
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
            indicizzazioneStepsPojo.setAdditionalFileTagsSearchResponseResponseEntity(response);
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
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
            indicizzazioneStepsPojo.setAdditionalFileTagsSearchResponseResponseEntity(response);
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
        }
    }

    @And("La response contiene uno o più errori {string} riportanti la dicitura {string} riguardanti il documento {int}")
    public void checkUpdateMassiveErrors(String errorCode, String errorMessage, Integer documentIndex) {
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity());
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getBody());
        ErrorDetail fileKeyError;
        if (this.indicizzazioneStepsPojo.getFileKeyInesistenti().isEmpty()) {
            String faultyFileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
            fileKeyError = this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getBody().getErrors()
                    .stream().filter(x -> x.getFileKey().contains(faultyFileKey)).findFirst().orElse(null);
        } else {
            fileKeyError = this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getBody().getErrors().get(documentIndex - this.indicizzazioneStepsPojo.getCreatedFiles().size() - 1);
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
        if (indicizzazioneStepsPojo.getHttpException() != null) {
            throw indicizzazioneStepsPojo.getHttpException();
        }
    }

    @After("@aggiuntaTag")
    public void cleanDocuments() {
        this.indicizzazioneStepsPojo.getCreatedFiles().forEach(file -> {
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
}
