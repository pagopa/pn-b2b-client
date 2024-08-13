package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.*;
import it.pagopa.pn.client.b2b.pa.polling.impl.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPaB2bUtils {
    @AllArgsConstructor
    @Data
    @ToString
    public static class Pair<K, E> {
        K value1;
        E value2;
    }
    public static final String PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF = "PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf";
    public static final String PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON ="PN_F24_META-ab2acab392d042a1afd66f59732791f2.json";
    public static final String LEGAL_FACT_IS_NOT_A_PDF = "LegalFact is not a PDF ";
    public static final String WRONG_STATUS = "WRONG STATUS: ";
    private final RestTemplate restTemplate;
    private final ApplicationContext ctx;
    @Setter
    private IPnPaB2bClient client;
    private PnPollingFactory pollingFactory;
    private final IPnRaddFsuClient raddFsuClient;
    private final IPnRaddAlternativeClient raddAltClient;
    private static final String ACCEPTED = "ACCEPTED";
    private static final String REFUSED = "REFUSED";
    public static final String PAGOPA = "PAGOPA";
    public static final String F_24 = "F24";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL = "Attachment: resourceKey = {}, sha256 = {}, secret = {}, presignedUrl = {}";
    public static final String SHA_256_DIFFERS = "SHA256 differs ";
    public static final String NEW_NOTIFICATION_REQUEST = "New Notification Request {}";
    public static final String NEW_NOTIFICATION_REQUEST_RESPONSE = "New Notification Request response {}";
    private static final String LOAD_TO_PRESIGNED = "LOAD_TO_PRESIGNED";
    private static final String LOAD_TO_PRESIGNED_METADATI = "LOAD_TO_PRESIGNED_METADATI";
    private static final String NEW_NOTIFICATION_IUN = "New Notification\n IUN {}";
    private final Random random = new Random();


    @Autowired
    public PnPaB2bUtils(ApplicationContext ctx,
                        IPnPaB2bClient client,
                        @Qualifier("defaultRestTemplate") RestTemplate restTemplate,
                        IPnRaddFsuClient raddFsuClient,
                        IPnRaddAlternativeClient raddAltClient,
                        PnPollingFactory pollingFactory) {

        this.restTemplate = restTemplate;
        this.ctx = ctx;
        this.client = client;
        this.raddFsuClient = raddFsuClient;
        this.raddAltClient = raddAltClient;
        this.pollingFactory = pollingFactory;
    }

    public void setClient(IPnPaB2bClient client, PnPollingFactory pollingFactory){
        this.client = client;
        this.pollingFactory = pollingFactory;
    }

    public NewNotificationResponse uploadNotification(NewNotificationRequestV23 request) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(this.random.nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (doc != null) {
                newdocs.add(this.preloadDocument(doc));
            }
        }
        request.setDocuments(newdocs);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        preloadPayDocumentV23(request);
        return getAndCheckSendNewNotificationV23(request);
    }

    private void preloadPayDocumentV23(NewNotificationRequestV23 request) throws IOException {
        for (NotificationRecipientV23 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                setAttachmentWithSleepV23(paymentList);
            }
        }
    }

    private void setAttachmentWithSleepV23(List<NotificationPaymentItem> paymentList) throws IOException {
        for (NotificationPaymentItem paymentInfo: paymentList) {
            try {
                Thread.sleep(this.random.nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (paymentInfo.getPagoPa()!= null) {
                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
            }
            if (paymentInfo.getF24()!= null) {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse uploadNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest request) throws IOException {
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocumentV1(doc));
        }
        request.setDocuments(newdocs);
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient : request.getRecipients()) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if (paymentInfo != null) {
                paymentInfo.setPagoPaForm(preloadAttachmentV1(paymentInfo.getPagoPaForm()));
            }
        }
        log.info(NEW_NOTIFICATION_REQUEST, request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response = client.sendNewNotificationV1( request );
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        return response;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse uploadNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest request) throws IOException {
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocumentV2(doc));
        }
        request.setDocuments(newdocs);
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient recipient : request.getRecipients()) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if (paymentInfo != null) {
                paymentInfo.setPagoPaForm(preloadAttachmentV2(paymentInfo.getPagoPaForm()));
            }
        }
        log.info(NEW_NOTIFICATION_REQUEST, request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response = client.sendNewNotificationV2(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        return response;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse uploadNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 request) throws IOException {
            //PRELOAD DOCUMENTI NOTIFICA
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(this.random.nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }

            if (doc!= null) {
                newdocs.add(this.preloadDocumentV21(doc));
            }
        }
        request.setDocuments(newdocs);

        //PRELOAD DOCUMENTI DI PAGAMENTO
        preloadPayDocumentV21(request);
        return getAndCheckSendNewNotificationV21(request);
    }

    private void preloadPayDocumentV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 request) throws IOException {

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21 recipient : request.getRecipients()) {
            List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                setAttachmentWithSleepV21(paymentList);
            }
        }
    }

    private void setAttachmentWithSleepV21(List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem> paymentList) throws IOException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem paymentInfo: paymentList) {
            try {
                Thread.sleep(this.random.nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (paymentInfo.getPagoPa()!= null) {
                paymentInfo.getPagoPa().setAttachment(preloadAttachmentV21(paymentInfo.getPagoPa().getAttachment()));
            }
            if (paymentInfo.getF24()!= null) {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    public NewNotificationResponse uploadNotificationAllegatiUgualiPagamento(NewNotificationRequestV23 request) throws IOException {
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocument(doc));
        }
        request.setDocuments(newdocs);

        for (NotificationRecipientV23 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    if(paymentInfo.getPagoPa()!= null) {
                        paymentInfo.getPagoPa().setAttachment(new NotificationPaymentAttachment()
                                .ref(request.getDocuments().get(0).getRef())
                                .digests(request.getDocuments().get(0).getDigests())
                                .contentType(request.getDocuments().get(0).getContentType()));
                    }
                    if(paymentInfo.getF24()!= null) {
                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }
                }

            }
        }
        return getAndCheckSendNewNotificationV23(request);
    }

    private NewNotificationResponse getAndCheckSendNewNotificationV23(NewNotificationRequestV23 request) {
        log.info(NEW_NOTIFICATION_REQUEST, request);
        NewNotificationResponse response = client.sendNewNotification(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        if (response != null) {
            try {
                log.info(NEW_NOTIFICATION_IUN, new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception e) {
                throw new PnB2bException(e.getMessage());
            }
        }
        return response;
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse getAndCheckSendNewNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 request) {
        log.info(NEW_NOTIFICATION_REQUEST, request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse response = client.sendNewNotificationV21(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        if (response != null) {
            try {
                log.info(NEW_NOTIFICATION_IUN, new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception e) {
                throw new PnB2bException(e.getMessage());
            }
        }
        return response;
    }

    public NewNotificationResponse uploadNotificationNotFindAllegato(NewNotificationRequestV23 request, boolean noUpload) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty() && !noUpload) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey(PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF);
        }
        composeNewNotification(request, notificationDocument, true, noUpload, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotFindAllegatoJson(NewNotificationRequestV23 request, boolean noUpload) throws IOException {
        NotificationDocument notificationDocument = null;

        if ((!request.getRecipients().isEmpty()) && !noUpload) {
            NotificationRecipientV23 notificationRecipientV23 = request.getRecipients().get(0);
            Objects.requireNonNull(Objects.requireNonNull(notificationRecipientV23.getPayments()).get(0).getF24()).getMetadataAttachment().getRef().setKey(PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON);
        }
        composeNewNotification(request, notificationDocument, true, noUpload, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotEqualSha(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty()) {
            notificationDocument = request.getDocuments().get(0);
            // the document uploaded to safe storage is multa.pdf
            // I compute a different sha256 and I replace the old one
            String sha256 = computeSha256("classpath:/multa.pdf");
            notificationDocument.getDigests().setSha256(sha256);
        }
        composeNewNotification(request, notificationDocument, true, false, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotEqualShaJson(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getRecipients().isEmpty()) {
            // the document uploaded to safe storage is multa.pdf
            // I compute a different sha256 and I replace the old one
            String sha256 = computeSha256("classpath:/multa.pdf");
            Objects.requireNonNull(Objects.requireNonNull(request.getRecipients().get(0).getPayments()).get(0).getF24()).getMetadataAttachment().getDigests().setSha256(sha256);
        }
        composeNewNotification(request, notificationDocument, true, false, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationWrongExtension(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty()) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("classpath:/sample.txt");
        }
        composeNewNotification(request, notificationDocument, true, false, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationOver15Allegato(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample.pdf");
        composeNewNotification(request, notificationDocument, false, false, 20);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationOverSizeAllegato(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/200MB_PDF.pdf");
        composeNewNotification(request, notificationDocument, false, false, 1);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationInjectionAllegato(NewNotificationRequestV23 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample_injection.xml.pdf");
        composeNewNotification(request, notificationDocument, false, false, 1);
        return sendNewNotification(request);
    }

    private void composeNewNotification(NewNotificationRequestV23 request, NotificationDocument notificationDocument, boolean isAlist, boolean noUpload, int overAllegato) throws IOException {
        List<NotificationDocument> newdocs = new ArrayList<>();
        if (isAlist) {
            for (NotificationDocument doc : request.getDocuments()) {
                if (noUpload) {
                    newdocs.add(this.preloadDocumentWithoutUpload(doc));
                } else {
                    newdocs.add(this.preloadDocument(doc));
                }
            }
        } else {
            for (int i = 0; i < overAllegato; i++) {
                newdocs.add(this.preloadDocument(notificationDocument));
            }
        }
        request.setDocuments(newdocs);
        setAttachementAndMetadata(request, noUpload);
        log.info(NEW_NOTIFICATION_REQUEST, request);
    }

    private NewNotificationResponse sendNewNotification(NewNotificationRequestV23 request) {
        NewNotificationResponse response = client.sendNewNotification(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        return response;
    }

    private void setAttachementAndMetadata(NewNotificationRequestV23 newNotificationRequestV23, boolean noUpload) throws IOException {
        for (NotificationRecipientV23 recipient : newNotificationRequestV23.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                for (NotificationPaymentItem paymentInfo : paymentList) {
                    setPaymentMetadataAndAttachment(paymentInfo, noUpload);
                }
            }
        }
    }

    private void setPaymentMetadataAndAttachment(NotificationPaymentItem paymentInfo, boolean noUpload) throws IOException {
        if (paymentInfo.getPagoPa() != null) {
            paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
        }
        if (paymentInfo.getF24() != null) {
            if (noUpload) {
                paymentInfo.getF24().setMetadataAttachment(preloadNoMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            } else {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    public FullSentNotificationV23 waitForRequestAcceptation(NewNotificationResponse response) {
        PnPollingServiceValidationStatusV23 validationStatusV23 = (PnPollingServiceValidationStatusV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V23);
        PnPollingResponseV23 pollingResponseV23 = validationStatusV23.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());

        return pollingResponseV23.getNotification() == null ? null : pollingResponseV23.getNotification();
    }

    public FullSentNotificationV23 waitForRequestNoAcceptation(NewNotificationResponse response) {
        PnPollingServiceValidationStatusNoAcceptedV23 validationStatusNoAcceptedV23 = (PnPollingServiceValidationStatusNoAcceptedV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_NO_ACCEPTATION_V23);
        PnPollingResponseV23 pollingResponseV23 = validationStatusNoAcceptedV23.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());

        return pollingResponseV23.getNotification() == null ? null : pollingResponseV23.getNotification() ;
    }


    public FullSentNotificationV23 waitForRequestAcceptationShort(NewNotificationResponse response) {

        PnPollingServiceValidationStatusAcceptedShortV23 validationStatusAcceptedShortV23 = (PnPollingServiceValidationStatusAcceptedShortV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_SHORT_V23) ;
        PnPollingResponseV23 pollingResponseV23 = validationStatusAcceptedShortV23.waitForEvent( response.getNotificationRequestId(), PnPollingParameter.builder().value (ACCEPTED).build());
        return pollingResponseV23.getNotification() == null ? null : pollingResponseV23.getNotification();
    }


    public FullSentNotificationV23 waitForRequestAcceptationExtraRapid( NewNotificationResponse response) {

        PnPollingServiceValidationStatusAcceptedExtraRapidV23 validationStatusAcceptedExtraRapidV23 = (PnPollingServiceValidationStatusAcceptedExtraRapidV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_EXTRA_RAPID_V23);
        PnPollingResponseV23 pollingResponseV23 = validationStatusAcceptedExtraRapidV23.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());

        return pollingResponseV23.getNotification() == null ? null : pollingResponseV23.getNotification() ;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification searchForRequestV1( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse status = null;

        status = client.getNotificationRequestStatusV1( response.getNotificationRequestId() );

        log.info("New Notification Request status {}", status.getNotificationRequestStatus());

        String iun = status.getIun();

        return iun == null? null : client.getSentNotificationV1( iun );
    }



    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification waitForRequestAcceptationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response) {
        PnPollingServiceValidationStatusV1 validationStatusV1 = (PnPollingServiceValidationStatusV1) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V1);
            PnPollingResponseV1 pollingResponseV1 = validationStatusV1.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value (ACCEPTED).build());
        return pollingResponseV1.getNotification() == null ? null : pollingResponseV1.getNotification();
    }


    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 waitForRequestAcceptationV2( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response) {

        PnPollingServiceValidationStatusV20 validationStatusV20 = (PnPollingServiceValidationStatusV20) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V20);
        PnPollingResponseV20 pollingResponseV20 = validationStatusV20.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());

        return pollingResponseV20.getNotification() == null ? null : pollingResponseV20.getNotification();
    }

    public boolean waitForRequestNotRefused( NewNotificationResponse response) {

        PnPollingServiceValidationStatusV23 validationStatusV23 = (PnPollingServiceValidationStatusV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V23);
        PnPollingResponseV23 pollingResponseV23 = validationStatusV23.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());

        return pollingResponseV23.getResult();
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21 waitForRequestAcceptationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse response) {

        PnPollingServiceValidationStatusV21 validationStatusV21 = (PnPollingServiceValidationStatusV21) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V21);
        PnPollingResponseV21 pollingResponseV21 = validationStatusV21.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());

        return pollingResponseV21.getNotification() == null ? null : pollingResponseV21.getNotification();
    }


    public String waitForRequestRefused( NewNotificationResponse response) {
        log.info("Request status for " + response.getNotificationRequestId() );
        long startTime = System.currentTimeMillis();
        PnPollingServiceValidationStatusV23 validationStatusV23 = (PnPollingServiceValidationStatusV23) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V23);
        PnPollingResponseV23 pollingResponseV23 = validationStatusV23.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms",(endTime - startTime));
        StringBuilder error = new StringBuilder();
        if (pollingResponseV23.getStatusResponse() != null && pollingResponseV23.getStatusResponse().getErrors()!= null && !pollingResponseV23.getStatusResponse().getErrors().isEmpty()) {
            for (ProblemError err :pollingResponseV23.getStatusResponse().getErrors()) {
                error.append(" ").append(err.getDetail());
            }
        }
        log.info("Detail status {}", error);
        return error.toString();
    }

    public void verifyNotification(FullSentNotificationV23 fsn) throws IllegalStateException {
        verifySha256NotificationV23(fsn);
        getSentNotificationAttachment(fsn);
        verifyLegalFactFormat(fsn.getIun(), Objects.requireNonNull(fsn.getTimeline().get(0).getLegalFactsIds()));
        checkNotificationStatus(fsn.getNotificationStatus());
    }

    private void getSentNotificationAttachment(FullSentNotificationV23 fsn) {
        fsn.getRecipients().stream()
                .filter(recipient -> recipient.getPayments() != null && !recipient.getPayments().isEmpty())
                .forEach(recipient -> {
                    extractAndCheckAttachment(fsn, recipient);
                    extractAttachment(fsn, recipient);
                });
    }

    private void extractAttachment(FullSentNotificationV23 fsn, NotificationRecipientV23 recipient) {
        if (Objects.requireNonNull(recipient.getPayments()).get(0).getF24() != null) {
            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), F_24, 0);
            if (resp != null && resp.getRetryAfter() != null && resp.getRetryAfter() > 0) {
                try {
                    Thread.sleep(resp.getRetryAfter() * 3L);
                    client.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), "F24", 0);
                } catch (InterruptedException exc) {
                    Thread.currentThread().interrupt();
                    throw new PnB2bException(exc.getMessage());
                }
            }
        }
    }

    private void extractAndCheckAttachment(FullSentNotificationV23 fsn, NotificationRecipientV23 recipient) {
        if (Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa() != null) {
            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), PAGOPA, 0);
            checkAttachment(resp);
        }
    }

    public void verifyNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification fsn) throws IllegalStateException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc : fsn.getDocuments()) {
            checkSha256NotificationV1(getSentNotificationDocumentV1(fsn.getIun(), Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()))), Integer.parseInt(doc.getDocIdx()));
        }
        fsn.getRecipients().stream()
                .filter(recipient -> recipient.getPayment() != null &&
                        recipient.getPayment().getPagoPaForm() != null)
                .forEach(recipient -> {
                    int i = fsn.getRecipients().indexOf(recipient);
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp;

                    resp = client.getSentNotificationAttachmentV1(fsn.getIun(), i, PAGOPA);
                    checkAttachmentV1(resp);
                });
        verifyLegalFactFormatV1(fsn.getIun(), Objects.requireNonNull(fsn.getTimeline().get(0).getLegalFactsIds()));
        checkNotificationStatusV1(fsn.getNotificationStatus());
    }

    public void verifyNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 fsn) throws IllegalStateException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            checkSha256Notification(getSentNotificationDocument(fsn.getIun(), docIdx), docIdx);
        }
        fsn.getRecipients().stream()
                .filter(recipient -> recipient.getPayment() != null && recipient.getPayment().getPagoPaForm() != null)
                .forEach(recipient -> {
                    int index = fsn.getRecipients().indexOf(recipient);
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachmentV2(fsn.getIun(), index, PAGOPA);
                    checkAttachmentV2(resp);
                });
        verifyLegalFactFormatV2(fsn.getIun(), Objects.requireNonNull(fsn.getTimeline().get(0).getLegalFactsIds()));
        checkNotificationStatusV2(fsn.getNotificationStatus());
    }

    public void verifyNotificationAndSha256AllegatiPagamento(FullSentNotificationV23 fsn, String attachment) throws IllegalStateException {
        verifySha256NotificationV23(fsn);
        for (int i = 0; i < fsn.getRecipients().size(); i++) {
            NotificationRecipientV23 recipient = fsn.getRecipients().get(i);
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, PAGOPA, 0);
                checkAttachment(resp);
            }
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    Objects.requireNonNull(recipient.getPayments()).get(0).getF24() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24", 0);
                checkAttachment(resp);
            }
        }
    }

    private void verifySha256NotificationV23(FullSentNotificationV23 fsn) {
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            checkSha256Notification(getSentNotificationDocument(fsn.getIun(), docIdx), docIdx);
        }
    }

    private void checkSha256Notification(NotificationAttachmentDownloadMetadataResponse response, int docIdx) {
        byte[] content = downloadFile(response.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(response.getSha256())) {
            throw new IllegalStateException(SHA_256_DIFFERS + docIdx);
        }
    }

    private void checkNotificationStatus(NotificationStatus notificationStatus) {
        if (notificationStatus.equals(NotificationStatus.REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + notificationStatus);
        }
    }

    private void checkNotificationStatusV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus notificationStatus) {
        if (notificationStatus.equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + notificationStatus);
        }
    }

    private void checkNotificationStatusV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatus notificationStatus) {
        if (notificationStatus.equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatus.REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + notificationStatus);
        }
    }

    private void checkSha256NotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse response, int docIdx) {
        byte[] content = downloadFile(response.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(response.getSha256())) {
            throw new IllegalStateException(SHA_256_DIFFERS + docIdx);
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, int docIdx) {
        return client.getSentNotificationDocument(iun, docIdx);
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, int docIdx) {
        return client.getSentNotificationDocumentV1(iun, docIdx);
    }

    private LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactsId) {
        return client.getLegalFact(
                iun,
                LegalFactCategory.SENDER_ACK,
                URLEncoder.encode(legalFactsId, StandardCharsets.UTF_8));
    }

    private void verifyLegalFactFormat(String iun, List<LegalFactsId> legalFactsIdList) {
        for (LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormat(String url, LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException(LEGAL_FACT_IS_NOT_A_PDF+ legalFactsId);
        }
    }

    private void verifyLegalFactFormatV1(String iun, List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId> legalFactsIdList) {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormatV1(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormatV1(String url, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException(LEGAL_FACT_IS_NOT_A_PDF+ legalFactsId);
        }
    }

    private void verifyLegalFactFormatV2(String iun, List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId> legalFactsIdList) {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormatV2(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormatV2(String url, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException(LEGAL_FACT_IS_NOT_A_PDF+ legalFactsId);
        }
    }

    private void checkAttachment(NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new IllegalStateException(SHA_256_DIFFERS + resp.getFilename() );
        }
    }

    private void checkAttachmentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new PnB2bException(SHA_256_DIFFERS + resp.getFilename());
        }
    }

    private void checkAttachmentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new PnB2bException(SHA_256_DIFFERS + resp.getFilename());
        }
    }

    public Pair<String,String> preloadRadFsuDocument( String resourcePath, boolean usePresignedUrl) throws IOException {
        String sha256 = computeSha256(resourcePath);
        DocumentUploadResponse documentUploadResponse = getPreLoadRaddResponse(sha256);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, secret, url);
        if(usePresignedUrl) {
            loadToPresigned( url, secret, sha256, resourcePath );
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    public void preloadRadCSVDocument(String resourcePath, String sha256, RegistryUploadResponse responseUploadCsv, boolean usePresignedUrl) {
        String secret = responseUploadCsv.getSecret();
        String url = responseUploadCsv.getUrl();
        if(usePresignedUrl){
            loadToPresignedCsv( url, secret, sha256, resourcePath );
            log.info("UPLOAD RADD CSV COMPLETE");
        }else{
            log.info("UPLOAD RADD CSV COMPLETE WITHOUT UPLOAD");
        }
    }

    public Pair<String, String> preloadRaddAlternativeDocument(String resourcePath, boolean usePresignedUrl, String operationId) throws IOException {
        String sha256 = computeSha256(resourcePath);
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse documentUploadResponse = getPreLoadRaddAlternativeResponse(sha256, operationId);
        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, secret, url);
        if(usePresignedUrl){
            loadToPresignedZip( url, secret, sha256, resourcePath );
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    private DocumentUploadResponse getPreLoadRaddResponse(String sha256) {
        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest()
                .bundleId("TEST")
                .checksum(sha256)
                .contentType(APPLICATION_PDF);
        return raddFsuClient.documentUpload("1234556", documentUploadRequest);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getPreLoadRaddAlternativeResponse(String sha256, String operationid) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest documentUploadRequest = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest()
                .operationId(operationid)
                .checksum(sha256);
        return raddAltClient.documentUpload("1234556", documentUploadRequest);
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument preloadDocumentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKeyV1(document, preloadDocument.getValue1());
        documentSetVersionTokenV1(document, "v1");
        documentSetDigestsV1(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument preloadDocumentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKeyV20(document, preloadDocument.getValue1());
        documentSetVersionTokenV20(document, "v1");
        documentSetDigestsV20(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument preloadDocumentV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKeyV21(document, preloadDocument.getValue1());
        documentSetVersionTokenV21(document, "v1");
        documentSetDigestsV21(document, preloadDocument.getValue2());
        return document;
    }

    public NotificationDocument preloadDocumentWithoutUpload(NotificationDocument document) throws IOException {
        String resourceName = "classpath:/test.xml";
        Pair<String, String> preloadDocument = preloadGeneric(resourceName, LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKey(attachment, preloadAttachment.getValue1());
            attachmentSetVersionToken(attachment, "v1");
            attachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment preloadAttachmentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKeyV1(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV1(attachment, "v1");
            attachmentSetDigestsV1(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment preloadAttachmentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKeyV20(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV20(attachment, "v1");
            attachmentSetDigestsV20(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment preloadAttachmentV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKeyV21(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV21(attachment, "v1");
            attachmentSetDigestsV21(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment preloadMetadataAttachment(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
            metadataAttachmentSetKeyV21(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionTokenV21(attachment, "v1");
            metadataAttachmentSetDigestsV21(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public NotificationMetadataAttachment preloadNoMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            String resourceName = "classpath:/test.xml";
            Pair<String, String> preloadAttachment = preloadGeneric(resourceName, "");
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public void loadToPresigned(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, APPLICATION_PDF, 0);
    }

    public void loadToPresignedMetadati(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, APPLICATION_JSON, 0);
    }

    public void loadToPresignedZip(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, "application/zip", 0);
    }

    public void loadToPresignedCsv( String url, String secret, String sha256, String resource ) {
        loadToPresigned(url,secret,sha256,resource,"text/csv",0);
    }

    private void loadToPresigned(String url, String secret, String sha256, String resource, String resourceType, int depth) {
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-type", resourceType);
            headers.add("x-amz-checksum-sha256", sha256);
            headers.add("x-amz-meta-secret", secret);
            log.info("headers: {}", headers);
            HttpEntity<Resource> req = new HttpEntity<>(ctx.getResource(resource), headers);
            restTemplate.exchange(URI.create(url), HttpMethod.PUT, req, Object.class);
        } catch (Exception e) {
            if (depth >= 5) {
                throw e;
            }
            log.info("Upload in catch, retry");
            try {
                Thread.sleep(2000);
                log.error("[THREAD IN SLEEP PRELOAD] id: {} , attempt: {} , url: {}, secret: {}, sha256: {}, resourceType: {}", Thread.currentThread().getId(), depth, url, secret, sha256, resourceType);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(ex.getMessage());
            }
            loadToPresigned(url, secret, sha256, resource, resourceType, depth + 1);
        }
    }

    private PreLoadResponse getPreLoadResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256(sha256)
                .contentType(APPLICATION_PDF);
        return client.presignedUploadRequest(
                Collections.singletonList(preLoadRequest)
        ).get(0);
    }

    private PreLoadResponse getPreLoadMetaDatiResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256(sha256)
                .contentType(APPLICATION_JSON);
        return client.presignedUploadRequest(
                Collections.singletonList(preLoadRequest)
        ).get(0);
    }

    public String computeSha256(String resName) throws IOException {
        Resource res = ctx.getResource(resName);
        return computeSha256(res);
    }

    private String computeSha256(Resource res) throws IOException {
        return computeSha256(res.getInputStream());
    }

    public String computeSha256(InputStream inStrm) {
        try (inStrm) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(StreamUtils.copyToByteArray(inStrm));
            return bytesToBase64(encodedhash);
        } catch (IOException | NoSuchAlgorithmException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    private Pair<String, String> preloadGeneric(String resourceName, String loadToMetadata) throws IOException {
        String sha256 = computeSha256(resourceName);
        PreLoadResponse preLoadResponse;

        if (loadToMetadata.equals(LOAD_TO_PRESIGNED_METADATI)) {
            preLoadResponse = getPreLoadMetaDatiResponse(sha256);
        } else {
            preLoadResponse = getPreLoadResponse(sha256);
        }

        String key = preLoadResponse.getKey();
        String secret = preLoadResponse.getSecret();
        String url = preLoadResponse.getUrl();
        log.info("Attachment resourceKey={} sha256={} secret={} presignedUrl={}", resourceName, sha256, secret, url);

        if (loadToMetadata.equals(LOAD_TO_PRESIGNED_METADATI)) {
            loadToPresignedMetadati(url, secret, sha256, resourceName);
        } else if (loadToMetadata.equals(LOAD_TO_PRESIGNED)) {
            loadToPresigned(url, secret, sha256, resourceName);
        }
        return new Pair<>(key, sha256);
    }

    public byte[] downloadFile(String downloadUrl) {
        try {
            URL url = new URL(downloadUrl);
            return IOUtils.toByteArray(url);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly();
        }
    }

    private static String bytesToBase64(byte[] hash) {
        return Base64Utils.encodeToString(hash);
    }

    public FullSentNotificationV23 getNotificationByIun(String iun) {
        return client.getSentNotification(iun);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification getNotificationByIunV1(String iun) {
        return client.getSentNotificationV1(iun);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 getNotificationByIunV2(String iun) {
        return client.getSentNotificationV2(iun);
    }

    public NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument()
                .contentType(APPLICATION_PDF)
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument newDocumentV1(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument newDocumentV2(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument newDocumentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment()
                .contentType(APPLICATION_PDF)
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment newAttachmentV1(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment newAttachmentV2(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment newAttachmentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment()
                .contentType(APPLICATION_PDF)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment()
                .contentType(APPLICATION_JSON)
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment newMetadataAttachmentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment()
                .contentType(APPLICATION_JSON)
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    //metodo per stampa pdf per verifiche manuali
    public void stampaPdfTramiteByte(byte[] file, String path) {
        try {
            // Create file
            OutputStream out = new FileOutputStream(path);
            out.write(file);
            out.close();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
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

    private void attachmentSetKey(NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionToken(NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigests(NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
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


    private void documentSetKeyV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionTokenV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigestsV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDigests().sha256(sha256));
    }

    private void attachmentSetKeyV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionTokenV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigestsV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDigests().sha256(sha256));
    }


    private void documentSetKeyV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionTokenV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigestsV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentDigests().sha256(sha256));
    }

    private void attachmentSetKeyV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionTokenV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigestsV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentDigests().sha256(sha256));
    }

    private void metadataAttachmentSetKeyV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment notificationMetadataAttachment, String key) {
        notificationMetadataAttachment.getRef().setKey(key);
    }

    private void metadataAttachmentSetVersionTokenV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment notificationMetadataAttachment, String version) {
        notificationMetadataAttachment.getRef().setVersionToken(version);
    }

    private void metadataAttachmentSetDigestsV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment notificationMetadataAttachment, String sha256) {
        notificationMetadataAttachment.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentDigests().sha256(sha256));
    }


    private void documentSetKeyV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionTokenV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigestsV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDigests().sha256(sha256));
    }

    private void attachmentSetKeyV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionTokenV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigestsV20(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDigests().sha256(sha256));
    }

    public boolean downloadUrlAndCheckContent(String strUrl, String contentType) {

        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                return StringUtils.equals(connection.getHeaderField("Content-Type"), contentType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOffsetDateTimeFromDate(Instant date) {
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(date, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return offsetDateTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS).format(formatter);
    }

    public int convertToSeconds(String timeStr) {
        int number = Integer.parseInt(timeStr.substring(0, timeStr.length() - 1));
        char unit = timeStr.toLowerCase().charAt(timeStr.length() - 1);

        if (unit == 'm') {
            return number * 60;
        } else if (unit == 'h') {
            return number * 3600;
        } else {
            throw new IllegalArgumentException("Unità di misura non supportata");
        }
    }
}