package it.pagopa.interop.probing.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse;

import java.util.List;
import java.util.UUID;

public interface IProbingClient extends SettableBearerToken {

    // -------------------------
    // StatusApi (probing core)
    // -------------------------

    void getProbingApiHealthStatus();

    // -------------------------
    // EServicesApi
    // -------------------------

    MainDataEserviceResponse getEserviceMainData(Long eserviceRecordId);

    ProbingDataEserviceResponse getEserviceProbingData(Long eserviceRecordId);

    SearchEserviceResponse searchEservices(
            Integer limit,
            Integer offset,
            String eserviceName,
            String producerName,
            Integer versionNumber,
            List<EserviceStateFE> state
    );

    void updateEserviceFrequency(
            UUID eserviceId,
            UUID versionId,
            ChangeProbingFrequencyRequest request
    );

    void updateEserviceProbingState(
            UUID eserviceId,
            UUID versionId,
            ChangeProbingStateRequest request
    );

    void updateEserviceState(
            UUID eserviceId,
            UUID versionId,
            ChangeEserviceStateRequest request
    );

    // -------------------------
    // ProducersApi
    // -------------------------

    List<SearchProducerNameResponse> getEservicesProducers(
            Integer limit,
            Integer offset,
            String producerName
    );

    // ============================================================
    // probing-statistics
    // ============================================================

    /**
     * StatusApi probing-statistics (health check).
     */
    void getStatisticsHealthStatus();

    /**
     * TelemetryApi: statistiche e-service.
     */
    TelemetryDataEserviceResponse statisticsEservices(
            Long eserviceRecordId,
            Integer pollingFrequency
    );

    /**
     * TelemetryApi: statistiche e-service filtrate per periodo.
     */
    TelemetryDataEserviceResponse filteredStatisticsEservices(
            Long eserviceRecordId,
            Integer pollingFrequency,
            String startDate,
            String endDate
    );
}
