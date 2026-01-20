package it.pagopa.interop.probing.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.probing.ApiClient;
import it.pagopa.interop.generated.openapi.clients.probing.api.EServicesApi;
import it.pagopa.interop.generated.openapi.clients.probing.api.ProducersApi;
import it.pagopa.interop.generated.openapi.clients.probing.api.StatusApi;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.probing.service.IProbingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProbingClient extends AbstractClient implements IProbingClient {

    // --- probing (core) ---
    private final StatusApi statusApi;
    private final EServicesApi eServicesApi;
    private final ProducersApi producersApi;

    // --- probingStatistics ---
    private final it.pagopa.interop.generated.openapi.clients.probingStatistics.api.StatusApi statisticsStatusApi;
    private final it.pagopa.interop.generated.openapi.clients.probingStatistics.api.TelemetryApi telemetryApi;

    private final RestTemplate restTemplate;
    private final String basePath;

    private final HttpCallExecutor httpCallExecutor;

    public ProbingClient(
            RestTemplate restTemplate,
            InteropClientConfigs interopClientConfigs,
            HttpCallExecutor httpCallExecutor
    ) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getProbingBaseUrl();

        this.httpCallExecutor = httpCallExecutor;

        // --- probing core ---
        ApiClient probingApiClient = createProbingApiClient("dummyBearer");
        this.statusApi = new StatusApi(probingApiClient);
        this.eServicesApi = new EServicesApi(probingApiClient);
        this.producersApi = new ProducersApi(probingApiClient);

        // --- probingStatistics ---
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient statsApiClient =
                createStatisticsApiClient("dummyBearer");
        this.statisticsStatusApi = new it.pagopa.interop.generated.openapi.clients.probingStatistics.api.StatusApi(statsApiClient);
        this.telemetryApi = new it.pagopa.interop.generated.openapi.clients.probingStatistics.api.TelemetryApi(statsApiClient);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        // --- probing core ---
        ApiClient probingApiClient = createProbingApiClient(bearerToken);
        this.statusApi.setApiClient(probingApiClient);
        this.eServicesApi.setApiClient(probingApiClient);
        this.producersApi.setApiClient(probingApiClient);

        // --- probingStatistics ---
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient statsApiClient =
                createStatisticsApiClient(bearerToken);
        this.statisticsStatusApi.setApiClient(statsApiClient);
        this.telemetryApi.setApiClient(statsApiClient);
    }

    private ApiClient createProbingApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        // apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    private it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient createStatisticsApiClient(String bearerToken) {
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient apiClient =
                new it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        // apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    // -------------------------
    // StatusApi (probing core) mappings
    // -------------------------

    @Override
    public void getProbingApiHealthStatus() {
        performOperation(() -> statusApi.getHealthStatusWithHttpInfo()).orElse(null);
    }

    // -------------------------
    // EServicesApi mappings
    // -------------------------

    @Override
    public MainDataEserviceResponse getEserviceMainData(Long eserviceRecordId) {
        return performOperation(() -> eServicesApi.getEserviceMainDataWithHttpInfo(eserviceRecordId))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero main data e-service (response non 2xx o body nullo)"
                ));
    }

    @Override
    public ProbingDataEserviceResponse getEserviceProbingData(Long eserviceRecordId) {
        return performOperation(() -> eServicesApi.getEserviceProbingDataWithHttpInfo(eserviceRecordId))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero probing data e-service (response non 2xx o body nullo)"
                ));
    }

    @Override
    public SearchEserviceResponse searchEservices(
            Integer limit,
            Integer offset,
            String eserviceName,
            String producerName,
            Integer versionNumber,
            List<EserviceStateFE> state
    ) {
        return performOperation(() -> eServicesApi.searchEservicesWithHttpInfo(
                limit, offset, eserviceName, producerName, versionNumber, state
        ))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nella ricerca e-services (response non 2xx o body nullo)"
                ));
    }

    @Override
    public void updateEserviceFrequency(UUID eserviceId, UUID versionId, ChangeProbingFrequencyRequest request) {
        performOperation(() -> eServicesApi.updateEserviceFrequencyWithHttpInfo(eserviceId, versionId, request))
                .orElse(null);
    }

    @Override
    public void updateEserviceProbingState(UUID eserviceId, UUID versionId, ChangeProbingStateRequest request) {
        performOperation(() -> eServicesApi.updateEserviceProbingStateWithHttpInfo(eserviceId, versionId, request))
                .orElse(null);
    }

    @Override
    public void updateEserviceState(UUID eserviceId, UUID versionId, ChangeEserviceStateRequest request) {
        performOperation(() -> eServicesApi.updateEserviceStateWithHttpInfo(eserviceId, versionId, request))
                .orElse(null);
    }

    // -------------------------
    // ProducersApi mappings
    // -------------------------

    @Override
    public List<SearchProducerNameResponse> getEservicesProducers(Integer limit, Integer offset, String producerName) {
        return performOperation(() -> producersApi.getEservicesProducersWithHttpInfo(limit, offset, producerName))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero producers (response non 2xx o body nullo)"
                ));
    }

    // ============================================================
    // NEW: probingStatistics wrappers (StatusApi + TelemetryApi)
    // ============================================================

    /**
     * StatusApi del probingStatistics (GET /status -> 204 se ok).
     */
    @Override
    public void getStatisticsHealthStatus() {
        performOperation(() -> statisticsStatusApi.getHealthStatusWithHttpInfo()).orElse(null);
    }

    /**
     * TelemetryApi: GET /telemetryData/eservices/{eserviceRecordId}
     */
    @Override
    public it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse
    statisticsEservices(Long eserviceRecordId, Integer pollingFrequency) {

        return performOperation(() -> telemetryApi.statisticsEservicesWithHttpInfo(eserviceRecordId, pollingFrequency))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero statistiche e-service (response non 2xx o body nullo)"
                ));
    }

    /**
     * TelemetryApi: GET /telemetryData/eservices/filtered/{eserviceRecordId}
     */
    @Override
    public it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse
    filteredStatisticsEservices(Long eserviceRecordId,
                                Integer pollingFrequency,
                                String startDate,
                                String endDate) {

        return performOperation(() -> telemetryApi.filteredStatisticsEservicesWithHttpInfo(
                eserviceRecordId, pollingFrequency, startDate, endDate
        ))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero statistiche filtrate e-service (response non 2xx o body nullo)"
                ));
    }
}
