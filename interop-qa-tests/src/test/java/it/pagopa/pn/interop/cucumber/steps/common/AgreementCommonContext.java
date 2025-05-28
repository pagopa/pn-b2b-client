package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AgreementCommonContext {
    private ResponseEntity<CompactOrganizations> responseOffsetOne;
    private ResponseEntity<CompactOrganizations> responseOffsetTwo;
    private UUID documentId;
    private List<UUID> agreementIds = new ArrayList<>();
    private UUID responseAgreementId;
    private UUID eserviceSubscribedId;
    private UUID descriptorSubscribedId;

}
