package it.pagopa.interop.purpose.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Modalità di revisione della suite, indipendenti dalla versione dei client generati. */
public enum RiskAnalysisReviewMode {
    ADMIN_WRITES_ADMIN_SIGNS("ADMIN_WRITES_ADMIN_SIGNS"),
    ADMIN_WRITES_REVIEWER_SIGNS("ADMIN_WRITES_REVIEWER_SIGNS"),
    REVIEWER_WRITES_REVIEWER_SIGNS("REVIEWER_WRITES_REVIEWER_SIGNS");

    private final String value;

    RiskAnalysisReviewMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static RiskAnalysisReviewMode fromValue(String value) {
        for (RiskAnalysisReviewMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
