package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model;

public enum BucketRole {
    /** Write Once Read Many – archiviazione definitiva */
    WORM,

    /** Bucket operativo / temporaneo */
    STANDARD,

    /** Bucket contenente i dati firmati digitalmente */
    SIGNED,
}
