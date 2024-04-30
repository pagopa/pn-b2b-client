package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;


public class OpenSearchSteps {
    private final PnExternalServiceClientImpl pnExternalServiceClient;

    @Autowired
    public OpenSearchSteps(PnExternalServiceClientImpl pnExternalServiceClient) {
        this.pnExternalServiceClient = pnExternalServiceClient;
    }

    @Given("viene effettuato una prova di chiamata ad openSearch")
    public void vieneEffettuatoUnaProvaDiChiamata() {
        PnExternalServiceClientImpl.OpenSearchResponse stringResponseEntity = pnExternalServiceClient.openSearchGetAudit("10y","AUD_NT_PRELOAD",10);
        System.out.println(stringResponseEntity);
        System.out.println("NUM. DI RISULTATI: "+stringResponseEntity.getHits().getHits().size());
    }

    @Then("viene verificato che esiste un audit log {string} in {string}")
    public void vieneVerificatoCheEsisteUnAuditLogIn(String auditLogType, String auditLogRetention) {
        checkAudit(auditLogType,auditLogRetention,-1);
    }

    @Then("viene verificato che esiste un audit log {string} in {string} non più vecchio di {int} giorni")
    public void vieneVerificatoCheEsisteUnAuditLogInNonPiùVecchioDiGiorni(String auditLogType, String auditLogRetention, Integer maxAge) {
        checkAudit(auditLogType,auditLogRetention,maxAge);
    }

    private void checkAudit(String auditLogType, String auditLogRetention, Integer maxAge){
        PnExternalServiceClientImpl.OpenSearchResponse openSearchResponse = pnExternalServiceClient.openSearchGetAudit(auditLogRetention,auditLogType,10);
        Assertions.assertFalse(openSearchResponse.getHits().getHits().isEmpty());
        PnExternalServiceClientImpl.InnerHits innerHits = openSearchResponse.getHits().getHits().get(0);
        Assertions.assertTrue(innerHits.get_source().getAud_type().equalsIgnoreCase(auditLogType));
        if(maxAge != -1){
            OffsetDateTime ageTimeStamp = innerHits.get_source().getTimestamp();
            System.out.println("AGE: "+ageTimeStamp);
            Assertions.assertTrue(ChronoUnit.DAYS.between(ageTimeStamp,OffsetDateTime.now()) <= maxAge);
            System.out.println("DIFF: "+ ChronoUnit.DAYS.between(ageTimeStamp,OffsetDateTime.now()));
        }
    }

    @And("viene verificato che non esiste un audit log {string} in {string}")
    public void vieneVerificatoCheNonEsisteUnAuditLogIn(String auditLogType, String auditLogRetention) {
        PnExternalServiceClientImpl.OpenSearchResponse openSearchResponse = pnExternalServiceClient.openSearchGetAudit(auditLogRetention,auditLogType,10);
        Assertions.assertTrue(openSearchResponse.getHits().getHits().isEmpty());
    }
}