package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV1;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.utils.NotificationValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.TAX_ID;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationUtilsV1 extends B2bUtils {

    @Autowired
    public NotificationUtilsV1(ApplicationContext context, IPnPaB2bClient b2bClient, PnPollingFactory pollingFactory) {
        super(context, b2bClient, pollingFactory);
    }

    public synchronized NewNotificationRequest convertNotificationRequest(Map<String, String> data) {
        NewNotificationRequest notificationRequest = (new NewNotificationRequest()
                .subject(getValue(data, SUBJECT.key))
                .cancelledIun(getValue(data, CANCELLED_IUN.key))
                .group(getValue(data, GROUP.key))
                .idempotenceToken(getValue(data, IDEMPOTENCE_TOKEN.key))
                ._abstract(getValue(data, ABSTRACT.key))
                .senderDenomination(getValue(data, SENDER_DENOMINATION.key))
                .senderTaxId(getValue(data, SENDER_TAX_ID.key))
                .paProtocolNumber(getValue(data, PA_PROTOCOL_NUMBER.key))
                .taxonomyCode(getValue(data, TAXONOMY_CODE.key))
                .amount(getValue(data, AMOUNT.key) == null ? null : Integer.parseInt(getValue(data, AMOUNT.key)))
                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ? null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
                .notificationFeePolicy(
                        (getValue(data, NOTIFICATION_FEE_POLICY.key) == null ? null :
                                (getValue(data, NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE") ?
                                        NotificationFeePolicy.FLAT_RATE : NotificationFeePolicy.DELIVERY_MODE)))
                .physicalCommunicationType(
                        (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key) == null ? null :
                                (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890") ?
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequest.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .addDocumentsItem(getValue(data, DOCUMENT.key) == null ? null : newDocument(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data, PAGOPAINTMODE.key) == null ? null :
                                (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
                                        NewNotificationRequest.PagoPaIntModeEnum.SYNC : NewNotificationRequest.PagoPaIntModeEnum.NONE))));
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    public synchronized NotificationRecipient convertNotificationRecipient(Map<String, String> data) {
        NotificationRecipient notificationRecipient = (new NotificationRecipient()
                .denomination(getValue(data, DENOMINATION.key))
                .taxId(getValue(data, TAX_ID.key))
                //.internalId(getValue(data,INTERNAL_ID.key))
                .digitalDomicile(getValue(data, DIGITAL_DOMICILE.key) == null ? null : (new NotificationDigitalAddress()
                        .type((getValue(data, DIGITAL_DOMICILE_TYPE.key) == null ? null : NotificationDigitalAddress.TypeEnum.PEC))
                        .address(getValue(data, DIGITAL_DOMICILE_ADDRESS.key)))
                )
                .physicalAddress(getValue(data, PHYSICAL_ADDRES.key) == null ? null : new NotificationPhysicalAddress()
                        .address(getValue(data, PHYSICAL_ADDRESS_ADDRESS.key))
                        .addressDetails(getValue(data, PHYSICAL_ADDRESS_DETAILS.key))
                        .municipality(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITY.key))
                        .at(getValue(data, PHYSICAL_ADDRESS_AT.key))
                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
                        .province(getValue(data, PHYSICAL_ADDRESS_PROVINCE.key))
                        .foreignState(getValue(data, PHYSICAL_ADDRESS_STATE.key))
                        .zip(getValue(data, PHYSICAL_ADDRESS_ZIP.key))
                )
                .recipientType((getValue(data, RECIPIENT_TYPE.key) == null ? null :
                        (getValue(data, RECIPIENT_TYPE.key).equalsIgnoreCase("PF") ?
                                NotificationRecipient.RecipientTypeEnum.PF : NotificationRecipient.RecipientTypeEnum.PG)))
                .payment(getValue(data, NotificationValue.PAYMENT.key) == null ? null : new NotificationPaymentInfo()
                                .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                .noticeCodeAlternative(getValue(data, PAYMENT_NOTICE_CODE_OPTIONAL.key).equalsIgnoreCase("SI") ? getDefaultValue(PAYMENT_NOTICE_CODE_OPTIONAL.key) : null)
                                .pagoPaForm(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ?
                                        null : newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))
                        //                  .f24flatRate(getValue(data, PAYMENT_F24_FLAT.key) == null ? null :
                        //                  (getValue(data, PAYMENT_F24_FLAT.key).equalsIgnoreCase("SI")?
                        //                                  utils.newAttachment(getDefaultValue(PAYMENT_F24_FLAT.key)):null))
                        //
                        //                    .f24standard(getValue(data, PAYMENT_F24_STANDARD.key) == null ? null :
                        //                           (getValue(data, PAYMENT_F24_STANDARD.key).equalsIgnoreCase("SI")?
                        //                                  utils.newAttachment(getDefaultValue(PAYMENT_F24_STANDARD.key)):null))
                )
        );
        /* TEST
        if(getValue(data,DIGITAL_DOMICILE.key) != null && !getValue(data,DIGITAL_DOMICILE.key).equalsIgnoreCase(EXCLUDE_VALUE)){
            notificationRecipient = notificationRecipient.digitalDomicile(getValue(data,DIGITAL_DOMICILE.key) == null? null : (new NotificationDigitalAddress()
                    .type((getValue(data,DIGITAL_DOMICILE_TYPE.key) == null?
                            null : NotificationDigitalAddress.TypeEnum.PEC ))
                    .address( getValue(data,DIGITAL_DOMICILE_ADDRESS.key)))
            );
        }

         */
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRecipient;
    }

    //documents

    public NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(context, b2bClient, document.getRef().getKey(), APPLICATION_PDF);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    private void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    //attachments
    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(context, b2bClient, attachment.getRef().getKey(), APPLICATION_PDF);
            attachmentSetKey(attachment, preloadAttachment.getValue1());
            attachmentSetVersionToken(attachment, "v1");
            attachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void attachmentSetKey(NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionToken(NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigests(NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    public void verifyNotification(String iun) {
        FullSentNotification fsn = b2bClient.getSentNotificationV1(iun);
        //Verify Sha
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            NotificationAttachmentDownloadMetadataResponse response = b2bClient.getSentNotificationDocument(iun, docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
        //Verify Attachments
        fsn.getRecipients().stream().filter(recipient -> recipient.getPayment() != null && recipient.getPayment().getPagoPaForm() != null)
                .forEach(recipient -> {
                    int i = fsn.getRecipients().indexOf(recipient);
                    NotificationAttachmentDownloadMetadataResponse resp = b2bClient.getSentNotificationAttachment(fsn.getIun(), i, PAGOPA, 0);
                    checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
                });
        //Verify LegalFacts format
        List<LegalFactsId> legalFactsIdList = fsn.getTimeline().get(0).getLegalFactsIds();
        for (LegalFactsId legalFactsId : legalFactsIdList) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse resp;
            resp = getLegalFact(b2bClient, fsn.getIun(), legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
        //Verify Status
        if (fsn.getNotificationStatus().getValue().equals(NOTIFICATION_STATUS_REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + fsn.getNotificationStatus());
        }
    }

    public NewNotificationResponse uploadNotification(NewNotificationRequest request) throws IOException {
        List<NotificationDocument> documents = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            documents.add(preloadDocument(doc));
        }
        request.setDocuments(documents);
        for (NotificationRecipient recipient : request.getRecipients()) {
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if (paymentInfo != null) {
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
            }
        }
        return b2bClient.sendNewNotificationV1(request);
    }

    public PnPollingResponseV1 waitForEvent(NewNotificationResponse response, String pollingStrategy, String notificationStatus) {
        IPnPollingService pollingService = pollingFactory.getPollingService(getPollingStrategy(pollingStrategy));
        return (PnPollingResponseV1) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(notificationStatus).build());
    }

    public static String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V1;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V1;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V1;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V1;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V1;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione 1: " + pollingStrategy);
        };
    }
}
