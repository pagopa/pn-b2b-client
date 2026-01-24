package it.pagopa.interop.probing.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface IProbingClient extends SettableBearerToken {

    void getProbingApiHealthStatus();

    MainDataEserviceResponse getEserviceMainData(Long eserviceRecordId);

    ProbingDataEserviceResponse getEserviceProbingData(Long eserviceRecordId);

    List<SearchEserviceContent> getAllEservice();

    List<SearchEserviceContent> findEserviceByName(String name);

    List<SearchEserviceContent> findEserviceByProducer(String producer);

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
            Integer frequency,
            LocalTime startTime,
            LocalTime endTime
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

    List<SearchProducerNameResponse> getEservicesProducers(
            Integer limit,
            Integer offset,
            String producerName
    );

    void getStatisticsHealthStatus();

    TelemetryDataEserviceResponse statisticsEservices(
            Long eserviceRecordId,
            Integer pollingFrequency
    );

    TelemetryDataEserviceResponse filteredStatisticsEservices(
            Long eserviceRecordId,
            Integer pollingFrequency,
            String startDate,
            String endDate
    );
}
