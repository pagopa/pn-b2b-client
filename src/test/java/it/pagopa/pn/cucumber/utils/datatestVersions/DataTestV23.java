package it.pagopa.pn.cucumber.utils.datatestVersions;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.EventId;
import lombok.Data;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Data
public class DataTestV23 extends AbstractDataTest {

    private TimelineElementV23 timelineElement;

    public static DataTestV23 convertMap(Map<String, String> data) {

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
        String loadTimelineFrom = getValue(data, LOAD_TIMELINE_FROM.key);
        String notificationCost = getValue(data, DETAILS_NOTIFICATION_COST.key);

        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        try {
            DataTestV23 dataTest = new DataTestV23();
            dataTest.setInputData(data);
            TimelineElementV23 timelineElement = new TimelineElementV23()
                    .legalFactsIds(getListValue(LegalFactsId.class, data, LEGAL_FACT_IDS.key))
                    .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV23()
                            .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                            .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                            .refusalReasons(getListValue(NotificationRefusedErrorV23.class, data, DETAILS_REFUSAL_REASONS.key))
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
                            .notificationCost(notificationCost != null ? Long.parseLong(notificationCost) : null)
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
            dataTest.setLoadTimelineFrom(loadTimelineFrom);

            return dataTest;
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Errore in fase di conversione della dataMap: " + jsonProcessingException.getMessage());
        }
    }

    public static void checkTimelineElementEquality(String timelineEventCategory, TimelineElementV23 elementFromNotification, DataTestV23 dataTest) {
        TimelineElementV23 elementFromTest = dataTest.getTimelineElement();
        TimelineElementDetailsV23 expected = elementFromTest.getDetails();
        TimelineElementDetailsV23 actual = elementFromNotification.getDetails();

        DelegateInfo delegateInfoActual = actual != null ? actual.getDelegateInfo() : null;
        DelegateInfo delegateInfoExpected = expected != null ? expected.getDelegateInfo() : null;

        String error = EQUALITY_CHECK_FAILED + ": ";
        switch (timelineEventCategory) {
            case SEND_COURTESY_MESSAGE -> {
                if (expected != null) {
                    assertThat(actual.getDigitalAddress()).as(error + EQUALITY_DIGITAL_ADDRESS).isEqualTo(expected.getDigitalAddress());
                    assertThat(actual.getRecIndex()).as(error + EQUALITY_REC_INDEX).isEqualTo(expected.getRecIndex());
                }
            }
            case REQUEST_REFUSED -> {
                if (expected != null) {
                    Assertions.assertNotNull(actual.getRefusalReasons());
                    assertThat(actual.getRefusalReasons().size()).as(error + EQUALITY_REFUSAL_REASON_SIZE).isEqualTo(expected.getRefusalReasons().size());
                    for (int i = 0; i < actual.getRefusalReasons().size(); i++) {
                        assertThat(actual.getRefusalReasons().get(i).getErrorCode())
                                .as(error + EQUALITY_ERROR_CODE)
                                .isEqualTo(expected.getRefusalReasons().get(i).getErrorCode());
                    }
                }
            }
            case AAR_GENERATION -> {
                if (expected != null) {
                    assertThat(actual.getGeneratedAarUrl()).as(error + EQUALITY_GENERATED_AAR_URL).isNotNull();
                }
            }
            case SEND_DIGITAL_FEEDBACK -> {
                if (expected != null) {
                    assertThat(actual.getResponseStatus()).as(error + EQUALITY_RESPONSE_STATUS).isNotNull();
                    assertThat(actual.getResponseStatus().getValue()).as(error + EQUALITY_RESPONSE_STATUS_VALUE).isEqualTo(expected.getResponseStatus().getValue());
                    assertThat(actual.getDigitalAddress()).as(error + EQUALITY_DIGITAL_ADDRESS).isEqualTo(expected.getDigitalAddress());
                    assertThat(actual.getSendingReceipts().size()).as(error + EQUALITY_SENDING_RECEIPTS_SIZE).isEqualTo(expected.getSendingReceipts().size());
                    for (int i = 0; i < actual.getSendingReceipts().size(); i++) {
                        assertThat(actual.getSendingReceipts().get(i)).as("Il sendingReceipt non dev'essere null").isNotNull();
                        assertThat(actual.getSendingReceipts().get(i).getId()).as("L'ID del sendingReceipt non dev'essere null").isNotNull();
                        assertThat(actual.getSendingReceipts().get(i).getSystem()).as("Il System del sendingReceipt non dev'essere null").isNotNull();
                    }
                }
            }
            case REQUEST_ACCEPTED, DIGITAL_SUCCESS_WORKFLOW, DIGITAL_FAILURE_WORKFLOW -> {
                assertThat(elementFromNotification.getLegalFactsIds()).as(error + EQUALITY_LEGAL_FACTS_IDS).isNotNull();
                assertThat(elementFromNotification.getLegalFactsIds().size()).as(error + EQUALITY_LEGAL_FACTS_IDS_SIZE).isEqualTo(elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    assertThat(elementFromNotification.getLegalFactsIds().get(i).getCategory())
                            .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_CATEGORY)
                            .isEqualTo(elementFromTest.getLegalFactsIds().get(i).getCategory());
                    assertThat(elementFromNotification.getLegalFactsIds().get(i).getKey())
                            .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_KEY)
                            .isNotNull();
                }
                if ((timelineEventCategory.equals(DIGITAL_SUCCESS_WORKFLOW) || timelineEventCategory.equals(DIGITAL_FAILURE_WORKFLOW)) && expected != null) {
                    assertThat(actual.getDigitalAddress()).as(error + EQUALITY_DIGITAL_ADDRESS).isEqualTo(expected.getDigitalAddress());
                }
            }
            case SEND_DIGITAL_DOMICILE -> {
                if (expected != null) {
                    assertThat(actual.getDigitalAddress()).as(error + EQUALITY_DIGITAL_ADDRESS).isEqualTo(expected.getDigitalAddress());
                }
            }
            case GET_ADDRESS -> {
                if (expected != null) {
                    assertThat(actual.getDigitalAddressSource()).as(error + EQUALITY_DIGITAL_ADDRESS_SOURCE).isEqualTo(expected.getDigitalAddressSource());
                    assertThat(actual.getIsAvailable()).as(error + EQUALITY_IS_AVAILABLE).isEqualTo(expected.getIsAvailable());
                }
            }
            case SEND_ANALOG_FEEDBACK -> {
                if (expected != null) {
                    if (expected.getDeliveryDetailCode() != null) {
                        assertThat(actual.getDeliveryDetailCode()).as(error + EQUALITY_DELIVERY_DETAIL_CODE).isEqualTo(expected.getDeliveryDetailCode());
                    }
                    //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                    if (expected.getPhysicalAddress() != null) {
                        if (B2bUtils.objectHasAllFieldsNull(expected.getPhysicalAddress())) {
                            assertThat(actual.getPhysicalAddress()).as(error + EQUALITY_PHYSICAL_ADDRESS).isNotNull();
                        } else {
                            B2bUtils.compareActualAndExpected(error + EQUALITY_PHYSICAL_ADDRESS, actual.getPhysicalAddress(), expected.getPhysicalAddress());
                        }
                    }
                    //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                    if (expected.getResponseStatus() != null && expected.getResponseStatus().getValue() != null) {
                        assertThat(expected.getResponseStatus().getValue()).as(error + EQUALITY_RESPONSE_STATUS_VALUE).isEqualTo(actual.getResponseStatus().getValue());
                    }
                    if (expected.getDeliveryFailureCause() != null) {
                        List<String> failureCauses = Arrays.asList(expected.getDeliveryFailureCause().split(" "));
                        assertThat(failureCauses).asList()
                                .as(error + EQUALITY_FAILURE_CAUSES)
                                .contains(elementFromNotification.getDetails().getDeliveryFailureCause());
                    }
                }
            }
            case SEND_ANALOG_PROGRESS, SEND_SIMPLE_REGISTERED_LETTER_PROGRESS -> {
                if (expected != null) {
                    if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                        assertThat(elementFromNotification.getLegalFactsIds().size()).as(error + EQUALITY_LEGAL_FACTS_IDS_SIZE).isEqualTo(elementFromTest.getLegalFactsIds().size());
                        for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                            assertThat(elementFromNotification.getLegalFactsIds().get(i).getCategory())
                                    .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_CATEGORY)
                                    .isEqualTo(elementFromTest.getLegalFactsIds().get(i).getCategory());
                            assertThat(elementFromNotification.getLegalFactsIds().get(i).getKey())
                                    .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_KEY)
                                    .isNotNull();
                        }
                    }
                    if (Objects.nonNull(expected.getDeliveryDetailCode())) {
                        assertThat(actual.getDeliveryDetailCode()).as(error + EQUALITY_DELIVERY_DETAIL_CODE).isEqualTo(expected.getDeliveryDetailCode());
                    }
                    if (Objects.nonNull(expected.getAttachments())) {
                        assertThat(actual.getAttachments()).as(error + EQUALITY_ATTACHMENTS_NULL).isNotNull();
                        assertThat(actual.getAttachments().size()).as(error + EQUALITY_ATTACHMENTS_SIZE).isEqualTo(expected.getAttachments().size());
                        for (int i = 0; i < actual.getAttachments().size(); i++) {
                            List<String> documentTypes = Arrays.asList(expected.getAttachments().get(i).getDocumentType().split(" "));
                            assertThat(documentTypes).asList()
                                    .as(error + EQUALITY_DOCUMENT_TYPE)
                                    .contains(actual.getAttachments().get(i).getDocumentType());
                        }
                    }

                    if (Objects.nonNull(expected.getDeliveryFailureCause())) {
                        List<String> failureCauses = Arrays.asList(expected.getDeliveryFailureCause().split(" "));
                        assertThat(failureCauses).asList()
                                .as(error + EQUALITY_DELIVERY_FAILURE_CAUSE)
                                .contains(elementFromNotification.getDetails().getDeliveryFailureCause());
                    }
                }
            }
            case ANALOG_SUCCESS_WORKFLOW, PREPARE_SIMPLE_REGISTERED_LETTER -> {
                //ignorare Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull), non è vero
                if (expected != null && expected.getPhysicalAddress() != null) {
                    B2bUtils.compareActualAndExpected(error + EQUALITY_PHYSICAL_ADDRESS, actual.getPhysicalAddress(), expected.getPhysicalAddress());
                }
            }
            case SEND_SIMPLE_REGISTERED_LETTER -> {
                if (expected != null) {
                    B2bUtils.compareActualAndExpected(error + EQUALITY_PHYSICAL_ADDRESS, actual.getPhysicalAddress(), expected.getPhysicalAddress());
                    assertThat(actual.getAnalogCost()).as(error + EQUALITY_ANALOG_COST).isEqualTo(expected.getAnalogCost());
                }
            }
            case NOTIFICATION_VIEWED -> {
                assertThat(elementFromNotification.getLegalFactsIds()).as(error + EQUALITY_LEGAL_FACT_ID).isNotNull();
                assertThat(elementFromNotification.getLegalFactsIds().size()).as(error + EQUALITY_LEGAL_FACTS_IDS_SIZE).isEqualTo(elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    assertThat(elementFromNotification.getLegalFactsIds().get(i).getCategory())
                            .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_CATEGORY)
                            .isEqualTo(elementFromTest.getLegalFactsIds().get(i).getCategory());
                    assertThat(elementFromNotification.getLegalFactsIds().get(i).getKey())
                            .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " + " + (i + 1) + " " + EQUALITY_KEY)
                            .isNotNull();
                }
                if (delegateInfoExpected != null) {
                    assertThat(delegateInfoActual.getTaxId()).as(error + EQUALITY_DELEGATE_TAX_ID).isEqualTo(delegateInfoExpected.getTaxId());
                    assertThat(delegateInfoActual.getDelegateType()).as(error + EQUALITY_DELEGATE_TYPE).isEqualTo(delegateInfoExpected.getDelegateType());
                    assertThat(delegateInfoActual.getDenomination()).as(error + EQUALITY_DELEGATE_DENOMINATION).isEqualTo(delegateInfoExpected.getDenomination());
                }
            }
            case COMPLETELY_UNREACHABLE -> {
                if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                    assertThat(elementFromNotification.getLegalFactsIds().size()).as(error + EQUALITY_LEGAL_FACTS_IDS_SIZE).isEqualTo(elementFromTest.getLegalFactsIds().size());
                    for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                        assertThat(elementFromNotification.getLegalFactsIds().get(i).getCategory())
                                .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " " + (i + 1) + " " + EQUALITY_CATEGORY)
                                .isEqualTo(elementFromTest.getLegalFactsIds().get(i).getCategory());
                        assertThat(elementFromNotification.getLegalFactsIds().get(i).getKey())
                                .as(error + EQUALITY_LEGAL_FACT_ID_NUMBER + " + " + (i + 1) + " " + EQUALITY_KEY)
                                .isNotNull();
                    }
                }
            }
            case REFINEMENT, SCHEDULE_REFINEMENT -> {
                if (expected != null) {
                    assertThat(actual.getRecIndex()).as(error + EQUALITY_REC_INDEX).isEqualTo(expected.getRecIndex());
                }
            }
            default -> throw new IllegalArgumentException(INVALID_TIMELINE_CATEGORY + timelineEventCategory);
        }
    }

    public String getTimelineEventId(String timelineEventCategory, String iun) {
        EventId event = getEventId(iun, this);
        return B2bUtils.getTimelineEventId(event, timelineEventCategory);
    }

    private static EventId getEventId(String iun, DataTestV23 dataFromTest) {
        TimelineElementV23 timelineElement = dataFromTest.getTimelineElement();
        TimelineElementDetailsV23 timelineElementDetails = timelineElement.getDetails();
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