package it.pagopa.pn.client.b2b.pa.mapper.model;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PnTimelineLegalFactV23 extends PnTimelineLegalFact {
    protected LegalFactCategory legalFactCategory;
    protected TimelineElementCategoryV23 timelineElementInternalCategory;
}
