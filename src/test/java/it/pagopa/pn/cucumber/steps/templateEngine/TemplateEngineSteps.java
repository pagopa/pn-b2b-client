package it.pagopa.pn.cucumber.steps.templateEngine;

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
        retrieveTemplate(templateTypeObject, language, body, new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String language) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body:")
    public void recuperoIlTemplateConIValoriNelRequestBody(String templateType, Map<String, String> parameters) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body errati")
    public void recuperoIlTemplateConIValoriNelRequestBodyErrati(String templateType) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        templateEngineObjectFields.get(templateTypeObject)
                .forEach(data -> {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put(data, "null");
                    retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, parameters);
                });
    }

    private void retrieveTemplate(TemplateType templateType, String language, String body, Map<String, String> parameters) {
        try {
            TemplateRequestContext context = contextFactory.createContext(parameters);
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
                return true;
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
}
