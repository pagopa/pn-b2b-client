package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.cucumber.steps.pg.LegalPersonAuthSteps;

import java.util.List;

@Getter
@Setter
public class LegalPersonsAuthStepsPojo {
    private List<LegalPersonAuthSteps.ExpectedPublicKey> publicKeysResponses;
    private RestClientException exception;
}
