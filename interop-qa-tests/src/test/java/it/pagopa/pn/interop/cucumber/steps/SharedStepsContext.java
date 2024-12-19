package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.common.ClientCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedStepsContext {
    private HttpCallExecutor httpCallExecutor;
    private CommonUtils commonUtils;


    private int testSeed;
    private String tenantType;
    private String userToken;
    private ClientCommonContext clientCommonContext;
    private PurposeCommonContext purposeCommonContext;
    private EServicesCommonContext eServicesCommonContext;

    public SharedStepsContext(HttpCallExecutor httpCallExecutor, CommonUtils commonUtils) {
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @Before
    public void resetSharedStepsContext() {
        testSeed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        clientCommonContext = new ClientCommonContext();
        purposeCommonContext = new PurposeCommonContext();
        eServicesCommonContext = new EServicesCommonContext();
    }

    public String getXCorrelationId() {
        return String.valueOf(testSeed);
    }

}
