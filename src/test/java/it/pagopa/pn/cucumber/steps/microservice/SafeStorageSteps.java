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
import java.util.*;
import java.util.stream.Collectors;

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

    @And("gli si associano {int} valori diversi a un singolo tag")
    public void associateValuesToSingleTag(Integer tagNumber) {
        Integer limitPerRequestUat = 100;
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(0).getKey();
        int iterations = tagNumber / limitPerRequestUat;
        for (int i = 0; i < iterations; i++) {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(i, limitPerRequestUat)));
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
            }
        } catch (HttpClientErrorException httpExc) {
            this.indicizzazioneStepsPojo.setHttpException(httpExc);
        }
    }

    @Then("La chiamata genera un errore con status code {int}")
    public void checkForStatusCode(Integer statusCode) {
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getHttpException());
        Assertions.assertEquals(statusCode,
            this.indicizzazioneStepsPojo.getHttpException().getRawStatusCode());
    }

    @And("Il messaggio di errore riporta la dicitura {string}")
    public void checkForStatusCode(String errorMessage) {
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getHttpException());
        Assertions.assertTrue(this.indicizzazioneStepsPojo.getHttpException().getMessage().contains(errorMessage));
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

    @When("Si modifica il documento {int} associando {int} valori a un singolo tag")
    public void updateDocument(Integer documentIndex, Integer tagNumber) {
        Assertions.assertTrue(documentIndex <= this.indicizzazioneStepsPojo.getCreatedFiles().size());
        String fileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        try {
            this.indicizzazioneStepsPojo.setUpdateSingleResponseEntity(safeStorageClient.additionalFileTagsUpdateWithHttpInfo(
                    fileKey, "pn-test", createUpdateRequest(0, tagNumber)));
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
        Assertions.assertTrue(documentIndex <= this.indicizzazioneStepsPojo.getCreatedFiles().size());
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
            Assertions.assertEquals(searchResult.size(), expectedFileKeys.size());
            searchResult.forEach(x -> Assertions.assertTrue(expectedFileKeys.contains(x)));
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

    @And("La response contiene uno o più errori riportanti la dicitura {string} riguardanti il documento {int}")
    public void checkUpdateMassiveErrors(String errorMessage, Integer documentIndex) {
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity());
        Assertions.assertNotNull(this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getBody());
        String faultyFileKey = this.indicizzazioneStepsPojo.getCreatedFiles().get(documentIndex - 1).getKey();
        ErrorDetail fileKeyError = this.indicizzazioneStepsPojo.getUpdateMassiveResponseEntity().getBody().getErrors()
                .stream().filter(x -> x.getFileKey().contains(faultyFileKey)).findFirst().orElse(null);
        Assertions.assertNotNull(fileKeyError);
        Assertions.assertEquals("400.00", fileKeyError.getResultCode());
        Assertions.assertTrue(fileKeyError.getResultDescription().contains(errorMessage));
    }

    @Then("Il documento {int} è associato alla seguente lista di tag")
    public void getTagsAndGetFiles(Integer documentIndex, List<String> expectedTags) {
        checkDocument(documentIndex, expectedTags);
        getAndCheckFile(documentIndex, expectedTags);
        if (indicizzazioneStepsPojo.getHttpException() != null) {
            throw indicizzazioneStepsPojo.getHttpException();
        }
    }

    @Given("Sul DB non è presente nessun documento con associato il tag {string}")
    public void disassociaTag(String tagString) {
        Map<String, String> tagMap = new HashMap<>();
        String tagName = tagString.split(":")[0];
        String tagValue = tagString.split(":")[1];
        tagMap.put(tagName, tagValue);
        AdditionalFileTagsSearchResponse searchResponse = this.safeStorageClient.additionalFileTagsSearch("or", true, tagMap);
        List<String> fileKeys = searchResponse.getFileKeys().stream().map(AdditionalFileTagsSearchResponseFileKeys::getFileKey).toList();

        if (!fileKeys.isEmpty()) {
            AdditionalFileTagsMassiveUpdateRequest massiveUpdateRequest = new AdditionalFileTagsMassiveUpdateRequest();
            fileKeys.forEach(fk -> {
                Tags newTag = new Tags();
                newTag.setFileKey(fk);
                newTag.putDELETEItem(tagName, List.of(tagValue));
                massiveUpdateRequest.addTagsItem(newTag);
            });

            AdditionalFileTagsMassiveUpdateResponse massiveUpdateResponse = this.safeStorageClient.additionalFileTagsMassiveUpdate(massiveUpdateRequest);
            log.info(massiveUpdateResponse.toString());
        }
    }

    @After("@aggiuntaTag")
    public void cleanDocuments() {
        this.indicizzazioneStepsPojo.getCreatedFiles().forEach(file -> {
            AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
            Map<String, List<String>> tagMap = safeStorageClient.additionalFileTagsGet(file.getKey()).getTags();

            log.info("PRE-CANCELLAZIONE: " + tagMap.toString());
            if (!tagMap.isEmpty()) {
                request.DELETE(tagMap);
                safeStorageClient.additionalFileTagsUpdate(file.getKey(), request);
                log.info("POST-CANCELLAZIONE");
            }
        });
    }
}
