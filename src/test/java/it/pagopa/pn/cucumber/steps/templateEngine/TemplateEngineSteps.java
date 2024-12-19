package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class TemplateEngineSteps {

    private static final String BODY_CORRETTO = "CORRETTO";

    private final Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy;
    private final Map<TemplateType, List<String>> templateEngineObjectFields;
    private final TemplateEngineContextFactory contextFactory;
    private TemplateEngineResult result;

    private HttpClientErrorException templateFileException;
    private HttpServerErrorException templateServerException;
    private List<HttpStatusCodeException> templateFileExceptions = new ArrayList<>();

    public TemplateEngineSteps(Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy,
                               TemplateEngineContextFactory contextFactory, Map<TemplateType, List<String>> templateEngineObjectFields) {
        this.templateEngineStrategy = templateEngineStrategy;
        this.contextFactory = contextFactory;
        this.templateEngineObjectFields = templateEngineObjectFields;
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string} con il body {string}")
    public void recuperoIlTemplatePerInLinguaConIlBody(String templateType, String language, String body) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, body, "semplice", new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} di tipo {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String notificationType, String language) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, notificationType, new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String language) {
        recuperoIlTemplatePerInLingua(templateType, "semplice", language);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body:")
    public void recuperoIlTemplateConIValoriNelRequestBody(String templateType, Map<String, String> parameters) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, "semplice", parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body errati")
    public void recuperoIlTemplateConIValoriNelRequestBodyErrati(String templateType) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        templateEngineObjectFields.get(templateTypeObject)
                .forEach(data -> {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put(data, "null");
                    retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, "semplice", parameters);
                });
    }

    private void retrieveTemplate(TemplateType templateType, String language, String body, String notificationTpe, Map<String, String> parameters) {
        try {
            TemplateRequestContext context = contextFactory.createContext(parameters, notificationTpe);
            result = templateEngineStrategy.get(templateType).retrieveTemplate(language, body.equals(BODY_CORRETTO), context);
        } catch ( HttpClientErrorException e) {
            templateFileException = e;
            templateFileExceptions.add(e);
        } catch (HttpServerErrorException e) {
            templateServerException = e;
            templateFileExceptions.add(e);
        }
    }

    @Then("verifico che il template è in formato {string}")
    public void verificoCheIlTemplateInFormato(String extentionFile) {
        Assertions.assertNotNull(result);
        if(extentionFile.equals(".pdf")) {
            Assertions.assertNotNull(result.getTemplateFileReturned());
            Assertions.assertTrue(isValidPdf(result.getTemplateFileReturned()));
        } else if (extentionFile.equals("html")){
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertTrue(result.getTemplateHtmlReturned().contains("<html"));
        } else if (extentionFile.equals("text")) {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertFalse(result.getTemplateHtmlReturned().contains("<html"));
        }
    }

    public boolean isValidPdf(Resource resource) {
        try {
            byte[] input = ((ByteArrayResource) resource).getByteArray();
            RandomAccessReadBuffer buffer = new RandomAccessReadBuffer(input);
            PDFParser parser = new PDFParser(buffer);
            PDDocument doc = parser.parse();
            try (PDDocument document = new PDDocument(doc.getDocument())) {
                PDFTextStripper textStripper = new PDFTextStripper();
                String retrievedText = textStripper.getText(document);
                result.setFileTextRetrieved(retrievedText);
                return retrievedText != null;
            }
        } catch (IOException | ClassCastException e) {
            return false;
        }
    }

    @Then("verifico che (tutte le chiamate)(la chiamata) (sia)(siano) (andata)(andate) in {string} error(.)( e che nessuna abbia ricevuto una risposta)")
    public void verificoCheLaChiamataSiaAndataInError(String errorCode) {
        Assertions.assertNull(result);
        if (errorCode.equals("400")) {
            Assertions.assertNotNull(templateFileException);
            Assertions.assertNotNull(templateFileExceptions);
            Assertions.assertEquals(errorCode, String.valueOf(templateFileException.getRawStatusCode()));
        } else if (errorCode.equals("500")) {
            Assertions.assertNotNull(templateServerException);
        } else throw new IllegalArgumentException("no error map on the test system.");
        templateFileExceptions.forEach(data -> Assertions.assertEquals(errorCode, String.valueOf(data.getRawStatusCode())));
    }

    private void additionalCheck(String notificationType) {

    }

    private int countOccurrences(String searchString) {
        return (int) Pattern.compile(Pattern.quote(searchString))
                .splitAsStream(result.retrieveFormattedText())
                .count() - 1;
    }

    private String getTextToRetrieve(String language, TemplateType templateType) {
        return templateEngineStrategy.get(templateType).getTextToCheckLanguage(language);
    }

    @And("controllo che nel file {string} contenga il (campo)(testo) {string} valorizzato (a)(con) {string}")
    public void controlloCheNelFileContengaIlCampoValirizzatoA(String fileType, String field, String fieldValue) {
        if (fileType.equals("pdf")) {
            Assertions.assertNotNull(result.getFileTextRetrieved());
            if (field.equals("finale")) {
                Assertions.assertTrue(result.retrieveFormattedText().endsWith(fieldValue));
            } else if (field.equals("delegato")) {
                Assertions.assertTrue(result.retrieveFormattedText().contains("il " + fieldValue + " ha avuto accesso ai documenti informatici oggetto di notifica"));
            } else {
                Assertions.assertTrue(result.retrieveFormattedText().contains(fieldValue + field), "il PDF non contiene il campo: " + field + ", valorizzato a " + fieldValue);
            }
        } else {
            throw new IllegalArgumentException("no valid file to check");
        }
    }

    @Then("verifico che il template è in formato {string} in lingua {string}")
    public void verificoCheIlTemplateÈInFormatoInLingua(String fileType, String language) {
        controlloChePerIlTemplateIlFilePerUnaNotificaIlTestoSiaInLingua("", fileType, language);
    }

    @And("controllo che per il template {string} il file {string} sia in lingua {string}")
    public void controlloChePerIlTemplateIlFilePerUnaNotificaIlTestoSiaInLingua(String templateType, String fileType, String languange) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        String textToFind = getTextToRetrieve(languange, templateTypeObject);
        if (fileType.equals("pdf")) {
            Assertions.assertNotNull(result.getFileTextRetrieved());
            Assertions.assertTrue(result.retrieveFormattedText().contains(textToFind));
            //additionalCheck(notificationType);
        } else {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertTrue(result.getTemplateHtmlReturned().contains(textToFind));
        }
    }

/*    @And("controllo che per il template {string} il file {string} per una notifica {string} il testo sia in lingua {string}")
    public void controlloChePerIlTemplateIlFilePerUnaNotificaIlTestoSiaInLingua(String templateType, String fileType, String notificationType, String languange) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        String textToFind = getTextToRetrieve(languange, templateTypeObject);
        if (fileType.equals("pdf")) {
            Assertions.assertNotNull(result.getFileTextRetrieved());
            Assertions.assertTrue(result.retrieveFormattedText().contains(textToFind));
            //additionalCheck(notificationType);
        } else {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertTrue(result.getTemplateHtmlReturned().contains(textToFind));
        }
    }*/

    @And("controllo che la notifica {string} abbia i giusti campi valorizzati")
    public void controlloCheLaNotificaAbbiaIGiustiCampiValorizzati(String notificationType) {
        switch (notificationType) {
            case "monodestinatario" -> {
                int count = countOccurrences("Nome e Cognome / Ragione Sociale");
                Assertions.assertEquals(1, count);
            } case "multidestinatario" -> {
                int count = countOccurrences("Nome e Cognome / Ragione Sociale");
                Assertions.assertEquals(2, count);
            } case "singolo allegato" -> {
                int count = countOccurrences("TEST_digest_allegato");
                Assertions.assertEquals(1, count);
            } case "piu allegati" -> {
                int count = countOccurrences("TEST_digest_allegato");
                Assertions.assertEquals(2, count);
            }
        }
    }
}
