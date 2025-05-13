package it.pagopa.pn.cucumber.utils.datatestVersions;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.*;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.EventId;
import lombok.Data;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Data
public class DataTestV21 extends AbstractDataTest {

    private TimelineElementV20 timelineElement;

    public static DataTestV21 convertMap(Map<String, String> data) {

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

        try {
            DataTestV21 dataTest = new DataTestV21();
            dataTest.setInputData(data);
            TimelineElementV20 timelineElement = new TimelineElementV20()
                    .legalFactsIds(getListValue(LegalFactsId.class, data, LEGAL_FACT_IDS.key))
                    .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV20()
                            .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                            .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                            .refusalReasons(getListValue(NotificationRefusedErrorV20.class, data, DETAILS_REFUSAL_REASONS.key))
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
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Errore in fase di conversione della dataMap: " + jsonProcessingException.getMessage());
        }
    }

    public static void checkTimelineElementEquality(String timelineEventCategory, TimelineElementV20 elementFromNotification, DataTestV21 dataTest) {
        TimelineElementV20 elementFromTest = dataTest.getTimelineElement();
        TimelineElementDetailsV20 detailsFromNotification = elementFromNotification.getDetails();
        TimelineElementDetailsV20 detailsFromTest = elementFromTest.getDetails();
        DelegateInfo delegateInfoFromTest = detailsFromTest != null ? detailsFromTest.getDelegateInfo() : null;
        DelegateInfo delegateInfoFromNotification = detailsFromNotification != null ? detailsFromNotification.getDelegateInfo() : null;

        String error = EQUALITY_CHECK_FAILED + ": " + timelineEventCategory;
        switch (timelineEventCategory) {
            case SEND_COURTESY_MESSAGE -> {
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                    Assertions.assertEquals(detailsFromNotification.getRecIndex(), detailsFromTest.getRecIndex());
                }
            }
            case REQUEST_REFUSED -> {
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getRefusalReasons());
                    Assertions.assertEquals(detailsFromNotification.getRefusalReasons().size(), detailsFromTest.getRefusalReasons().size());
                    for (int i = 0; i < detailsFromNotification.getRefusalReasons().size(); i++) {
                        Assertions.assertEquals(detailsFromNotification.getRefusalReasons().get(i).getErrorCode(), detailsFromTest.getRefusalReasons().get(i).getErrorCode());
                    }
                }
            }
            case AAR_GENERATION -> {
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getGeneratedAarUrl());
                }
            }
            case SEND_DIGITAL_FEEDBACK -> {
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getResponseStatus());
                    Assertions.assertEquals(detailsFromNotification.getResponseStatus().getValue(), detailsFromTest.getResponseStatus().getValue());
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                    Assertions.assertEquals(detailsFromNotification.getSendingReceipts().size(), detailsFromTest.getSendingReceipts().size());
                    for (int i = 0; i < detailsFromNotification.getSendingReceipts().size(); i++) {
                        Assertions.assertEquals(detailsFromNotification.getSendingReceipts().get(i), detailsFromTest.getSendingReceipts().get(i));
                    }
                }
            }
            case REQUEST_ACCEPTED -> {
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
            }
            case SEND_DIGITAL_DOMICILE -> {
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                }
            }
            case DIGITAL_SUCCESS_WORKFLOW, DIGITAL_FAILURE_WORKFLOW -> {
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                }
            }
            case GET_ADDRESS -> {
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddressSource(), detailsFromTest.getDigitalAddressSource());
                    Assertions.assertEquals(detailsFromNotification.getIsAvailable(), detailsFromTest.getIsAvailable());
                }
            }
            case SEND_ANALOG_FEEDBACK -> {
                if (detailsFromTest != null) {
                    if (detailsFromTest.getDeliveryDetailCode() != null) {
                        Assertions.assertEquals(detailsFromTest.getDeliveryDetailCode(), detailsFromNotification.getDeliveryDetailCode());
                    }
                    //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                    if (detailsFromTest.getPhysicalAddress() != null) {
                        Assertions.assertEquals(detailsFromTest.getPhysicalAddress(), detailsFromNotification.getPhysicalAddress());
                    }
                    //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                    if (detailsFromTest.getResponseStatus() != null && detailsFromTest.getResponseStatus().getValue() != null) {
                        Assertions.assertEquals(detailsFromTest.getResponseStatus().getValue(), detailsFromNotification.getResponseStatus().getValue());
                    }
                    if (detailsFromTest.getDeliveryFailureCause() != null) {
                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
                        assertThat(failureCauses).asList().contains(elementFromNotification.getDetails().getDeliveryFailureCause());
                    }
                }
            }
            case SEND_ANALOG_PROGRESS, SEND_SIMPLE_REGISTERED_LETTER_PROGRESS -> {
                if (detailsFromTest != null) {
                    if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                        Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                        for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                            Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                            Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                        }
                    }
                    if (Objects.nonNull(detailsFromTest.getDeliveryDetailCode())) {
                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                    }
                    if (Objects.nonNull(detailsFromTest.getAttachments())) {
                        Assertions.assertNotNull(detailsFromNotification.getAttachments());
                        Assertions.assertEquals(detailsFromNotification.getAttachments().size(), detailsFromTest.getAttachments().size());

                        for (int i = 0; i < detailsFromNotification.getAttachments().size(); i++) {
                            List<String> documentTypes = Arrays.asList(detailsFromTest.getAttachments().get(i).getDocumentType().split(" "));
                            Assertions.assertTrue(documentTypes.contains(detailsFromNotification.getAttachments().get(i).getDocumentType()));
                        }
                    }

                    if (Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
                        Assertions.assertEquals(Boolean.TRUE, failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()));
                    }
                }
            }
            case ANALOG_SUCCESS_WORKFLOW, PREPARE_SIMPLE_REGISTERED_LETTER -> {
                //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                if (detailsFromTest != null && detailsFromTest.getPhysicalAddress() != null) {
                    Assertions.assertEquals(detailsFromTest.getPhysicalAddress(), detailsFromNotification.getPhysicalAddress());
                }
            }
            case SEND_SIMPLE_REGISTERED_LETTER -> {
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
                    Assertions.assertEquals(detailsFromNotification.getAnalogCost(), detailsFromTest.getAnalogCost());
                }
            }
            case NOTIFICATION_VIEWED -> {
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                if (delegateInfoFromTest != null) {
                    Assertions.assertEquals(delegateInfoFromNotification.getTaxId(), delegateInfoFromTest.getTaxId());
                    Assertions.assertEquals(delegateInfoFromNotification.getDelegateType(), delegateInfoFromTest.getDelegateType());
                    Assertions.assertEquals(delegateInfoFromNotification.getDenomination(), delegateInfoFromTest.getDenomination());
                }
            }
            case COMPLETELY_UNREACHABLE -> {
                if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                    assert elementFromNotification.getLegalFactsIds() != null;
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                }
                for (int i = 0; i < Objects.requireNonNull(elementFromNotification.getLegalFactsIds()).size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
            }
            case REFINEMENT -> {
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getRecIndex(), detailsFromTest.getRecIndex());
                }
            }
            default ->
                    throw new IllegalArgumentException("Valore non valido per timelineEventCategory: " + timelineEventCategory);
        }
    }

    public String getTimelineEventId(String timelineEventCategory, String iun) {
        EventId event = getEventId(iun, this);
        return B2bUtils.getTimelineEventId(event, timelineEventCategory);
    }

    private static EventId getEventId(String iun, DataTestV21 dataFromTest) {
        TimelineElementV20 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV20 timelineElementDetails = timelineElement.getDetails();
        DigitalAddress digitalAddress = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddress();
        DigitalAddressSource digitalAddressSource = timelineElementDetails == null ? null : timelineElementDetails.getDigitalAddressSource();

        EventId event = new EventId();
        event.setIun(iun);
        event.setRecIndex(timelineElementDetails == null ? null : timelineElementDetails.getRecIndex());
        event.setCourtesyAddressType(digitalAddress == null ? null : digitalAddress.getType());
        event.setSource(digitalAddressSource == null ? null : digitalAddressSource.getValue());
        event.setIsFirstSendRetry(dataFromTest.isFirstSendRetry());
        event.setSentAttemptMade(timelineElementDetails == null ? null : timelineElementDetails.getSentAttemptMade());
        event.setProgressIndex(dataFromTest.getProgressIndex());
        return event;
    }
}