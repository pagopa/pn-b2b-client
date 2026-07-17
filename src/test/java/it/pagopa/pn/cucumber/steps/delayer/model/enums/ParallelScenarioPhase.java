package it.pagopa.pn.cucumber.steps.delayer.model.enums;

/**
 * Stato remoto per-scenario nella suite parallela Delayer (gate su Batch / Phase2).
 */
public enum ParallelScenarioPhase {
    BATCH_REQUESTED,
    BATCH_DONE,
    PHASE2_REQUESTED,
    PHASE2_DONE
}
