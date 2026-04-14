package it.pagopa.pn.interop.cucumber.steps.m2m.event.config;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateM2MEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceEvent;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.event.util.RequestMappingUtils;

import java.util.Map;

public class EventRequestConfig {

    private static final boolean FAIL_FAST_UNKNOWN_FIELDS = true;

    private final TokenResolver tokenResolver;

    public EventRequestConfig(SharedStepsContext sharedStepsContext) {
        this.tokenResolver = new TokenResolver(sharedStepsContext);
    }

    @ParameterType("non visualizza|visualizza")
    public Boolean visibilitaEvento(String testo) {
        return "visualizza".equals(testo);
    }

    @DataTableType
    public PurposeTemplateM2MEvent purposeTemplateEventMapper(Map<String, String> row) {
        return RequestMappingUtils.mapToRequest(new PurposeTemplateM2MEvent(), resolveTokens(row), FAIL_FAST_UNKNOWN_FIELDS);
    }

    @DataTableType
    public EServiceEvent eserviceEventMapper(Map<String, String> row) {
        return RequestMappingUtils.mapToRequest(new EServiceEvent(), resolveTokens(row), FAIL_FAST_UNKNOWN_FIELDS);
    }

    private Map<String, String> resolveTokens(Map<String, String> row) {
        return row.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> tokenResolver.resolve(e.getValue())
                ));
    }
}
