package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV21;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.utils.NotificationValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.TAX_ID;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationUtilsV21 extends B2bUtils {

    @Autowired
    public NotificationUtilsV21(ApplicationContext context, IPnPaB2bClient b2bClient, PnPollingFactory pollingFactory) {
        super(context, b2bClient, pollingFactory);
    }

    public synchronized NewNotificationRequestV21 convertNotificationRequest(Map<String, String> data) {
        NewNotificationRequestV21 notificationRequest = (new NewNotificationRequestV21()
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
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
                                        NewNotificationRequestV21.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
                .paFee(getValue(data, PA_FEE.key) == null ? null : Integer.parseInt(getValue(data, PA_FEE.key)))
                .vat(getValue(data, VAT.key) == null ? null : Integer.parseInt(getValue(data, VAT.key)))
                .addDocumentsItem(getValue(data, DOCUMENT.key) == null ? null : newDocument(getDefaultValue(DOCUMENT.key)))
                .pagoPaIntMode(
                        (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
                                NewNotificationRequestV21.PagoPaIntModeEnum.SYNC :
                                (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC") ?
                                        NewNotificationRequestV21.PagoPaIntModeEnum.ASYNC : null))));
        //.vat(getValue(data, VAT.key) == null ?  null : Integer.parseInt(getValue(data, VAT.key)))
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notificationRequest;
    }

    public synchronized NotificationRecipientV21 convertNotificationRecipient(Map<String, String> data) {
        List<NotificationPaymentItem> listPayment;

        NotificationRecipientV21 notificationRecipient = (new NotificationRecipientV21()
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
                                NotificationRecipientV21.RecipientTypeEnum.PF : NotificationRecipientV21.RecipientTypeEnum.PG)))
                //GESTIONE ISTANZE DI PAGAMENTI
        );
        //N PAGAMENTI
        if (getValue(data, NotificationValue.PAYMENT.key) != null && getValue(data, PAYMENT_MULTY_NUMBER.key) != null && !getValue(data, PAYMENT_MULTY_NUMBER.key).isEmpty()) {
            listPayment = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
                addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
                        (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NO") ?
                                null :
                                new PagoPaPayment()
                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key))
                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ?
                                                null : newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));

                //LOAD METADATI F24
                if (getValue(data, PAYMENT_F24.key) != null && getValue(data, PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_FLAT")) {
                    addPaymentsItem.f24(
                            new F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(newMetadataAttachment("classpath:/METADATA_CORRETTO_FLAT.json")));

                } else if (getValue(data, PAYMENT_F24.key) != null && getValue(data, PAYMENT_F24.key).equalsIgnoreCase("PAYMENT_F24_STANDARD_0")) {
                    addPaymentsItem.f24(
                            new F24Payment()
                                    .title(getValue(data, TITLE_PAYMENT.key) + "_" + i)
                                    .applyCost(getValue(data, PAYMENT_APPLY_COST_F24.key).equalsIgnoreCase("SI"))
                                    .metadataAttachment(newMetadataAttachment("classpath:/METADATA_CORRETTO_0.json")));

                }
                listPayment.add(addPaymentsItem);
            }
            notificationRecipient.setPayments(listPayment);
        }

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

    //metadataAttachments
    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment().contentType(APPLICATION_JSON).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(context, b2bClient, attachment.getRef().getKey(), APPLICATION_JSON);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void metadataAttachmentSetKey(NotificationMetadataAttachment notificationMetadataAttachment, String key) {
        notificationMetadataAttachment.getRef().setKey(key);
    }

    private void metadataAttachmentSetVersionToken(NotificationMetadataAttachment notificationMetadataAttachment, String version) {
        notificationMetadataAttachment.getRef().setVersionToken(version);
    }

    private void metadataAttachmentSetDigests(NotificationMetadataAttachment notificationMetadataAttachment, String sha256) {
        notificationMetadataAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    //payDocument
    public void preloadPayDocument(NewNotificationRequestV21 request) throws IOException {
        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                setAttachmentWithSleep(paymentList);
            }
        }
    }

    private void setAttachmentWithSleep(List<NotificationPaymentItem> paymentList) throws IOException {
        for (NotificationPaymentItem paymentInfo : paymentList) {
            try {
                Thread.sleep(new Random().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (paymentInfo.getPagoPa() != null) {
                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
            }
            if (paymentInfo.getF24() != null) {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    public void verifyNotification(String iun) throws IllegalStateException {
        FullSentNotificationV21 fsn = b2bClient.getSentNotificationV21(iun);
        //Verify Sha
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            NotificationAttachmentDownloadMetadataResponse response = b2bClient.getSentNotificationDocument(iun, docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
        //Verify Attachments
        fsn.getRecipients().stream().filter(recipient -> recipient.getPayments() != null && !recipient.getPayments().isEmpty())
                .forEach(recipient -> {
                    extractAndCheckAttachment(fsn, recipient);
                    extractAttachment(fsn, recipient);
                });
        //Verify LegalFacts format
        List<LegalFactsId> legalFactsIdList = Objects.requireNonNull(fsn.getTimeline().get(0).getLegalFactsIds());
        for (LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(b2bClient, iun, legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
        //Verify status
        if (fsn.getNotificationStatus().getValue().equals(NOTIFICATION_STATUS_REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + fsn.getNotificationStatus());
        }
    }

    private void verifySha256Notification(FullSentNotificationV21 fsn) {
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            NotificationAttachmentDownloadMetadataResponse response = b2bClient.getSentNotificationDocument(fsn.getIun(), docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
    }

    private void extractAndCheckAttachment(FullSentNotificationV21 fsn, NotificationRecipientV21 recipient) {
        if (Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa() != null) {
            NotificationAttachmentDownloadMetadataResponse resp = b2bClient.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), PAGOPA, 0);
            checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
        }
    }

    private void extractAttachment(FullSentNotificationV21 fsn, NotificationRecipientV21 recipient) {
        if (Objects.requireNonNull(recipient.getPayments()).get(0).getF24() != null) {
            NotificationAttachmentDownloadMetadataResponse resp = b2bClient.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), F_24, 0);
            if (resp != null && resp.getRetryAfter() != null && resp.getRetryAfter() > 0) {
                try {
                    Thread.sleep(resp.getRetryAfter() * 3L);
                    b2bClient.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), "F24", 0);
                } catch (InterruptedException exc) {
                    Thread.currentThread().interrupt();
                    throw new PnB2bException(exc.getMessage());
                }
            }
        }
    }

    public NewNotificationResponse uploadNotification(NewNotificationRequestV21 request, String errorType) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> documents = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(new Random().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (doc != null) {
                documents.add(preloadDocument(doc));
            }
        }
        request.setDocuments(documents);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        preloadPayDocument(request);
        //GENERAZIONE DI ERRORI CREATI INTENZIONALMENTE
        addErrorsOnPurpose(request, errorType);

        log.info(NEW_NOTIFICATION_REQUEST, request);
        return b2bClient.sendNewNotificationV21(request);
    }

    public PnPollingResponseV21 waitForEvent(NewNotificationResponse response, String pollingStrategy, String notificationStatus) {
        IPnPollingService pollingService = pollingFactory.getPollingService(getPollingStrategy(pollingStrategy));
        return (PnPollingResponseV21) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(notificationStatus).build());
    }

    public static String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V21;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V21;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V21;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V21;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V21;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione 21: " + pollingStrategy);
        };
    }

    private NewNotificationRequestV21 addErrorsOnPurpose(NewNotificationRequestV21 request, String errorType) throws IOException {
        if (errorType != null) {
            switch (errorType) {
                case NOT_FOUND_NO_PRELOAD -> {
                    NotificationDocument notPreloadedDocument = newDocument("");
                    documentSetKey(notPreloadedDocument, PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF);
                    documentSetVersionToken(notPreloadedDocument, "v1");
                    documentSetDigests(notPreloadedDocument, computeSha256(context, "classpath:/sample.pdf"));
                    request.getDocuments().add(notPreloadedDocument);
                }
                case NOT_FOUND_ON_SAFE_STORAGE -> {
                    NotificationPaymentAttachment attachment = request.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment();
                    attachment.getRef().setKey(PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF);
                }
                case NOT_FOUND_ALLEGATO_JSON -> {
                    NotificationMetadataAttachment metadataAttachment = request.getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment();
                    metadataAttachment.getRef().setKey(PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON);
                }
                case NOT_EQUAL_SHA -> {
                    String sha256 = computeSha256(context, "classpath:/multa.pdf");
                    NotificationPaymentAttachment attachment = request.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment();
                    attachment.getDigests().setSha256(sha256);
                }
                case NOT_EQUAL_SHA_JSON -> {
                    String sha256 = computeSha256(context, "classpath:/multa.pdf");
                    NotificationMetadataAttachment metadataAttachment = request.getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment();
                    metadataAttachment.getDigests().setSha256(sha256);
                }
            }
        }
        return request;
    }
}
