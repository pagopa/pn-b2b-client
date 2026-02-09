package it.pagopa.interop.probing.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.generated.openapi.clients.probing.ApiClient;
import it.pagopa.interop.generated.openapi.clients.probing.api.EServicesApi;
import it.pagopa.interop.generated.openapi.clients.probing.api.HealthApi;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.probing.config.ProbingClientConfigs;
import it.pagopa.interop.probing.service.IProbingClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProbingClient extends AbstractClient implements IProbingClient {

    // --- probing (core) ---
    private final HealthApi statusApi;
    private final EServicesApi eServicesApi;

    // --- probingStatistics ---
    private final it.pagopa.interop.generated.openapi.clients.probingStatistics.api.HealthApi statisticsStatusApi;
    private final it.pagopa.interop.generated.openapi.clients.probingStatistics.api.TelemetryApi telemetryApi;

    private final RestTemplate restTemplate;
    private final String basePath;

    private final String probingBearerTokenKms;
    private final String probingBearerTokenTelemetry;

    public ProbingClient(RestTemplate restTemplate,
                         ProbingClientConfigs probingClientConfigs,
                         HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = probingClientConfigs.getBaseUrl();

        this.probingBearerTokenKms = probingClientConfigs.getBearerTokenKms();
        this.probingBearerTokenTelemetry = probingClientConfigs.getBearerTokenTelemetry();

        super.httpCallExecutor = httpCallExecutor;

        // --- probing core ---
        ApiClient probingApiClient = createProbingApiClient(probingBearerTokenKms);
        this.statusApi = new HealthApi(probingApiClient);
        this.eServicesApi = new EServicesApi(probingApiClient);

        // --- probingStatistics ---
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient statsApiClient =
            createStatisticsApiClient(probingBearerTokenTelemetry);
        this.statisticsStatusApi = new it.pagopa.interop.generated.openapi.clients.probingStatistics.api.HealthApi(statsApiClient);
        this.telemetryApi = new it.pagopa.interop.generated.openapi.clients.probingStatistics.api.TelemetryApi(statsApiClient);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        // --- probing core ---
        ApiClient probingApiClient = createProbingApiClient(bearerToken);
        this.statusApi.setApiClient(probingApiClient);
        this.eServicesApi.setApiClient(probingApiClient);

        // --- probingStatistics ---
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient statsApiClient =
                createStatisticsApiClient(bearerToken);
        this.statisticsStatusApi.setApiClient(statsApiClient);
        this.telemetryApi.setApiClient(statsApiClient);
    }

    private ApiClient createProbingApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    private it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient createStatisticsApiClient(String bearerToken) {
        it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient apiClient =
                new it.pagopa.interop.generated.openapi.clients.probingStatistics.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void getProbingApiHealthStatus() {
        performOperation(statusApi::getStatusWithHttpInfo);
    }

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
    public List<SearchEserviceContent> getAllEservice() {
        return searchAll(null, null, null, null);
    }

    @Override
    public List<SearchEserviceContent> findEserviceByName(String name) {
        return searchAll(name, null, null, null);
    }

    @Override
    public List<SearchEserviceContent> findEserviceByProducer(String producer) {
        return searchAll(null, producer, null, null);
    }

    private List<SearchEserviceContent> searchAll(
            String eserviceName,
            String producerName,
            Integer versionNumber,
            List<EserviceStateFE> state
    ) {
        final int limit = 30;
        int offset = 0;

        List<SearchEserviceContent> all = new java.util.ArrayList<>();
        Long totalElements = null;

        while (true) {
            SearchEserviceResponse page = searchEservices(
                    limit,
                    offset,
                    eserviceName,
                    producerName,
                    versionNumber,
                    state
            );

            if (page == null) {
                break;
            }

            if (totalElements == null) {
                totalElements = page.getTotalElements();
            }

            List<SearchEserviceContent> content = page.getContent();
            if (content == null || content.isEmpty()) {
                break;
            }

            all.addAll(content);

            int previousOffset = offset;
            offset += content.size();

            // Protezione anti-loop: se non avanza interrompi
            if (offset == previousOffset) {
                break;
            }

            // Stop “preciso” se totalElements è presente
            if (totalElements != null && offset >= totalElements) {
                break;
            }

            // Fallback: se pagina più corta del limit, siamo all'ultima
            if (content.size() < limit) {
                break;
            }
        }

        return all;
    }


    @Override
    public void updateEserviceFrequency(UUID eserviceId, UUID versionId, Integer frequency, OffsetTime startTime, OffsetTime endTime) {
        ChangeProbingFrequencyRequest req = new ChangeProbingFrequencyRequest();
        req.setFrequency(frequency);
        req.setStartTime(startTime != null ? startTime.format(DateTimeFormatter.ISO_OFFSET_TIME) : null);
        req.setEndTime(endTime != null ? endTime.format(DateTimeFormatter.ISO_OFFSET_TIME) : null);

        performOperation(() -> eServicesApi.updateEserviceFrequencyWithHttpInfo(eserviceId, versionId, req));

        if (!super.httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("Errore durante l'update della frequency dell'e-ervice con id: " + eserviceId);
        }
    }

    @Override
    public void updateEserviceProbingState(UUID eserviceId, UUID versionId, ChangeProbingStateRequest request) {
        performOperation(() -> eServicesApi.updateEserviceProbingStateWithHttpInfo(eserviceId, versionId, request));

        if (!super.httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("Errore durante l'update del probing state dell'e-ervice con id: " + eserviceId);
        }
    }

    @Override
    public void updateEserviceState(UUID eserviceId, UUID versionId, ChangeEserviceStateRequest request) {
        performOperation(() -> eServicesApi.updateEserviceStateWithHttpInfo(eserviceId, versionId, request));

        if (!super.httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("Errore durante l'update dell'eservice state con e-service id: " + eserviceId);
        }
    }

    @Override
    public List<SearchProducerNameResponse> getEservicesProducers(Integer limit, Integer offset, String producerName) {
        return performOperation(() -> eServicesApi.getEservicesProducersWithHttpInfo(limit, offset, producerName))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero producers (response non 2xx o body nullo)"
                ));
    }

    @Override
    public void getStatisticsHealthStatus() {
        performOperation(statisticsStatusApi::getStatusWithHttpInfo);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse statisticsEservices(Long eserviceRecordId, Integer pollingFrequency) {
        return performOperation(() -> telemetryApi.statisticsEservicesWithHttpInfo(eserviceRecordId, pollingFrequency))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero statistiche e-service (response non 2xx o body nullo)"
                ));
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse filteredStatisticsEservices(Long eserviceRecordId, Integer pollingFrequency, String startDate, String endDate) {
        return performOperation(() -> telemetryApi.filteredStatisticsEservicesWithHttpInfo(
                eserviceRecordId, pollingFrequency, startDate, endDate
        ))
                .orElseThrow(() -> new IllegalStateException(
                        "Errore nel recupero statistiche filtrate e-service (response non 2xx o body nullo)"
                ));
    }
}
