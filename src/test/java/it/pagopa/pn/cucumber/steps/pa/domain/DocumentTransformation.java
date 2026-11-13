package it.pagopa.pn.cucumber.steps.pa.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Trasformazioni che pn-ec applica ai documenti da stampare, con il prefisso della fileKey del documento trasformato
@Getter
@RequiredArgsConstructor
public enum DocumentTransformation {
    RASTERIZZAZIONE("PN_PAPER_ATTACHMENT-"),
    NORMALIZZAZIONE("PN_CLEAN_PAPER_ATTACHMENT-");

    private final String fileKeyPrefix;
}
