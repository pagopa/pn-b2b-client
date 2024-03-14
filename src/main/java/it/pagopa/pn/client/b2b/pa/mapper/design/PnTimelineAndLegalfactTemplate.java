package it.pagopa.pn.client.b2b.pa.mapper.design;

import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFact;
import it.pagopa.pn.client.b2b.pa.service.IPnTimelineLegalFactService;

public abstract class PnTimelineAndLegalfactTemplate <T extends PnTimelineLegalFact> implements IPnTimelineLegalFactService<T> {

    public abstract T getCategory(String legalFactCategory);


}

