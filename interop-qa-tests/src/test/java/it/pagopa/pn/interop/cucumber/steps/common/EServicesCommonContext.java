package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.agreement.DocumentMetadata;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    private UUID eserviceId;
    private UUID descriptorId;

    private UUID documentId;
    private UUID documentId2;
    private List<DocumentMetadata> documentsMetadata;

    private UUID interfaceId;
    private String interfaceName;
    private UUID oldDescriptorId;
    private String name;

}
