package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServicesCommonContext {
    private List<EServiceDescriptor> publishedEservicesIds = new ArrayList<>();
    private List<EServiceDescriptor> suspendedEservicesIds = new ArrayList<>();
    private List<EServiceDescriptor> draftEServicesIds = new ArrayList<>();
    private List<EServiceDescriptor> retrievedEservicesIds = new ArrayList<>();
    private List<EServiceDescriptor> totalPublishedEServicesIds = new ArrayList<>();
    private UUID eserviceId;
    private UUID descriptorId;
    private int groupId; // id dell'ultimo gruppo di attributi creato
    private String producerName;

    private List<UUID> certifiedAttributesIds = new ArrayList<>();
    private List<UUID> declaredAttributesIds = new ArrayList<>();
    private List<UUID> verifiedAttributesIds = new ArrayList<>();

    private List<UUID> removedCertifiedAttributesIds = new ArrayList<>();
    private List<UUID> removedDeclaredAttributesIds = new ArrayList<>();
    private List<UUID> removedVerifiedAttributesIds = new ArrayList<>();

    private UUID documentId;
    private UUID documentId2;
    private List<DocumentMetadata> documentsMetadata;

    private UUID interfaceId;
    private String interfaceName;
    private UUID callbackInterfaceId;
    private String callbackInterfaceName;
    private UUID oldDescriptorId;
    private String name;
    private String description;
    private Boolean isConsumerDelegable;
    private Boolean IsClientAccessDelegable;

    private OffsetDateTime creationTimestamp;
    private OffsetDateTime publicationTimestamp;
    private OffsetDateTime eServiceEditTimestamp;
    private OffsetDateTime descriptorArchivingRequestTimestamp;
    private GracePeriodDays descriptorArchivingGracePeriodDays;

    public void addCertifiedAttributes(List<UUID> attributesIds) {
        this.certifiedAttributesIds.addAll(attributesIds);
    }

    public void addDeclaredAttributes(List<UUID> attributesIds) {
        this.declaredAttributesIds.addAll(attributesIds);
    }

    public void addVerifiedAttributes(List<UUID> attributesIds) {
        this.verifiedAttributesIds.addAll(attributesIds);
    }
}
