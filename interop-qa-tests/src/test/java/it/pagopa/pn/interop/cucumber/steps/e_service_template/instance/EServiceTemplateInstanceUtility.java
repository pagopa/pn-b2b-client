package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class EServiceTemplateInstanceUtility extends AbstractResolver {

    protected EServiceTemplateInstanceUtility(SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
    }

    public String parseSuffix(String suffix) {
        return super.resolveOrParse(
                suffix,
                value -> value,
                null,
                null,
                null,
                () -> " "
        );
    }

    public UUID resolveEServiceTemplateInstanceId(String eServiceTemplateInstanceId) {
        return resolveOrParse(eServiceTemplateInstanceId, UUID::fromString, () -> sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(), () -> sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(), UUID::randomUUID, () -> null);
    }

    public UUID resolveEServiceTemplateId(String eServiceTemplateId) {
        return resolveOrParse(eServiceTemplateId, UUID::fromString, () -> sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), () -> sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), UUID::randomUUID, () -> null);
    }

    public String resolveTenantName(String tenantTypeOrName) {
        try {
            return sharedStepsContext.getIdentityService().getTenantName(tenantTypeOrName);
        } catch (IllegalArgumentException exception) {
            return resolveOrParse(tenantTypeOrName, value -> value, () -> null, () -> null, () -> RandomStringUtils.insecure().nextAlphanumeric(8), () -> null);
        }
    }
}
