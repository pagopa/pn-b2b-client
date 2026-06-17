package it.pagopa.pn.cucumber.steps.delayer.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum WorkflowSteps {
    EVALUATE_SENDER_LIMIT(0),
    EVALUATE_SENDER_PRIORITY(1),
    EVALUATE_DRIVER_CAPACITY(2),
    EVALUATE_PRINT_CAPACITY(3),
    SENT_TO_PREPARE_PHASE_2(4),
    EVALUATE_RESIDUAL_CAPACITY(5);

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
