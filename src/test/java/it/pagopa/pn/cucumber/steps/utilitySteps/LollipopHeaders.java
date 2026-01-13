package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

@Getter
public enum LollipopHeaders {

    LOLLIPOP_ORIGINAL_URL("xPagopaLollipopOriginalUrl"),
    LOLLIPOP_ORIGINAL_METHOD("xPagopaLollipopOriginalMethod"),
    LOLLIPOP_PUBLIC_KEY("xPagopaLollipopPublicKey"),
    LOLLIPOP_ASSERTION_REF("xPagopaLollipopAssertionRef"),
    LOLLIPOP_ASSERTION_TYPE("xPagopaLollipopAssertionType"),
    LOLLIPOP_AUTH_JWT("xPagopaLollipopAuthJwt"),
    LOLLIPOP_SIGNATURE_INPUT("signatureInput"),
    LOLLIPOP_SIGNATURE("signature");

    private final String headerName;

    LollipopHeaders(String headerName) {
        this.headerName = headerName;
    }
}
