package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import com.fasterxml.jackson.annotation.JsonCreator;

/* Astrazione degli oggetti AgreementState usati nei set di APIs interop. Si è scelto di evitare
* il prefisso "abstract" poiché in Java ciò suggerirebbe una classe astratta. */
public enum UpperAgreementState {
  
  DRAFT("DRAFT"),
  
  ACTIVE("ACTIVE"),
  
  ARCHIVED("ARCHIVED"),
  
  PENDING("PENDING"),
  
  SUSPENDED("SUSPENDED"),
  
  MISSING_CERTIFIED_ATTRIBUTES("MISSING_CERTIFIED_ATTRIBUTES"),
  
  REJECTED("REJECTED");

  private final String value;

  UpperAgreementState(String value) {
    this.value = value;
  }

  @JsonCreator
  public static UpperAgreementState fromValue(String value) {
    for (UpperAgreementState b : UpperAgreementState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }

  public static UpperAgreementState from(
      it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState agreementState) {
    return UpperAgreementState.fromValue(agreementState.getValue());
  }

  public static UpperAgreementState from(
      it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState agreementState) {
    return UpperAgreementState.fromValue(agreementState.getValue());
  }

}