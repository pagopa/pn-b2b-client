package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.common.*;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.notification.model.NotificationCommonContext;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import it.pagopa.common.model.ISharedContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Slf4j
@ScenarioScope
public class SharedStepsContext implements ISharedContext {
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final DelayService delayService;

    private int testSeed;
    private String tenantType;
    private Role role;
    private String userToken;
    private Auth auth;
    private ClientCommonContext clientCommonContext;
    private PurposeCommonContext purposeCommonContext;
    private EServicesCommonContext eServicesCommonContext;
    private DelegationCommonContext delegationCommonContext;
    private AttributeCommonContext attributeCommonContext;
    private AgreementCommonContext agreementCommonContext;
    private RiskAnalysisCommonContext riskAnalysisCommonContext;
    private EServiceTemplateStepContext eServiceTemplateStepContext;
    private PurposeTemplateCommonContext purposeTemplateContext;
    private NotificationCommonContext notificationCommonContext = new NotificationCommonContext();
    private ProducerKeychainCommonContext producerKeychainCommonContext;
    private TenantCommonContext tenantCommonContext;

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
        purposeTemplateContext = new PurposeTemplateCommonContext();
        producerKeychainCommonContext = new ProducerKeychainCommonContext();
        tenantCommonContext = new TenantCommonContext();
    }

    @Before(order = Integer.MIN_VALUE)
    public void configLog(Scenario scenario) {
        MDC.clear();
        MDC.put("scenarioId", extractScenarioId(scenario.getName()));
    }

    private static String extractScenarioId(String scenarioName) {
        String scenarioIdRegex = "^(\\[.+\\])";

        Pattern pattern = Pattern.compile(scenarioIdRegex);
        Matcher matcher = pattern.matcher(scenarioName);

        String scenarioId;
        if (matcher.find()) {
            scenarioId = matcher.group(1);
        } else {
            scenarioId = RandomStringUtils.insecure().nextAlphanumeric(5);
            log.warn(
                "Non è stato possibile estrarre l'ID dello scenario '{}'. "
                    + "Al suo posto verrà utilizzata la stringa '{}'",
                scenarioName,
                scenarioId);
        }

        return scenarioId;
    }

    @Override
    public String getAgreementId() {
        return this.agreementCommonContext.getAgreementId().toString();
    }

    @Override
    public String getEServiceName() {
        return this.eServicesCommonContext.getName();
    }

    @Override
    public String getEServiceId() {
        return this.eServicesCommonContext.getEserviceId().toString();
    }

    @Override
    public String getDescriptorId() {
        return this.eServicesCommonContext.getDescriptorId().toString();
    }

    @Override
    public String getOldDescriptorId() {
        return this.eServicesCommonContext.getOldDescriptorId().toString();
    }

    @Override
    public String getProducerName() {
        return this.eServicesCommonContext.getProducerName();
    }

    @Override
    public String getConsumerName() {
        return this.tenantCommonContext.getConsumerTenantName();
    }

    @Override
    public String getPurposeId() {
        return this.purposeCommonContext.getNewPurposeId().toString();
    }

    @Override
    public String getPurposeTitle() {
        return this.purposeCommonContext.getCreatedPurposes().get(0).getTitle();
    }
}
