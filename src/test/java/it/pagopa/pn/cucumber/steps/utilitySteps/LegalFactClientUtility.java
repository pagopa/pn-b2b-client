package it.pagopa.pn.cucumber.steps.utilitySteps;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.legalfact.*;
import it.pagopa.pn.cucumber.steps.legalfact.data.LegalFactClientType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class LegalFactClientUtility {

    @Autowired
    private BeanFactory beanFactory;

    private final Map<LegalFactClientType, LegalFactClient> clientTypeMap;

    private LegalFactClientUtility() {
        this.clientTypeMap = Map.of(
                LegalFactClientType.PA, beanFactory.getBean(LegalFactPAClient.class),
                LegalFactClientType.PA_DOWNLOAD, beanFactory.getBean(LegalFactPADownloadClient.class),
                LegalFactClientType.APP_IO, beanFactory.getBean(LegalFactAppIOClient.class),
                LegalFactClientType.WEB_RECIPIENT, beanFactory.getBean(LegalFactWebRecipientClient.class));
    }

    public List<LegalFactDownloadMetadataResponse> getLegalFactResponseList(List<LegalFactClientType> legalFactClientTypes, final SharedSteps sharedSteps, final LegalFactCategory categorySearch, final String finalKeySearch) {
        return legalFactClientTypes.stream()
                .map(legalFact -> clientTypeMap.get(legalFact))
                .filter(Objects::nonNull)
                .map(client -> client.getLegalFact(sharedSteps, categorySearch, finalKeySearch))
                .collect(Collectors.toList());
    }

    public TimelineElementV23 getTimelineElement(SharedSteps sharedSteps, String deliveryDetailCode, TimelineElementCategoryV23 timelineElementInternalCategory) {
        return sharedSteps.getSentNotification().getTimeline().stream()
                .filter(el -> el.getCategory() != null && el.getCategory().equals(timelineElementInternalCategory))
                .filter(el -> el.getDetails() != null && el.getDetails().getDeliveryDetailCode() != null &&
                        (deliveryDetailCode == null || el.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)))
                .findFirst()
                .orElse(null);
    }
}
