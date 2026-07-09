package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactsIdV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.mapper.impl.PnTimelineAndLegalFactV28;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV28;
import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnParserResponse;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserParameter;
import it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact.PnLegalFactNotificaPresaInCaricoMultiDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implResponse.PnParserLegalFactResponse;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.service.impl.PnParserService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.AAR_GENERATION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Slf4j
public class LegalFactContentVerifySteps {
    private final PnParserService pnParserService;
    private final SharedSteps sharedSteps;
    private final PnTimelineAndLegalFactV28 pnTimelineAndLegalFactV28;
    @Setter
    private String legalFactUrl;
    @Setter
    private String legalFactType;

    @Value("${pn.notification-mario.gherkin.older-10-years}")
    private String notificationIun10years;

    @Value("${pn.legalFact-mario.gherkin.older-10-years}")
    private String legalFactId10years;

    @Autowired
    public LegalFactContentVerifySteps(PnParserService pnParserService, SharedSteps sharedSteps) {
        this.pnParserService = pnParserService;
        this.sharedSteps = sharedSteps;
        /*TODO al rilascio di una nuova versione di timelineElement e LegalFactCategory, creare nuova classe sul modello di quelle esistenti
           e sostituire a questa */
        this.pnTimelineAndLegalFactV28 = new PnTimelineAndLegalFactV28();
    }

    @Then("si verifica se il legalFact è di tipo {string}")
    public void siVerificaSeIlLegalFactEDiTipo(String legalFactType) {
        this.legalFactType = legalFactType;
        byte[] source = B2bUtils.downloadFile(legalFactUrl);
        checkLegalFactType(source, legalFactType);
    }

    @Then("si verifica se il legalFact contiene il campo {string} con value {string}")
    public void siVerificaSeIlLegalFactContieneIlCampoConValue(String legalFactField, String legalFactValue) {
        byte[] source = B2bUtils.downloadFile(legalFactUrl);
        checkLegalFactFieldValue(source, legalFactField, legalFactValue);
    }

    @Then("si verifica se il legalFact contiene i campi per il destinatario")
    public void siVerificaSeIlLegalFactContieneICampiPerIlDestinatario(DataTable dataTable) {
        byte[] source = B2bUtils.downloadFile(legalFactUrl);

        //Creation of a list of map for each dataTable pair
        List<Map<String, String>> listOfMap = dataTable
                .asLists()
                .stream()
                .map(pair -> Map.ofEntries(Map.entry(pair.get(0), pair.get(1))))
                .toList();

        String positionFieldName = Arrays.stream(PnParserParameter.class.getDeclaredFields())
                .filter(field -> field.getType() == int.class)
                .map(Field::getName)
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(positionFieldName);

        Map<String, String> positionField = listOfMap.stream().filter(mappa -> mappa.keySet().stream().anyMatch(key -> key.contains(positionFieldName))).findAny().get();
        Assertions.assertNotNull(positionField);
        int multiDestinatarioPosition = Integer.parseInt(positionField.get(positionFieldName));

        checkLegalFactDestinatario(source, PnDestinatarioAnalogico.mapToDestinatarioAnalogico(listOfMap), multiDestinatarioPosition);

        List<Map<String, String>> listOfMapCleaned = listOfMap.stream()
                .filter(map -> map.keySet().stream().noneMatch(key -> key.contains(IPnParserLegalFact.DESTINATARIO) || key.contains(positionFieldName)))
                .toList();

        if (!listOfMapCleaned.isEmpty()) {
            listOfMapCleaned.forEach(map -> map.forEach((legalFactField, legalFactValue) -> checkLegalFactFieldValue(source, legalFactField, legalFactValue)));
        }
    }

    @Then("si verifica se il legalFact contiene i campi")
    public void siVerificaSeIlLegalFactContieneICampi(DataTable dataTable) {
        byte[] source = B2bUtils.downloadFile(legalFactUrl);

        if (IPnParserLegalFact.LegalFactType.valueOf(legalFactType).equals(IPnParserLegalFact.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO)) {
            //Creation of a list of map for each dataTable pair
            List<Map<String, String>> listOfMap = dataTable
                    .asLists()
                    .stream()
                    .map(pair -> Map.ofEntries(Map.entry(pair.get(0), pair.get(1))))
                    .toList();

            List<Map<String, String>> listOfMapCleaned = listOfMap.stream()
                    .filter(map -> map.keySet().stream().noneMatch(key -> key.contains(IPnParserLegalFact.DESTINATARIO)))
                    .toList();

            listOfMapCleaned.forEach(map -> map.forEach((legalFactField, legalFactValue) -> checkLegalFactFieldValue(source, legalFactField, legalFactValue)));
            checkLegalFactDestinatario(source, PnDestinatarioAnalogico.mapToDestinatarioAnalogico(listOfMap), 0);
        } else {
            dataTable
                    .asMap()
                    .forEach((legalFactField, legalFactValue) -> checkLegalFactFieldValue(source, legalFactField, legalFactValue));
        }
    }

    @Then("si verifica se il legalFact è di tipo {string} e contiene il campo {string} con value {string}")
    public void siVerificaSeIlLegalFactEDiTipoEContieneIlCampoConValue(String legalFactType, String legalFactField, String legalFactValue) {
        this.legalFactType = legalFactType;
        byte[] source = B2bUtils.downloadFile(legalFactUrl);
        checkLegalFactType(source, legalFactType);
        checkLegalFactFieldValue(source, legalFactField, legalFactValue);
    }

    @And("verifica che il file contenga massimo {int} pagine")
    public void siVerificaSeIlLegalFactContieneNPagine(int numPagine) {
        checkPdfPagesFromBytes(numPagine);
    }


    public void checkPdfPagesFromBytes(int numPage) {
        byte[] source = B2bUtils.downloadFile(legalFactUrl);

        PDDocument document = null;

        try {
            document = Loader.loadPDF(source);
            int numberOfPages = document.getNumberOfPages();

            Assertions.assertTrue(numberOfPages <= numPage, "Il PDF contiene più di " + numPage + " pagine!");

        } catch (IOException e) {
            Assertions.fail("Errore durante la lettura del PDF: " + e.getMessage());
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkLegalFactType(byte[] source, String legalFactType) {
        PnParserParameter pnParserParameter = PnParserParameter.builder()
                .legalFactType(IPnParserLegalFact.LegalFactType.valueOf(legalFactType))
                .legalFactField(IPnParserLegalFact.LegalFactField.valueOf(IPnParserLegalFact.LegalFactField.TITLE.name()))
                .build();
        PnParserLegalFactResponse pnParserLegalFactResponse = (PnParserLegalFactResponse) parseLegalFact(source, pnParserParameter, false);
        assertLegalFactParserResponse(pnParserLegalFactResponse);
        assertLegalFactType(pnParserLegalFactResponse, legalFactType);
    }

    private void checkLegalFactFieldValue(byte[] source, String legalFactField, String legalFactValue) {
        PnParserParameter pnParserParameter = PnParserParameter.builder()
                .legalFactType(IPnParserLegalFact.LegalFactType.valueOf(legalFactType))
                .legalFactField(IPnParserLegalFact.LegalFactField.valueOf(legalFactField))
                .build();
        PnParserLegalFactResponse pnParserLegalFactResponse = (PnParserLegalFactResponse) parseLegalFact(source, pnParserParameter, false);
        assertLegalFactParserResponse(pnParserLegalFactResponse);
        assertLegalFactFieldValue(pnParserLegalFactResponse, legalFactField, legalFactValue);
    }

    private void checkLegalFactDestinatario(byte[] source, List<PnDestinatarioAnalogico> destinatarioAnalogicoList, int multiDestinatarioPosition) {
        PnParserParameter pnParserParameter;
        PnParserLegalFactResponse pnParserLegalFactResponse;
        boolean isAllField;
        if (multiDestinatarioPosition == 0) {
            pnParserParameter = PnParserParameter.builder()
                    .legalFactType(IPnParserLegalFact.LegalFactType.valueOf(legalFactType))
                    .build();
            isAllField = true;
        } else {
            pnParserParameter = PnParserParameter.builder()
                    .legalFactType(IPnParserLegalFact.LegalFactType.valueOf(legalFactType))
                    .multiDestinatarioPosition(multiDestinatarioPosition)
                    .build();
            isAllField = false;
        }
        pnParserLegalFactResponse = (PnParserLegalFactResponse) parseLegalFact(source, pnParserParameter, isAllField);
        assertLegalFactParserResponse(pnParserLegalFactResponse);
        assertLegalFactDestinatario(pnParserLegalFactResponse, destinatarioAnalogicoList, multiDestinatarioPosition);
    }

    private IPnParserResponse parseLegalFact(byte[] source, PnParserParameter pnParserParameter, boolean isAllField) {
        if (isAllField) {
            return pnParserService.extractAllField(source, pnParserParameter);
        } else {
            return pnParserService.extractSingleField(source, pnParserParameter);
        }
    }

    private void assertLegalFactParserResponse(PnParserLegalFactResponse pnParserLegalFactResponse) {
        assertThat(pnParserLegalFactResponse).as("La PnParserLegalFactResponse non dev'essere null").isNotNull();
        assertThat(pnParserLegalFactResponse.getResponse().getPnLegalFact())
                .as("Il LegalFact della PnParserLegalFactResponse non dev'essere null")
                .isNotNull();
        log.info("PN_LEGAL_FACT:\n {}", pnParserLegalFactResponse.getResponse().getPnLegalFact());
    }

    private void assertLegalFactType(PnParserLegalFactResponse pnParserLegalFactResponse, String legalFactType) {
        String actual = pnParserLegalFactResponse.getResponse().getField().replace("ﬁ", "fi");
        String expected = IPnParserLegalFact.LegalFactTypeTitle.getTitleByType(IPnParserLegalFact.LegalFactType.valueOf(legalFactType));
        assertThat(actual).as("Il title del del LegalFact non coincide col valore atteso").isEqualTo(expected);
    }

    private void assertLegalFactFieldValue(PnParserLegalFactResponse pnParserLegalFactResponse, String legalFactField, String legalFactValue) {
        assertThat(pnParserLegalFactResponse.getResponse().getField())
                .as("Il campo " + legalFactField + " non dev'essere null")
                .isNotNull();
        assertThat(pnParserLegalFactResponse.getResponse().getField().replace("ﬁ", "fi"))
                .as("Il campo (ripulito dall'eventuale legatura tipografica) non coincide col valore atteso")
                .isEqualTo(legalFactValue);
        String actual = pnParserLegalFactResponse.getResponse().getPnLegalFact().getAllLegalFactValues().fieldValue().get(IPnParserLegalFact.LegalFactField.valueOf(legalFactField)).replace("ﬁ", "fi");
        assertThat(actual).as("Il campo " + legalFactField + " non coincide col valore atteso").isEqualTo(legalFactValue);
    }

    private void assertLegalFactDestinatario(PnParserLegalFactResponse pnParserLegalFactResponse, List<PnDestinatarioAnalogico> destinatarioAnalogicoList, int multiDestinatarioPosition) {
        assertLegalFactParserResponse(pnParserLegalFactResponse);
        PnLegalFactNotificaPresaInCaricoMultiDestinatario pnLegalFactNotificaPresaInCaricoMultiDestinatario = (PnLegalFactNotificaPresaInCaricoMultiDestinatario) pnParserLegalFactResponse.getResponse().getPnLegalFact();

        try {
            if (multiDestinatarioPosition == 0) {

                assertThat(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici().size())
                        .as("La size dei destinatari non coincide con quanto atteso")
                        .isEqualTo(destinatarioAnalogicoList.size());
                assertThat(destinatarioAnalogicoList).asList()
                        .as("La lista dei destinatari (expected) non contiene tutti i destinatari della response (actual)")
                        .containsAll(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici());
                assertThat(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici()).asList()
                        .as("La lista dei destinatari della response (actual) non contiene tutti i destinatari (expected)")
                        .containsAll(destinatarioAnalogicoList);
            } else {
                assertThat(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici().indexOf(destinatarioAnalogicoList.get(0)) + 1)
                        .as("L'indice del destinatario non coincide con quanto atteso")
                        .isEqualTo(multiDestinatarioPosition);
                assertThat(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici().get(multiDestinatarioPosition - 1))
                        .as("Il destinatario non coincide con quanto atteso")
                        .isEqualTo(destinatarioAnalogicoList.get(0));
                assertThat(pnLegalFactNotificaPresaInCaricoMultiDestinatario.getDestinatariAnalogici()).asList()
                        .as("La lista dei destinatari della response (actual) non contiene tutti i destinatari (expected)")
                        .containsAll(destinatarioAnalogicoList);
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("l'utente {string} recupera i legalFacts richiamando l'api versione {int} e tra questi {string} il legalFact con categoria {string}")
    public void downloadLegalFactWithIdUsingApiVersion(String user, Integer version, String presente, String legalFactCategory) {
        sharedSteps.selectUser(user);
        String iun = this.sharedSteps.getNotificationIun();
        boolean isPresent = presente.equalsIgnoreCase("COMPARE");
        Assertions.assertNotNull(this.legalFactType);
        Assertions.assertNotNull(this.legalFactUrl);
        Assertions.assertEquals(legalFactCategory, this.legalFactType);
        switch (version) {
            case 1 -> sharedSteps.getWebRecipientClient().downloadLegalFactById(iun, this.legalFactUrl, null);
            case 20 -> {
                List<BffLegalFactId> legalFactV20list = sharedSteps.getWebRecipientClient().getLegalFactsV20(iun, null);
                Assertions.assertNotNull(legalFactV20list);
                BffLegalFactId target = legalFactV20list.stream().filter(
                        x -> x.getCategory().getValue().equals(legalFactCategory)).findFirst().orElse(null);
                if (isPresent) {
                    Assertions.assertNotNull(target);
                } else {
                    Assertions.assertNull(target);
                }
            }
            default -> throw new IllegalArgumentException("Valore di versione non riconosciuto: " + version);
        }
    }


    //MATTEO: Tutto il codice sottostante è stato spostato da AvanzamentoNotificheB2bSteps, dove non c'entrava nulla

    @Then("la PA richiede il download dell'attestazione opponibile PEC_RECEIPT")
    public void paRequiresDownloadOfLegalFactPecRecipient() {
        downloadLegalFactPecRecipient("PEC_RECEIPT", true, false, false, null);
    }

    @Then("{string} richiede il download dell'attestazione opponibile PEC_RECEIPT")
    public void userDownloadLegalFactPecRecipient(String user) {
        sharedSteps.selectUser(user);
        downloadLegalFactPecRecipient("PEC_RECEIPT", false, false, true, null);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string} senza legalFactType")
    public void paRequiresDownloadOfLegalFactId(String legalFactCategory) {
        downloadLegalFactId(legalFactCategory, true, false, false, null);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string}")
    public void paRequiresDownloadOfLegalFact(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, null);
        setLegalFactUrl(legalFactUrl);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string} con deliveryDetailCode {string}")
    public void paRequiresDownloadOfLegalFactWithDeliveryDetailCode(String legalFactCategory, String deliveryDetailCode) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, deliveryDetailCode);
        setLegalFactUrl(legalFactUrl);
    }

    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
    public void appIODownloadLegalFact(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, true, false, null);
        setLegalFactUrl(legalFactUrl);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string}")
    public void userDownloadLegalFact(String user, String legalFactCategory) {
        sharedSteps.selectUser(user);
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
        setLegalFactUrl(legalFactUrl);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string} con errore {string}")
    public void userDownloadLegalFactError(String user, String legalFactCategory, String statusCode) {
        try {
            sharedSteps.selectUser(user);
            String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
            setLegalFactUrl(legalFactUrl);
        } catch (AssertionFailedError assertionFailedError) {
            Assertions.assertEquals(statusCode, assertionFailedError.getCause().getMessage().substring(0, 3));
        }
    }

    @And("ricerca ed effettua download del legalFact con la categoria {string}")
    public void ricercaEdEffettuaDownloadDelLegalFactConLaCategoria(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
        setLegalFactUrl(legalFactUrl);
    }

    @And("ricerca ed effettua download del legalFact con la categoria {string} con DetailCode {string}")
    public void ricercaEdEffettuaDownloadDelLegalFactConLaCategoria(String legalFactCategory, String deliveryDetailCode) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, deliveryDetailCode);
        setLegalFactUrl(legalFactUrl);
    }

    @Then("tra gli elementi di timeline con categoria {string} è presente un legalFact con categoria {string}")
    public void checkLegalFactAllVersions(String timelineElementCategory, String legalFactCategory) {
        List<LegalFactsIdV20> legalFactsList = this.sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(
                x -> x.getCategory().getValue().equals(timelineElementCategory)).findFirst().orElse(null).getLegalFactsIds();
        Assertions.assertFalse(legalFactsList.isEmpty());
        LegalFactsIdV20 legalFact = legalFactsList.stream().filter(x -> x.getCategory().equals(legalFactCategory)).findFirst().orElse(null);
        Assertions.assertNotNull(legalFact);
        setLegalFactType(legalFactCategory);
        setLegalFactUrl(legalFact.getKey());
        log.info("LEGAL FACT CATEGORY = " + legalFact.getCategory());
        log.info("LEGAL FACT URL: " + legalFact.getKey());
    }

    @Then("viene verificato che la chiave dell'attestazione opponibile {string} è {string}")
    public void verifiedThatTheKeyOfTheLegalFactIs(String legalFactCategory, String key) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        PnTimelineLegalFactV28 categories = pnTimelineAndLegalFactV28.getCategory(legalFactCategory);
        TimelineElementV28 timelineElement = sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(elem ->
                        elem.getCategory().getValue().equals(categories.getTimelineElementInternalCategory().getValue()))
                .findAny()
                .orElse(null);

        try {
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertEquals(categories.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            Assertions.assertTrue(timelineElement.getLegalFactsIds().get(0).getKey().contains(key));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("l'ente {string} richiede l'attestazione opponibile {string}")
    public void paRequiresLegalFact(String paName, String legalFactCategory) {
        sharedSteps.setPA(paName);
        try {
            takeLegalFact(legalFactCategory, null);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("l'ente {string} richiede l'attestazione opponibile {string} con deliveryDetailCode {string}")
    public void paRequiresLegalFactConDeliveryDetailCode(String paName, String legalFactCategory, String deliveryDetailCode) {
        sharedSteps.setPA(paName);
        try {
            takeLegalFact(legalFactCategory, deliveryDetailCode);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è {string}")
    public void downloadAttestazioneOpponibileAAREControlloDelContenutoDelFilePerVerificareSeIlTipoE(String aarType) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = getLegalFactIdAAR("PN_AAR");
        byte[] source = B2bUtils.downloadFile(legalFactDownloadMetadataResponse.getUrl());
        Assertions.assertNotNull(source);
        Assertions.assertTrue(checkTypeAAR(source, aarType));
    }

    @And("download attestazione opponibile AAR")
    public void downloadLegalFactIdAAR() {
        getLegalFactIdAAR("PN_AAR");
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType) {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = new AtomicReference<>();
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementV28 timelineElement = null;
        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (Objects.requireNonNull(element.getCategory().getValue()).equals(AAR_GENERATION)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
        String keySearch = null;
        if (!Objects.requireNonNull(timelineElement.getDetails()).getGeneratedAarUrl().isEmpty()) {

            if (timelineElement.getDetails().getGeneratedAarUrl().contains(aarType)) {
                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf(aarType));
            }

            String finalKeySearch = keySearch;
            try {
                Assertions.assertDoesNotThrow(() -> legalFactDownloadMetadataResponse.set(
                        sharedSteps.getB2bClient().getDownloadLegalFact(sharedSteps.getNotificationIun(), finalKeySearch)));
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
            }
        }
        return legalFactDownloadMetadataResponse.get();
    }

    private boolean checkTypeAAR(byte[] source, String aarType) {
        Pattern pattern = Pattern.compile("\\((CAF)\\s");
        try (final PDDocument document = Loader.loadPDF(source)) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            String extractedText = pdfStripper.getText(document);
            Matcher matcher = pattern.matcher(extractedText);
            if (aarType.equals("AAR")) {  //if AAR then check ' CAF ' pattern NOT exist
                return !matcher.find();
            } else if (aarType.equals("AAR RADD")) { //if AAR RADD then check ' CAF ' pattern exist
                return matcher.find();
            }
        } catch (Exception exception) {
            log.error("Error parsing PDF {}", exception);
        }
        return false;
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse takeLegalFact(String legalFactCategory, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
        PnTimelineLegalFactV28 categories = pnTimelineAndLegalFactV28.getCategory(legalFactCategory);
        TimelineElementV28 timelineElement = null;

        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (!Objects.equals(element.getCategory(), categories.getTimelineElementInternalCategory())) {
                continue;
            }

            if (deliveryDetailCode == null ||
                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
                timelineElement = element;
                break;
            }
        }

        log.info("TIMELINE ELEMENT : {}", timelineElement);
        Assertions.assertNotNull(timelineElement);

        Assertions.assertNotNull(timelineElement.getLegalFactsIds());
        Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
        Assertions.assertEquals(categories.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
        LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
        String key = timelineElement.getLegalFactsIds().get(0).getKey();
        String keySearch = getKeyLegalFact(key);


        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
                sharedSteps.getB2bClient().getLegalFact(sharedSteps.getNotificationIun(), categorySearch, keySearch);

        Assertions.assertNotNull(legalFactDownloadMetadataResponse);

        return legalFactDownloadMetadataResponse;
    }

    private String downloadLegalFact(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
        PnTimelineLegalFactV28 categories = pnTimelineAndLegalFactV28.getCategory(legalFactCategory);
        TimelineElementV28 timelineElement = null;

        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (!Objects.equals(element.getCategory(), categories.getTimelineElementInternalCategory())) {
                continue;
            }

            if (deliveryDetailCode == null) {
                timelineElement = element;
                break;
            }

            if (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode)) {
                timelineElement = element;
                break;
            }
        }

        try {
            log.info("TIMELINE ELEMENT : {}", timelineElement);
            Assertions.assertNotNull(timelineElement);

            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(categories.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String finalKeySearch = getKeyLegalFact(key);

            if (pa) {
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = Assertions.assertDoesNotThrow(() ->
                        sharedSteps.getB2bClient().getLegalFact(sharedSteps.getNotificationIun(), categorySearch, finalKeySearch));
                return legalFactDownloadMetadataResponse.getUrl();
            }
//            if (appIO) {
//                 Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
//                  sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
//            }
            if (webRecipient) {
                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
                        Assertions.assertDoesNotThrow(() ->
                                sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
                                        sharedSteps.deepCopy(categorySearch,
                                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                                        finalKeySearch
                                ));
                System.out.println("NOME FILE PEC RECIPIENT DEST" + legalFactDownloadMetadataResponse.getFilename());
                return legalFactDownloadMetadataResponse.getUrl();
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
        return null;
    }

    private void downloadLegalFactId(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
        PnTimelineLegalFactV28 categories = pnTimelineAndLegalFactV28.getCategory(legalFactCategory);
        TimelineElementV28 timelineElement = null;

        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (!Objects.equals(element.getCategory(), categories.getTimelineElementInternalCategory())) {
                continue;
            }

            if (deliveryDetailCode == null ||
                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
                timelineElement = element;
                break;
            }
        }

        try {
            log.info("TIMELINE ELEMENT : {}", timelineElement);
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(categories.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String finalKeySearch = getKeyLegalFact(key);

            if (pa) {
                Assertions.assertDoesNotThrow(() -> sharedSteps.getB2bClient().getDownloadLegalFact(sharedSteps.getNotificationIun(), finalKeySearch));
            }
//            if (appIO) {
//                 Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
//                        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
//            }
            if (webRecipient) {
                Assertions.assertDoesNotThrow(() -> sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                        finalKeySearch
                ));
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private void downloadLegalFactPecRecipient(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementV28 timelineElement = null;

        TimelineElementCategoryV28 timelineElementInternalCategory = TimelineElementCategoryV28.SEND_DIGITAL_PROGRESS;
        LegalFactCategory category = LegalFactCategory.PEC_RECEIPT;

        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (!Objects.equals(element.getCategory(), timelineElementInternalCategory)) {
                continue;
            }

            if (deliveryDetailCode == null ||
                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
                timelineElement = element;
                break;
            }
        }

        try {
            log.info("TIMELINE ELEMENT : {}", timelineElement);
            Assertions.assertNotNull(timelineElement);

            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(category.getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String keySearch = null;
            //TODO Verificare....
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            } else if (key.contains("PN_PRINTED")) {
                keySearch = key.substring(key.indexOf("PN_PRINTED"));
            } else if (key.contains("PN_F24")) {
                keySearch = key.substring(key.indexOf("PN_F24"));
            }

            String finalKeySearch = keySearch;
            if (pa) {
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
                        sharedSteps.getB2bClient().getLegalFact(sharedSteps.getNotificationIun(), categorySearch, finalKeySearch);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
            }

            if (webRecipient) {
                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
                        sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
                                sharedSteps.deepCopy(categorySearch,
                                        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                                finalKeySearch);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private String getKeyLegalFact(String key) {
        if (key.contains("PN_LEGAL_FACTS")) {
            return key.substring(key.indexOf("PN_LEGAL_FACTS"));
        } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
            return key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
        } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
            return key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
        } else if (key.contains("PN_PRINTED")) {
            return key.substring(key.indexOf("PN_PRINTED"));
        } else if (key.contains("PN_F24")) {
            return key.substring(key.indexOf("PN_F24"));
        }
        return null;
    }

    /**
     * Verifica che per un legalFact rimosso da SS dopo 10 anni, provando a recuperarlo tramite api-pubblica venga lanciato un 500, tramite api privata un 410.
     * Il test utilizza notifiche fisse a cui sono stati impostati i seguenti valori per simulare la rimozione da ss:
     * "documentLogicalState": "DELETED"
     * "documentState": "deleted"
     * Tali notifiche sono tutte state inviate da Comune_Multi a Mario Gherkin, ragion per cui i valori di pa e recipientInternalId sono impostati fissi nelle properties.
     */
    @Given("verifico che recuperando un legalFact rimosso da safeStorage, le api restituiscano l'errore corretto")
    public void checkLegalFactRemovedFromSafeStorage() {
        sharedSteps.setPA("Comune_Multi");
        String recipientInternalId = "PF-a6c1350d-1d69-4209-8bf8-31de58c79d6e";
        try {
            sharedSteps.getB2bClient().getLegalFact(notificationIun10years, LegalFactCategory.DIGITAL_DELIVERY, legalFactId10years);
        } catch (HttpStatusCodeException excApiPubblica) {
            log.info(excApiPubblica.getMessage());
            assertThat(excApiPubblica.getRawStatusCode()).as("La chiamata ad api pubblica deve restituire un 500").isEqualTo(500);
        }
        try {
            sharedSteps.getB2bClient().getLegalFactByIdPrivate(recipientInternalId, notificationIun10years, legalFactId10years, null, null, null);
        } catch (HttpStatusCodeException excApiPrivata) {
            log.info(excApiPrivata.getMessage());
            assertThat(excApiPrivata.getRawStatusCode()).as("La chiamata ad api privata deve restituire un 410").isEqualTo(410);
        }
    }
}