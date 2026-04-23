package it.pagopa.pn.cucumber.steps.pa.utilityInformalVersion;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationInformalUtilsV1 extends B2bUtils {


    @Autowired
    public NotificationInformalUtilsV1(ApplicationContext context, IPnPaB2bClient b2bClient, PnPollingFactory pollingFactory) {
        super(context, b2bClient, pollingFactory);
    }
//todo t bonarie
}
