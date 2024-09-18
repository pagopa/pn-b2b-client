package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.UserDto;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.IMandateServiceClient;
import it.pagopa.pn.client.b2b.pa.service.impl.B2bMandateServiceClientImpl;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class DestinatariStrutturatiSteps {

    private ResponseEntity<String> mandateReverseResponse;




    @Then("si verifica che la risposta contenga lo status code: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, mandateReverseResponse.getStatusCode().value());
    }

    }
