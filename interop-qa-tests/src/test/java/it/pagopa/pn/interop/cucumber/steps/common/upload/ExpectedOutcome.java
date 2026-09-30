package it.pagopa.pn.interop.cucumber.steps.common.upload;

public enum ExpectedOutcome {
    SUCCESS,
    FAILURE;

    public boolean isSuccessExpected() {
        return this == SUCCESS;
    }

    public static ExpectedOutcome fromItalian(String value) {
        return switch (value) {
            case "positivo" -> SUCCESS;
            case "negativo" -> FAILURE;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}

