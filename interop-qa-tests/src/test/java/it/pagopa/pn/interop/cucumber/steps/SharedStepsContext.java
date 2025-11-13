package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.common.AgreementCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.ClientCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

@Getter
@Setter
@Slf4j
@ScenarioScope
public class SharedStepsContext {
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final DelayService delayService;

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
    private EServiceTemplateStepContext eServiceTemplateStepContext;

    public SharedStepsContext(
            IHttpExecutor httpCallExecutor,
            @Qualifier("interopIdentityService") IdentityService identityService,
            PollingService pollingService,
            DelayService delayService) {
        this.httpCallExecutor = httpCallExecutor;
        this.identityService = identityService;
        this.pollingService = pollingService;
        this.delayService = delayService;
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
        eServiceTemplateStepContext = new EServiceTemplateStepContext();
    }

}
