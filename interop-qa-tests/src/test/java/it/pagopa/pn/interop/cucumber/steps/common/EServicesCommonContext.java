package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<DocumentMetadata> documentsMetadata = new ArrayList<>();

    private UUID interfaceId;
    private String interfaceName;
    private String interfaceUploadPath;
    private UUID callbackInterfaceId;
    private String callbackInterfaceName;
    private String callbackInterfaceUploadPath;
    private AsyncExchangeProperties asyncExchangeProperties;
    private UUID oldDescriptorId;
    private String name;
    private String description;
    private Boolean isConsumerDelegable;
    private Boolean IsClientAccessDelegable;
    private EServiceSeed eServiceSeed = new EServiceSeed();
    private final Map<UUID, UpdateEServiceDescriptorSeed> descriptorSeeds = new HashMap<>();

    private OffsetDateTime creationTimestamp;
    private OffsetDateTime publicationTimestamp;
    private OffsetDateTime eServiceEditTimestamp;
    private OffsetDateTime descriptorArchivingRequestTimestamp;
    private GracePeriodDays descriptorArchivingGracePeriodDays;
    private OffsetDateTime eServiceArchivingRequestTimestamp;
    private GracePeriodDays eServiceArchivingGracePeriodDays;

    public void addCertifiedAttributes(List<UUID> attributesIds) {
        this.certifiedAttributesIds.addAll(attributesIds);
    }

    public void addDeclaredAttributes(List<UUID> attributesIds) {
        this.declaredAttributesIds.addAll(attributesIds);
    }

    public void addVerifiedAttributes(List<UUID> attributesIds) {
        this.verifiedAttributesIds.addAll(attributesIds);
    }

    public EServiceSeed getEServiceSeed() {
        return eServiceSeed;
    }

    public void setEServiceSeed(EServiceSeed eServiceSeed) {
        this.eServiceSeed = eServiceSeed == null ? new EServiceSeed() : eServiceSeed;
        this.name = this.eServiceSeed.getName();
        this.description = this.eServiceSeed.getDescription();
        this.isConsumerDelegable = this.eServiceSeed.getIsConsumerDelegable();
        this.IsClientAccessDelegable = this.eServiceSeed.getIsClientAccessDelegable();
    }

    public UpdateEServiceDescriptorSeed getDescriptorSeed(UUID descriptorId) {
        return descriptorId == null ? null : descriptorSeeds.get(descriptorId);
    }

    public void setDescriptorSeed(UUID descriptorId, UpdateEServiceDescriptorSeed descriptorSeed) {
        if (descriptorId != null && descriptorSeed != null) {
            descriptorSeeds.put(descriptorId, descriptorSeed);
        }
    }

    public String getName() {
        return eServiceSeed != null && eServiceSeed.getName() != null ? eServiceSeed.getName() : name;
    }

    public void setName(String name) {
        this.name = name;
        if (eServiceSeed == null) {
            eServiceSeed = new EServiceSeed();
        }
        eServiceSeed.setName(name);
    }

    public String getDescription() {
        return eServiceSeed != null && eServiceSeed.getDescription() != null ? eServiceSeed.getDescription() : description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (eServiceSeed == null) {
            eServiceSeed = new EServiceSeed();
        }
        eServiceSeed.setDescription(description);
    }

    public Boolean getIsConsumerDelegable() {
        return eServiceSeed != null && eServiceSeed.getIsConsumerDelegable() != null ? eServiceSeed.getIsConsumerDelegable() : isConsumerDelegable;
    }

    public void setIsConsumerDelegable(Boolean isConsumerDelegable) {
        this.isConsumerDelegable = isConsumerDelegable;
        if (eServiceSeed == null) {
            eServiceSeed = new EServiceSeed();
        }
        eServiceSeed.setIsConsumerDelegable(isConsumerDelegable);
    }

    public Boolean getIsClientAccessDelegable() {
        return eServiceSeed != null && eServiceSeed.getIsClientAccessDelegable() != null ? eServiceSeed.getIsClientAccessDelegable() : IsClientAccessDelegable;
    }

    public void setIsClientAccessDelegable(Boolean isClientAccessDelegable) {
        this.IsClientAccessDelegable = isClientAccessDelegable;
        if (eServiceSeed == null) {
            eServiceSeed = new EServiceSeed();
        }
        eServiceSeed.setIsClientAccessDelegable(isClientAccessDelegable);
    }
}
