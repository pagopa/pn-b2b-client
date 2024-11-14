package it.pagopa.pn.client.b2b.pa.interop.service.impl;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.GetTracingsResponseResults;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.TracingState;
import it.pagopa.pn.client.b2b.pa.interop.IInteropTracingClient;
import it.pagopa.pn.client.b2b.pa.interop.polling.dto.PnTracingResponse;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

@Service(PnPollingStrategy.INTEROP_TRACING)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingInteropTracing extends PnPollingTemplate<PnTracingResponse> {
    private static final int OFFSET_VALUE = 0;
    private static final int LIMIT_VALUE = 50;
    protected final TimingForPolling timingForPolling;
    private final IInteropTracingClient interopTracingClient;
    private GetTracingsResponse pollingResponse;

    public PnPollingInteropTracing(TimingForPolling timingForPolling, IInteropTracingClient interopTracingClient) {
        this.timingForPolling = timingForPolling;
        this.interopTracingClient = interopTracingClient;
    }

    @Override
    protected Callable<PnTracingResponse> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnTracingResponse pnTracingResponse = new PnTracingResponse();
            GetTracingsResponse getTracingsResponse;
            try {
                getTracingsResponse = interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, List.of(pnPollingParameter.getPnPollingInterop().getStatus()));
            } catch (Exception exception) {
                log.error("There was an error while retrieving all the tracings!");
                throw new PnPollingException(exception.getMessage());
            }
            pnTracingResponse.setGetTracingsResponse(getTracingsResponse);
            this.pollingResponse = getTracingsResponse;
            return pnTracingResponse;
        };
    }

    @Override
    protected Predicate<PnTracingResponse> checkCondition(String id, PnPollingParameter pnPollingParameter) {
        return pnTracingResponse -> {
            pnTracingResponse.setResult(
                    pnTracingResponse.getGetTracingsResponse().getResults().stream()
                    .filter(x -> pnPollingParameter.getPnPollingInterop().getTracingId().equals(x.getTracingId()))
                    .map(GetTracingsResponseResults::getState)
                    .map(TracingState::fromValue)
                    .anyMatch(status -> pnPollingParameter.getPnPollingInterop().getStatus().equals(status)));
            return pnTracingResponse.getResult();
        };
    }

    @Override
    protected PnTracingResponse getException(Exception exception) {
        PnTracingResponse pnTracingResponse = new PnTracingResponse();
        pnTracingResponse.setGetTracingsResponse(this.pollingResponse);
        pnTracingResponse.setResult(false);
        return pnTracingResponse;
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, false, true);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, false, true);
        return timingResult.waiting() * timingResult.numCheck();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApiKey(String apiKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        throw new UnsupportedOperationException();
    }
}
