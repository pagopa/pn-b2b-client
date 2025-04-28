package it.pagopa.pn.interop.cucumber.steps.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Setter
@Component
public class PurposeCommonContext {
    private List<String> purposesIds = new ArrayList<>();
    private List<String> currentVersionIds = new ArrayList<>();
    private List<String> waitingForApprovalVersionIds = new ArrayList<>();
    private String purposeId;

    /* NOTA 23/04/2025: non sono ancora stati osservati casi in cui è stato usato contemporaneamente
    * a purposeId; possibile sia da rimuovere e usare al suo posto sempre purposeId */
    private UUID newPurposeId;

    private String versionId;
    private String waitingForApprovalVersionId;

    /* TODO 23/04/2025: sarebbe il caso di cambiare il tipo String -> UUID e uniformare quindi
    *   questo metodo con getPurposeId */
    public UUID getPurposeIdAsUUID() {
        return UUID.fromString(purposeId);
    }
}
