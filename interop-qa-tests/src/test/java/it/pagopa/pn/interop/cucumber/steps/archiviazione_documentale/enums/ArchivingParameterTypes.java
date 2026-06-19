package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.event.enums.InteropEvent;
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

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent interopEvent(String event) {
        return InteropEvent.fromValue(event);
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent eserviceInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.ESERVICE.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent agreementInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.AGREEMENT.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent purposeTemplateInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.PURPOSE_TEMPLATE.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent purposeInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.PURPOSE.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent consumerDelegationInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.CONSUMER_DELEGATION.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent producerDelegationInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.PRODUCER_DELEGATION.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent clientInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.CLIENT.name());
    }

    @ParameterType("[A-Za-z0-9]+")
    public InteropEvent keyInteropEvent(String event) {
        return InteropEvent.fromValueAndFamily(event, InteropEvent.Family.KEY.name());
    }

    @ParameterType("[A-Za-z_]+")
    public InteropFile interopFile(String file) {
        return InteropFile.valueOf(file);
    }
}
