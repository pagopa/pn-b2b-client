package it.pagopa.pn.cucumber.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.utils.datatestVersions.AbstractDataTest;
import lombok.Data;

import java.util.Map;

import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

/**
 * TODO: Al momento la tengo perché è usata anche in altri punti fuori da AvanzamentoNotificheB2BSteps ma poi diventerà superflua
 */
@Data
public class DataTest extends AbstractDataTest {

    private TimelineElementV26 timelineElement;

    @DataTableType
    public synchronized DataTest convertTimelineElement(Map<String, String> data) throws JsonProcessingException {
        String recIndex = getValue(data, DETAILS_REC_INDEX.key);
        String sentAttemptMade = getValue(data, DETAILS_SENT_ATTEMPT_MADE.key);
        String retryNumber = getValue(data, DETAILS_RETRY_NUMBER.key);
        String responseStatus = getValue(data, DETAILS_RESPONSE_STATUS.key);
        String digitalAddressSource = getValue(data, DETAILS_DIGITAL_ADDRESS_SOURCE.key);
        String isAvailable = getValue(data, DETAILS_IS_AVAILABLE.key);
        String isFirstRetry = getValue(data, IS_FIRST_SEND_RETRY.key);
        String progressIndex = getValue(data, PROGRESS_INDEX.key);
        String analogCost = getValue(data, DETAILS_ANALOG_COST.key);
        String pollingTime = getValue(data, POLLING_TIME.key);
        String numCheck = getValue(data, NUM_CHECK.key);
        String pollingType = getValue(data, POLLING_TYPE.key);
        String loadTimeline = getValue(data, LOAD_TIMELINE.key);

        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        DataTest dataTest = new DataTest();
        TimelineElementV26 timelineElement = new TimelineElementV26()
                .legalFactsIds(getListValue(LegalFactsIdV20.class, data, LEGAL_FACT_IDS.key))
                .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV26()
                        .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                        .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                        .refusalReasons(getListValue(NotificationRefusedErrorV25.class, data, DETAILS_REFUSAL_REASONS.key))
                        .generatedAarUrl(getValue(data, DETAILS_GENERATED_AAR_URL.key))
                        .responseStatus(responseStatus != null ? ResponseStatus.valueOf(responseStatus) : null)
                        .digitalAddressSource(digitalAddressSource != null ? DigitalAddressSource.valueOf(digitalAddressSource) : null)
                        .sentAttemptMade(sentAttemptMade != null ? Integer.parseInt(sentAttemptMade) : null)
                        .retryNumber(retryNumber != null ? Integer.parseInt(retryNumber) : null)
                        .sendingReceipts(getListValue(SendingReceipt.class, data, DETAILS_SENDING_RECEIPT.key))
                        .isAvailable(isAvailable != null ? Boolean.valueOf(getValue(data, DETAILS_IS_AVAILABLE.key)) : null)
                        .deliveryDetailCode(getValue(data, DETAILS_DELIVERY_DETAIL_CODE.key))
                        .deliveryFailureCause(getValue(data, DETAILS_DELIVERY_FAILURE_CAUSE.key))
                        .attachments(getListValue(AttachmentDetails.class, data, DETAILS_ATTACHMENTS.key))
                        .physicalAddress(getObjValue(PhysicalAddress.class, data, DETAILS_PHYSICALADDRESS.key))
                        .analogCost(analogCost != null ? Integer.parseInt(analogCost) : null)
                        .delegateInfo(getObjValue(DelegateInfo.class, data, DETAILS_DELEGATE_INFO.key))
                );

        // IMPORTANT: no empty data check; enrich with new checks if it is needed
        if (timelineElement.getDetails() != null || timelineElement.getLegalFactsIds() != null) {
            dataTest.setTimelineElement(timelineElement);
        }
        dataTest.setFirstSendRetry(isFirstRetry != null ? Boolean.valueOf(isFirstRetry) : null);
        dataTest.setProgressIndex(progressIndex != null ? Integer.parseInt(progressIndex) : null);
        dataTest.setPollingTime(pollingTime != null ? Integer.parseInt(pollingTime) : null);
        dataTest.setPollingType(pollingType);
        dataTest.setNumCheck(numCheck != null ? Integer.parseInt(numCheck) : null);
        dataTest.setLoadTimeline(loadTimeline != null ? Boolean.valueOf(loadTimeline) : null);

        return dataTest;
    }
}