package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Slf4j
public class TemplateEngineSteps {

    private static final String BODY_CORRETTO = "CORRETTO";

    private Map<String, ITemplateEngineStrategy> templateEngineStrategy;
    private TemplateEngineResult result;

    private HttpClientErrorException templateFileException;

    public TemplateEngineSteps(Map<String, ITemplateEngineStrategy> templateEngineStrategy) {
        this.templateEngineStrategy = templateEngineStrategy;
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string} con il body {string}")
    public void recuperoIlTemplatePerInLinguaConIlBody(String templateType, String language, String body) {
        try {
            result = templateEngineStrategy.get(templateType.toUpperCase()).retrieveTemplate(language, body.equals(BODY_CORRETTO));
        } catch (HttpClientErrorException e) {
            templateFileException = e;
        }
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void scaricoIlTemplateInLingua(String templateType, String language) {
        recuperoIlTemplatePerInLinguaConIlBody(templateType, language, BODY_CORRETTO);
    }

    @Then("verifico che il template è in formato {string}")
    public void verificoCheIlTemplateInFormato(String extentionFile) {
        Assertions.assertNotNull(result);
        if(extentionFile.equals(".pdf")) {
            Assertions.assertNotNull(result.getTemplateFileReturned());
            Assertions.assertTrue(result.getTemplateFileReturned().getName().endsWith(extentionFile));
        } else {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
        }
    }

    @Then("verifico che la chiamata sia andata in {string} error")
    public void verificoCheLaChiamataSiaAndataInError(String errorCode) {
        Assertions.assertNotNull(templateFileException);
        Assertions.assertEquals(errorCode, templateFileException.getStatusText());
    }
}
