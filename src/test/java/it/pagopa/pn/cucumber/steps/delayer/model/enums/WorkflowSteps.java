package it.pagopa.pn.cucumber.steps.delayer.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum WorkflowSteps {
    EVALUATE_SENDER_LIMIT(0),
    EVALUATE_DRIVER_CAPACITY(1),
    EVALUATE_PRINT_CAPACITY(2),
    SENT_TO_PREPARE_PHASE_2(3),
    EVALUATE_RESIDUAL_CAPACITY(4);

    private final int index;

    WorkflowSteps(int index) {
        this.index = index;
    }

    public static Optional<WorkflowSteps> fromIndex(int index) {
        return Arrays.stream(values())
                .filter(ws -> ws.index == index)
                .findFirst();
    }

    public static Optional<WorkflowSteps> fromString(String name) {
        return Arrays.stream(values())
                .filter(ws -> ws.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
