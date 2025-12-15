package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import io.cucumber.java.ParameterType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;

public class ArchivingParameterTypes {

    @ParameterType("signed|SIGNED|unsigned|UNSIGNED")
    public boolean bucketType(String type) {
        switch (type) {
            case "signed", "SIGNED" -> {
                return true;
            }
            case "unsigned", "UNSIGNED" -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Tipo di base non riconosciuto: " + type);
        }
    }

    @ParameterType("STANDARD|WORM")
    public BucketRole bucketRole(String role) {
        return BucketRole.valueOf(role);
    }

    @ParameterType("[A-Za-z]+")
    public InteropEvent interopEvent(String event) {
        return InteropEvent.fromValue(event);
    }

    @ParameterType("[A-Za-z_]+")
    public InteropFile interopFile(String file) {
        return InteropFile.valueOf(file);
    }
}
