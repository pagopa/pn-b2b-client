package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFact;


public interface IPnTimelineLegalFactService <T extends PnTimelineLegalFact> {
    T getCategory(String legalFactCategory);
}