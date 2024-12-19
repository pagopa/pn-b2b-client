package it.pagopa.pn.interop.cucumber.steps.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DelegationCommonContext {
    private UUID delegationId;
    private UUID delegatorId;
    private UUID delegateId;
}
