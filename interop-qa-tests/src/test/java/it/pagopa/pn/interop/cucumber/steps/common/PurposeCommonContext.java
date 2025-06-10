package it.pagopa.pn.interop.cucumber.steps.common;

import static java.util.stream.Collectors.toList;

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

    private UUID newPurposeId;

    private String versionId;
    private String waitingForApprovalVersionId;

    /* TODO 23/04/2025: sarebbe il caso di cambiare il tipo String -> UUID e uniformare quindi
    *   questo metodo con getPurposeId */
    public UUID getPurposeIdAsUUID() {
        return UUID.fromString(purposeId);
    }

    // TODO 12/05/2025: sarebbe il caso di cambiare il tipo String -> UUID e uniformare quindi i getters
    public UUID getWaitingForApprovalVersionIdAsUUID() {
        return UUID.fromString(waitingForApprovalVersionId);
    }

    // TODO 09/06/2025: sarebbe il caso di cambiare il tipo String -> UUID e uniformare quindi i getters
    public UUID getCurrentVersionIdAsUUID() {
        return UUID.fromString(currentVersionIds.get(currentVersionIds.size() - 1));
    }

    public List<UUID> getPurposesIdsAsUUID() {
        return this.getPurposesIds().stream()
            .map(UUID::fromString)
            .collect(toList());
    }

    public List<UUID> getPurposeCurrentVersionsIdsAsUUID() {
        return this.getCurrentVersionIds().stream()
            .map(UUID::fromString)
            .collect(toList());
    }

    public String getCurrentVersionId() {
        return currentVersionIds.get(currentVersionIds.size() - 1);
    }

    public void addCurrentVersionId(UUID id) {
        currentVersionIds.add(id.toString());
    }
}
