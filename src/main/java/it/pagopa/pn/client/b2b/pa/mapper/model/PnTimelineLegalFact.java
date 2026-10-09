package it.pagopa.pn.client.b2b.pa.mapper.model;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactCategoryV20;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PnTimelineLegalFact {
    protected LegalFactCategoryV20 legalFactCategory;
    protected TimelineElementCategoryV28 timelineElementInternalCategory;
}
