package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context;


import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.ArchivedFileMatched;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils;
import lombok.Getter;
import lombok.Setter;

public class ArchivingContext {

    private String centerTimestamp;
    @Getter
    @Setter
    private ArchivedFileMatched match = null;

    public String getCenterTimestamp() {
        if (centerTimestamp == null) centerTimestamp = ArchivingUtils.now();
        return centerTimestamp;
    }
}
