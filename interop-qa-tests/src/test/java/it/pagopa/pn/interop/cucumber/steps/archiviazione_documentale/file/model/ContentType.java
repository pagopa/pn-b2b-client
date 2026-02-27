package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

public enum ContentType {
    P7M,
    GZIP,
    NDJSON,
    JSON,
    PDF;

    public static ContentType fromExtension(String ext) {
        return switch (ext.toLowerCase()) {
            case "p7m" -> P7M;
            case "gz" -> GZIP;
            case "ndjson.gz" -> GZIP; // payload compresso
            case "ndjson" -> NDJSON;
            case "pdf" -> PDF;
            default -> throw new IllegalArgumentException("Unsupported extension: " + ext);
        };
    }
}
