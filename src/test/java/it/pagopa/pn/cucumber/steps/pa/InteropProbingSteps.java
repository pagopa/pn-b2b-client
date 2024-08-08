package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.IPnInteropProbingClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Slf4j
public class InteropProbingSteps {

  private final IPnInteropProbingClient IPnInteropProbingClient;
  private ResponseEntity<Void> probingResponse;

  @Autowired
  public InteropProbingSteps(IPnInteropProbingClient IPnInteropProbingClient) {
    this.IPnInteropProbingClient = IPnInteropProbingClient;
  }

  @When("viene chiamato il servizio di probing per interop")
  public void probingService() {
    probingResponse = IPnInteropProbingClient.getEserviceStatus();
  }

  @Then("la chiamata al servizio di probing per interop restituisce {int}")
  public void probingServiceResponse(int exspectedStatus) {
    Assertions.assertEquals(exspectedStatus, probingResponse.getStatusCodeValue());
  }
}
