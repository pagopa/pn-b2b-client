package it.pagopa.pn.interop.cucumber.steps.common;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PurposeTemplateCommonContext {
    private UUID purposeTemplateId;
    private OffsetDateTime updatedAt;
}
