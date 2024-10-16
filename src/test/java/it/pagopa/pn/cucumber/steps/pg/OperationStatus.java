package it.pagopa.pn.cucumber.steps.pg;

import lombok.Getter;

@Getter
public enum OperationStatus {

    REGISTER("CREA", VirtualKeyState.ENABLE),
    ROTATE("RUOTA", VirtualKeyState.ROTATE),
    BLOCK("BLOCCA", VirtualKeyState.BLOCK),
    REACTIVATE("RIATTIVA", VirtualKeyState.ENABLE),
    DELETE("CANCELLA", VirtualKeyState.CANCELLED);

    private String value;
    private VirtualKeyState status;

    OperationStatus(String value, VirtualKeyState status) {
        this.value = value;
        this.status = status;
    }

    public static OperationStatus fromValue(String value) {
        for (OperationStatus operationStatus : OperationStatus.values()) {
            if (operationStatus.value.equalsIgnoreCase(value)) {
                return operationStatus;
            }
        }
        throw new IllegalArgumentException("Operazione non valida: " + value);
    }
}
