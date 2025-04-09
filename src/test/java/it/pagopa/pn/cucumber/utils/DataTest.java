package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26;
import it.pagopa.pn.cucumber.utils.datatest.AbstractDataTest;
import lombok.Data;

/**
 * TODO: Al momento la tengo perchè è usata anche in altri punti fuori da AvanzamentoNotificheB2BSteps ma poi diventerà superflua
 */
@Data
public class DataTest extends AbstractDataTest {

    private TimelineElementV26 timelineElement;
}