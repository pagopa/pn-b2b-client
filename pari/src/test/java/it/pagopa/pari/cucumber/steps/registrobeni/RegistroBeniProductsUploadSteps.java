package it.pagopa.pari.cucumber.steps.registrobeni;

import com.opencsv.CSVWriter;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.cucumber.utils.SharedCommonContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.CsvDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.ProductDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.RegisterUploadResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadsListDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RegistroBeniProductsUploadSteps {
    private final ApiClientContext apiClientContext;
    private final ResourceLoader resourceLoader;
    private final SharedCommonContext sharedCommonContext;

    private RegisterUploadResponseDTO uploadResponseDTO;
    private CsvDTO csvDTO;
    private UploadsListDTO uploadsListDTO;
    private UploadDTO lastUpload;

    public RegistroBeniProductsUploadSteps(ApiClientContext apiClientContext, ResourceLoader resourceLoader,
                                           SharedCommonContext sharedCommonContext) {
        this.apiClientContext = apiClientContext;
        this.resourceLoader = resourceLoader;
        this.sharedCommonContext = sharedCommonContext;
    }

    @When("viene caricato un file NON csv con categoria: {string} e dati:")
    public void generateCsvWithWrongExtension(String categoria, List<Map<String, String>> dataCsv) throws Exception {
        Resource notCsvFile = generaCsv(dataCsv, ".txt");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(notCsvFile, categoria);
    }

    @When("viene caricato il csv con categoria: {string} e dati:")
    public void vieneGeneratoIlCsv(String categoria, List<Map<String, String>> dataCsv) throws Exception {
        sharedCommonContext.setCategory(categoria);
        Resource csvFile = generaCsv(dataCsv, ".csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(csvFile, categoria);
        sharedCommonContext.setLastProductsUploaded(dataCsv.stream()
                .map(row -> new ProductDTO().eprelCode(row.get("Codice EPREL")).gtinCode(row.get("Codice GTIN/EAN"))
                        .productCode(row.get("Codice Prodotto"))
                        .category(Optional.ofNullable(row.get("Categoria")).map(ProductDTO.CategoryEnum::fromValue).orElse(null))
                        .countryOfProduction(row.get("Paese di Produzione"))).toList()
        );

        // Viene aggiunto un delay per dare il tempo al csv di essere validato
        Thread.sleep(5000);
    }

    @When("viene caricato di nuovo lo stesso prodotto")
    public void uploadSameProducts() throws Exception {
        List<ProductDTO> productDTOList = sharedCommonContext.getLastProductsUploaded();
        Resource csvFile = generaCsv(createProductMap(productDTOList), ".csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(csvFile, sharedCommonContext.getCategory());
        // Viene aggiunto un delay per dare il tempo al csv di essere validato
        Thread.sleep(5000);

    }

    @When("viene verificato il csv con categoria: {string} e dati:")
    public void verifyCsv(String category, List<Map<String, String>> dataCsv) throws Exception {
        Resource csvFile = generaCsv(dataCsv, ".csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().verifyProductList(csvFile, category);
    }

    @When("viene recuperato il report di errore appena generato")
    public void retrieveErrorReport() {
        assertNotNull(uploadResponseDTO);
        assertNotNull(uploadResponseDTO.getProductFileId());
        csvDTO = apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(uploadResponseDTO.getProductFileId());
    }

    @And("si verifica che il report dell'ultimo prodotto aggiunto contenga la descrizione: {string}")
    public void retrieveLastReport(String expectedValidationError) {
        UploadsListDTO uploadList = apiClientContext.getRegisterPortalOperationClient().getProductFilesList(0, 10);
        Assertions.assertNotNull(uploadList);
        csvDTO = apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(uploadList.getContent().get(0).getProductFileId());
        Assertions.assertTrue(csvDTO.getData().contains(expectedValidationError));
    }

    @Then("il report è correttamente popolato")
    public void verifyReportSuccess() {
        assertNotNull(csvDTO);
        assertTrue(StringUtils.isNotBlank(csvDTO.getData()));
    }

    @When("si tenta di recuperare un report di errore {string} e si ottiene status code {int}")
    public void verifyReportError(String productFileId, int expectedStatusCode) {
        HttpStatus httpStatus = null;
        try {
            apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(productFileId);
        } catch (HttpStatusCodeException e) {
            httpStatus = e.getStatusCode();
        }
        assertEquals(HttpStatus.valueOf(expectedStatusCode), httpStatus);
    }

    private Resource generaCsv(List<Map<String, String>> tableRow, String suffix) throws Exception {
        Path tempPath = Files.createTempFile("products-", suffix);
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit();

        Set<String> header = tableRow.get(0).keySet();

        try (FileWriter fileWriter = new FileWriter(tempFile);
             CSVWriter csvWriter = new CSVWriter(fileWriter, ';',
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {
            csvWriter.writeNext(header.toArray(new String[0]));

            for (Map<String, String> row : tableRow) {
                String[] values = header.stream()
                        .map(h -> row.getOrDefault(h, ""))
                        .toArray(String[]::new);
                csvWriter.writeNext(values);
            }

        }
        return new FileSystemResource(tempFile);
    }

    @Then("si verifica che la risposta abbia:")
    public void verifyResponse(DataTable dataTable) {
        Map<String, String> expectedResults = dataTable.asMap();
        verifyErrorKey(expectedResults.get("errorKey"));
        assertEquals(expectedResults.get("status"), Optional.ofNullable(uploadResponseDTO.getStatus()).map(RegisterUploadResponseDTO.StatusEnum::getValue).orElse(null), "Mismatch on status field!");
        verifyProductFileId(expectedResults.get("productFileId"));
    }

    private void verifyProductFileId(String expectedValue) {
        if ("NOT_NULL".equals(expectedValue)) {
            assertNotNull(uploadResponseDTO.getProductFileId());
            assertFalse(uploadResponseDTO.getProductFileId().isEmpty());
        }
        else if ("NULL".equals(expectedValue)) {
            assertNull(uploadResponseDTO.getProductFileId());
        } else {
            assertEquals(expectedValue, uploadResponseDTO.getProductFileId(), "Mismatch on productFileId!");
        }
    }

    private void verifyErrorKey(String expectedValue) {
        if ("NULL".equals(expectedValue)) {
            assertNull(uploadResponseDTO.getErrorKey());
        }
        else {
            assertEquals(expectedValue, Optional.ofNullable(uploadResponseDTO.getErrorKey()).map(RegisterUploadResponseDTO.ErrorKeyEnum::getValue).orElse(null), "Mismatch on errorKey!");
        }
    }

    @Given("viene caricato un file csv di peso maggiore a quello consentito")
    public void uploadLargeCSV() {
        Resource csvFile = resourceLoader.getResource("file:src/main/resources/registroBeni/large-csv-up-two-mb.csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(csvFile, "WASHINGMACHINES");
    }

    @Given("viene caricato un file csv contente più righe di quelle accettate")
    public void uploadLongCSV() {
        Resource csvFile = resourceLoader.getResource("file:src/main/resources/registroBeni/long-csv-with-101-row.csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(csvFile, "WASHINGMACHINES");

    }

    @When("si recupera l'ultimo caricamento effettuato dall'utenza")
    public void retrieveLastUpload() {
        uploadsListDTO = apiClientContext.getRegisterPortalOperationClient().getProductFilesList(0, 10);
        lastUpload = uploadsListDTO.getContent().stream()
                .filter(x -> StringUtils.isNotBlank(x.getDateUpload()))
                .max(Comparator.comparing(UploadDTO::getDateUpload))
                .orElse(null);
    }

    @Then("si verifica che i prodotti non siano stati aggiunti in quanto già caricati da un produttore diverso")
    public void verifyProductsAreNotLoaded() {
        assertNotNull(lastUpload);
        csvDTO = apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(lastUpload.getProductFileId());
        assertNotNull(csvDTO);
        assertNotNull(csvDTO.getData());
        assertTrue(csvDTO.getData().contains("Il prodotto indicato è associato ad un altro produttore"));
    }

    @Then("si verifica che nella lista dei caricamenti ne sia stato aggiunto uno nuovo")
    public void verifyUploadsListResponse() {
        uploadsListDTO = apiClientContext.getRegisterPortalOperationClient().getProductFilesList(0, 10);
        assertNotNull(uploadsListDTO);
        assertNotNull(uploadsListDTO.getTotalElements());
        assertTrue(uploadsListDTO.getTotalElements() > 0);
        List<String> timestamp = new ArrayList<>();
        assertTrue(
                uploadsListDTO.getContent().stream()
                        .anyMatch(x -> {
                            LocalDateTime uploadTime = LocalDateTime.parse(x.getDateUpload());
                            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
                            Duration diff = Duration.between(uploadTime, now);
                            timestamp.addAll(Arrays.asList(now.toString(), diff.toString()));
                            return diff.toMinutes() < 1;
                        })
        );
    }

//    @Then("viene aggiunto di nuovo un prodotto già rifiutato")
//    public void uploadProductAlreadyRejected() throws Exception {
//        assertNotNull(sharedCommonContext.getLastProductsUploaded());
//        assertFalse(sharedCommonContext.getLastProductsUploaded().isEmpty());

//        ProductDTO rejectedProduct = sharedCommonContext.getProductDTO().get(0);
//        Map<String, String> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("Codice EPREL", rejectedProduct.getEprelCode());
//        linkedHashMap.put("Codice GTIN/EAN", rejectedProduct.getGtinCode());
//        linkedHashMap.put("Codice Prodotto", rejectedProduct.getProductCode());
//        linkedHashMap.put("Categoria", rejectedProduct.getCategory().getValue());
//        linkedHashMap.put("Paese di Produzione", rejectedProduct.getCountryOfProduction());

//        Resource csvFile = generaCsv(createProductMap(sharedCommonContext.getLastProductsUploaded()),".csv");
//        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(csvFile, ProductCategory.getEnglishCategory(sharedCommonContext.getCategory()));
//    }

    private List<Map<String, String>> createProductMap(List<ProductDTO> productDTOList) {
        List<Map<String, String>> result = new ArrayList<>();
        for (ProductDTO productDTO : productDTOList) {
            Map<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("Codice EPREL", productDTO.getEprelCode());
            linkedHashMap.put("Codice GTIN/EAN", productDTO.getGtinCode());
            linkedHashMap.put("Codice Prodotto", productDTO.getProductCode());
            linkedHashMap.put("Categoria", productDTO.getCategory().getValue());
            linkedHashMap.put("Paese di Produzione", productDTO.getCountryOfProduction());
            result.add(linkedHashMap);
        }
        return result;

    }


}
