package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.common.ClientCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SharedStepsContext {
    private HttpCallExecutor httpCallExecutor;
    private IdentityService identityService;
    private PollingService pollingService;

    private int testSeed;
    private String tenantType;
    private String userToken;
    private UUID agreementId;
    private ClientCommonContext clientCommonContext;
    private PurposeCommonContext purposeCommonContext;
    private EServicesCommonContext eServicesCommonContext;
    private DelegationCommonContext delegationCommonContext;
    private EServiceTemplateStepContext eServiceTemplateStepContext;

    public SharedStepsContext(
        HttpCallExecutor httpCallExecutor,
        @Qualifier("interopIdentityService") IdentityService identityService,
        PollingService pollingService) {
        this.httpCallExecutor = httpCallExecutor;
        this.identityService = identityService;
        this.pollingService = pollingService;
    }

    @Before
    public void resetSharedStepsContext() {
        testSeed = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        clientCommonContext = new ClientCommonContext();
        purposeCommonContext = new PurposeCommonContext();
        eServicesCommonContext = new EServicesCommonContext();
        delegationCommonContext = new DelegationCommonContext();
        eServiceTemplateStepContext = new EServiceTemplateStepContext();
    }

    public String getXCorrelationId() {
        return String.valueOf(testSeed);
    }

}
