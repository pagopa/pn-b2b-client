package it.pagopa.interop.tracing.client.model;

public enum BucketRole {
    /** Write Once Read Many – archiviazione definitiva */
    WORM,

    /** Bucket operativo / temporaneo */
    STANDARD
}
