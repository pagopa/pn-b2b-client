package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.common.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Slf4j
@ScenarioScope
public class SharedStepsContext {
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final PollingService pollingService;

    private int testSeed;
    private String tenantType;
    private Role role;
    private String userToken;
    private UUID agreementId;
    private ClientCommonContext clientCommonContext;
    private PurposeCommonContext purposeCommonContext;
    private EServicesCommonContext eServicesCommonContext;
    private DelegationCommonContext delegationCommonContext;
    private AttributeCommonContext attributeCommonContext;
    private AgreementCommonContext agreementCommonContext;
    private RiskAnalysisCommonContext riskAnalysisCommonContext;

    public SharedStepsContext(
            IHttpExecutor httpCallExecutor,
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
        attributeCommonContext = new AttributeCommonContext();
        agreementCommonContext = new AgreementCommonContext();
        riskAnalysisCommonContext = new RiskAnalysisCommonContext();
    }

}
