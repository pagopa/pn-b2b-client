package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


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

    private static Logger log = LoggerFactory.getLogger(PnPaB2bUtils.class);
    private final RestTemplate restTemplate;
    private final ApplicationContext ctx;
    private final PnB2bClientTimingConfigs timingConfigs;
    @Setter
    private IPnPaB2bClient client;
    private final IPnRaddFsuClient raddFsuClient;
    private final IPnRaddAlternativeClient raddAltClient;
    private static final String loadToPresigned = "loadToPresigned";
    private static final String loadToPresignedMetadati = "loadToPresignedMetadati";


    @Autowired
    public PnPaB2bUtils(ApplicationContext ctx, IPnPaB2bClient client, RestTemplate restTemplate, IPnRaddFsuClient raddFsuClient,
                        IPnRaddAlternativeClient raddAltClient, PnB2bClientTimingConfigs timingConfigs) {
        this.restTemplate = restTemplate;
        this.ctx = ctx;
        this.client = client;
        this.raddFsuClient = raddFsuClient;
        this.raddAltClient = raddAltClient;
        this.timingConfigs = timingConfigs;
    }

    public NewNotificationResponse uploadNotification(NewNotificationRequestV23 request) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(new Random().nextInt(350));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (doc != null) {
                newdocs.add(this.preloadDocument(doc));
            }
        }
        request.setDocuments(newdocs);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        for (NotificationRecipientV23 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                for (NotificationPaymentItem paymentInfo : paymentList) {
                    try {
                        Thread.sleep(new Random().nextInt(350));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (paymentInfo.getPagoPa() != null) {
                        paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    }
                    if (paymentInfo.getF24() != null) {
                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }
                }

                // paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }
        log.info("New Notification Request {}", request);
        NewNotificationResponse response = client.sendNewNotification(request);
        log.info("New Notification Request response {}", response);
        if (response != null) {
            try {
                log.info("New Notification\n IUN {}", new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception ignored) {
            }
        }
        return response;
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
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }
        log.info("New Notification Request {}", request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response = client.sendNewNotificationV1(request);
        log.info("New Notification Request response {}", response);
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
        log.info("New Notification Request {}", request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response = client.sendNewNotificationV2(request);
        log.info("New Notification Request response {}", response);
        return response;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse uploadNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 request) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument doc : request.getDocuments()) {
            try {
                Thread.sleep(new Random().nextInt(350));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (doc != null) {
                newdocs.add(this.preloadDocumentV21(doc));
            }
        }
        request.setDocuments(newdocs);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationRecipientV21 recipient : request.getRecipients()) {
            List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentItem paymentInfo : paymentList) {
                    try {
                        Thread.sleep(new Random().nextInt(350));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (paymentInfo.getPagoPa() != null) {
                        paymentInfo.getPagoPa().setAttachment(preloadAttachmentV21(paymentInfo.getPagoPa().getAttachment()));
                    }
                    if (paymentInfo.getF24() != null) {
                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }
                }
                // paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }
        log.info("New Notification Request {}", request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse response = client.sendNewNotificationV21(request);
        log.info("New Notification Request response {}", response);
        if (response != null) {
            try {
                log.info("New Notification\n IUN {}", new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception ignored) {
            }
        }
        return response;
    }

    public NewNotificationResponse uploadNotificationNotFindAllegato(NewNotificationRequestV23 request, boolean noUpload) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty() && !noUpload) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf");
        }
        composeNewNotification(request, notificationDocument, true, noUpload, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotFindAllegatoJson(NewNotificationRequestV23 request, boolean noUpload) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty() && !noUpload) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf");
        }

        if (!request.getRecipients().isEmpty() && !noUpload) {
            NotificationRecipientV23 notificationRecipientV23 = request.getRecipients().get(0);
            notificationRecipientV23.getPayments().get(0).getF24().getMetadataAttachment().getRef().setKey("PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf");
            //notificationDocument.getRef().setKey("PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf");
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
            request.getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment().getDigests().setSha256(sha256);
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
        log.info("New Notification Request {}", request);
    }

    private NewNotificationResponse sendNewNotification(NewNotificationRequestV23 request) {
        NewNotificationResponse response = client.sendNewNotification(request);
        log.info("New Notification Request response {}", response);
        return response;
    }

    private void setAttachementAndMetadata(NewNotificationRequestV23 newNotificationRequestV23, boolean noUpload) throws IOException {
        for (NotificationRecipientV23 recipient : newNotificationRequestV23.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                for (NotificationPaymentItem paymentInfo : paymentList) {
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

            }
        }

        // for (NotificationRecipientV23 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }

        // for (NotificationRecipientV23 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }
    }

    public FullSentNotificationV23 waitForRequestAcceptation(NewNotificationResponse response) {
        return waitForRequestAcceptation(response, 16, getAcceptedWait());
    }

    public FullSentNotificationV23 waitForRequestNoAcceptation(NewNotificationResponse response) {
        return waitForRequestAcceptation(response, 8, getAcceptedWait());
    }


    public FullSentNotificationV23 waitForRequestAcceptationShort(NewNotificationResponse response) {
        return waitForRequestAcceptation(response, 230, 5000);
    }


    private FullSentNotificationV23 waitForRequestAcceptation(NewNotificationResponse response, int numCheck, int waiting) {
        log.info("Request status for " + response.getNotificationRequestId());
        NewNotificationRequestStatusResponseV23 status = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numCheck; i++) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            status = client.getNotificationRequestStatus(response.getNotificationRequestId());
            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("ACCEPTED".equals(status.getNotificationRequestStatus())) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));
        String iun = status.getIun();
        return iun == null ? null : client.getSentNotification(iun);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification waitForRequestAcceptationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response) {
        log.info("Request status for " + response.getNotificationRequestId());
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse status = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            status = client.getNotificationRequestStatusV1(response.getNotificationRequestId());
            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("ACCEPTED".equals(status.getNotificationRequestStatus())) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));
        String iun = status.getIun();
        return iun == null ? null : client.getSentNotificationV1(iun);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 waitForRequestAcceptationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response) {
        log.info("Request status for " + response.getNotificationRequestId());
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse status = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            status = client.getNotificationRequestStatusV2(response.getNotificationRequestId());
            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("ACCEPTED".equals(status.getNotificationRequestStatus())) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));
        String iun = status.getIun();
        return iun == null ? null : client.getSentNotificationV2(iun);
    }

    public boolean waitForRequestNotRefused(NewNotificationResponse response) {
        log.info("Request status for " + response.getNotificationRequestId());
        NewNotificationRequestStatusResponseV23 status = null;
        long startTime = System.currentTimeMillis();
        boolean rifiutata = false;
        for (int i = 0; i < 8; i++) {
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            status = client.getNotificationRequestStatus(response.getNotificationRequestId());

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("REFUSED".equals(status.getNotificationRequestStatus())) {
                rifiutata = true;
                break;
            }
        }
        return rifiutata;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21 waitForRequestAcceptationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId());
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21 status = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {

            try {
                Thread.sleep(getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            status = client.getNotificationRequestStatusV21(response.getNotificationRequestId());

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("ACCEPTED".equals(status.getNotificationRequestStatus())) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));
        String iun = status.getIun();

        return iun == null ? null : client.getSentNotificationV21(iun);
    }


    public String waitForRequestRefused(NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId());
        NewNotificationRequestStatusResponseV23 status = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {

            try {
                Thread.sleep(getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            status = client.getNotificationRequestStatus(response.getNotificationRequestId());

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ("REFUSED".equals(status.getNotificationRequestStatus())) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));

        String error = null;
        if (status != null && status.getErrors() != null && status.getErrors().size() > 0) {
            for (ProblemError err : status.getErrors()) {
                error = error + " " + err.getDetail();
            }
        }
        log.info("Detail status {}", error);
        return error;
    }

    public void verifyNotification(FullSentNotificationV23 fsn) throws IOException, IllegalStateException {
        verifySha256NotificationV23(fsn);
        int i = 0;
        for (NotificationRecipientV23 recipient : fsn.getRecipients()) {
            if (fsn.getRecipients().get(i).getPayments() != null && !fsn.getRecipients().get(i).getPayments().isEmpty() &&
                    fsn.getRecipients().get(i).getPayments().get(0).getPagoPa() != null) {
                NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA", 0);
                checkAttachment(resp);
            }
            if (fsn.getRecipients().get(i).getPayments() != null && !fsn.getRecipients().get(i).getPayments().isEmpty() &&
                    fsn.getRecipients().get(i).getPayments().get(0).getF24() != null) {
                NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24", 0);
                if (resp != null && resp.getRetryAfter() != null && resp.getRetryAfter() > 0) {
                    try {
                        Thread.sleep(resp.getRetryAfter() * 3L);
                        client.getSentNotificationAttachment(fsn.getIun(), i, "F24", 0);
                    } catch (InterruptedException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }
            i++;
        }
        verifyLegalFactFormat(fsn.getIun(), fsn.getTimeline().get(0).getLegalFactsIds());
        checkNotificationStatus(fsn.getNotificationStatus());
    }

    public void verifyNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification fsn) throws IOException, IllegalStateException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc : fsn.getDocuments()) {
            checkSha256NotificationV1(getSentNotificationDocumentV1(fsn.getIun(), Integer.parseInt(doc.getDocIdx())), Integer.parseInt(doc.getDocIdx()));
        }
        int i = 0;
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient : fsn.getRecipients()) {
            if (fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null) {
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachmentV1(fsn.getIun(), i, "PAGOPA");
                checkAttachmentV1(resp);
            }
            i++;
        }
        verifyLegalFactFormatV1(fsn.getIun(), fsn.getTimeline().get(0).getLegalFactsIds());
        checkNotificationStatusV1(fsn.getNotificationStatus());
    }

    public void verifyNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 fsn) throws IOException, IllegalStateException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(doc.getDocIdx());
            checkSha256Notification(getSentNotificationDocument(fsn.getIun(), docIdx), docIdx);
        }
        int i = 0;
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId : fsn.getTimeline().get(0).getLegalFactsIds()) {
            if (fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null) {
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachmentV2(fsn.getIun(), i, "PAGOPA");
                checkAttachmentV2(resp);
            }
            i++;
        }
        verifyLegalFactFormatV2(fsn.getIun(), fsn.getTimeline().get(0).getLegalFactsIds());
        checkNotificationStatusV2(fsn.getNotificationStatus());
    }

    public void verifyNotificationAndSha256AllegatiPagamento(FullSentNotificationV23 fsn, String attachname) throws IOException, IllegalStateException {
        verifySha256NotificationV23(fsn);
        int i = 0;
        for (NotificationRecipientV23 recipient : fsn.getRecipients()) {
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getPagoPa() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA", 0);
                checkAttachment(resp);
            }
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getF24() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24", 0);
                checkAttachment(resp);
            }
            i++;
        }
    }

    private void verifySha256NotificationV23(FullSentNotificationV23 fsn) {
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(doc.getDocIdx());
            checkSha256Notification(getSentNotificationDocument(fsn.getIun(), docIdx), docIdx);
        }
    }

    private void checkSha256Notification(NotificationAttachmentDownloadMetadataResponse response, int docIdx) {
        byte[] content = downloadFile(response.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(response.getSha256())) {
            throw new IllegalStateException("SHA256 differs " + docIdx);
        }
    }

    private void checkNotificationStatus(NotificationStatus notificationStatus) {
        if (notificationStatus.equals(NotificationStatus.REFUSED)) {
            throw new IllegalStateException("WRONG STATUS: " + notificationStatus);
        }
    }

    private void checkNotificationStatusV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus notificationStatus) {
        if (notificationStatus.equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.REFUSED)) {
            throw new IllegalStateException("WRONG STATUS: " + notificationStatus);
        }
    }

    private void checkNotificationStatusV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatus notificationStatus) {
        if (notificationStatus.equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatus.REFUSED)) {
            throw new IllegalStateException("WRONG STATUS: " + notificationStatus);
        }
    }

    private void checkSha256NotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse response, int docIdx) {
        byte[] content = downloadFile(response.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(response.getSha256())) {
            throw new IllegalStateException("SHA256 differs " + docIdx);
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, int docIdx) {
        return client.getSentNotificationDocument(iun, docIdx);
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, int docIdx) {
        return client.getSentNotificationDocumentV1(iun, docIdx);
    }

    private LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactsId) throws UnsupportedEncodingException {
        return client.getLegalFact(
                iun,
                LegalFactCategory.SENDER_ACK,
                URLEncoder.encode(legalFactsId, StandardCharsets.UTF_8));
    }

    private void verifyLegalFactFormat(String iun, List<LegalFactsId> legalFactsIdList) throws UnsupportedEncodingException {
        for (LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormat(String url, LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId);
        }
    }

    private void verifyLegalFactFormatV1(String iun, List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId> legalFactsIdList) throws UnsupportedEncodingException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormatV1(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormatV1(String url, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId);
        }
    }

    private void verifyLegalFactFormatV2(String iun, List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId> legalFactsIdList) throws UnsupportedEncodingException {
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(iun, legalFactsId.getKey());
            checkLegalFactFormatV2(resp.getUrl(), legalFactsId);
        }
    }

    private void checkLegalFactFormatV2(String url, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId);
        }
    }

    private void checkAttachment(NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(resp.getSha256())) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename());
        }
    }

    private void checkAttachmentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(resp.getSha256())) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename());
        }
    }

    private void checkAttachmentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp) {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(resp.getSha256())) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename());
        }
    }

    public Pair<String, String> preloadRadFsuDocument(String resourcePath, boolean usePresignedUrl) throws IOException {
        String sha256 = computeSha256(resourcePath);
        DocumentUploadResponse documentUploadResponse = getPreLoadRaddResponse(sha256);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourcePath, sha256, secret, url));

        if (usePresignedUrl) {
            loadToPresigned(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    public Pair<String, String> preloadRaddAlternativeDocument(String resourcePath, boolean usePresignedUrl, String operationId) throws IOException {
        String sha256 = computeSha256(resourcePath);
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse documentUploadResponse = getPreLoadRaddAlternativeResponse(sha256, operationId);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourcePath, sha256, secret, url));

        if (usePresignedUrl) {
            loadToPresignedZip(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    private DocumentUploadResponse getPreLoadRaddResponse(String sha256) {
        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest()
                //.bundleId("PN_RADD_FSU_ATTACHMENT-"+id+".pdf")
                .bundleId("TEST")
                .checksum(sha256)
                .contentType("application/pdf");
        return raddFsuClient.documentUpload("1234556", documentUploadRequest);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getPreLoadRaddAlternativeResponse(String sha256, String operationid) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest documentUploadRequest = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest()
                .operationId(operationid)
                .checksum(sha256);
        return raddAltClient.documentUpload("1234556", documentUploadRequest);
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), loadToPresigned);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument preloadDocumentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), loadToPresigned);
        documentSetKeyV1(document, preloadDocument.getValue1());
        documentSetVersionTokenV1(document, "v1");
        documentSetDigestsV1(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument preloadDocumentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), loadToPresigned);
        documentSetKeyV20(document, preloadDocument.getValue1());
        documentSetVersionTokenV20(document, "v1");
        documentSetDigestsV20(document, preloadDocument.getValue2());
        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument preloadDocumentV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), loadToPresigned);
        documentSetKeyV21(document, preloadDocument.getValue1());
        documentSetVersionTokenV21(document, "v1");
        documentSetDigestsV21(document, preloadDocument.getValue2());
        return document;
    }

    public NotificationDocument preloadDocumentWithoutUpload(NotificationDocument document) throws IOException {
        String resourceName = "classpath:/test.xml";
        Pair<String, String> preloadDocument = preloadGeneric(resourceName, loadToPresigned);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresigned);
            attachmentSetKey(attachment, preloadAttachment.getValue1());
            attachmentSetVersionToken(attachment, "v1");
            attachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment preloadAttachmentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresigned);
            attachmentSetKeyV1(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV1(attachment, "v1");
            attachmentSetDigestsV1(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment preloadAttachmentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresigned);
            attachmentSetKeyV20(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV20(attachment, "v1");
            attachmentSetDigestsV20(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment preloadAttachmentV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresigned);
            attachmentSetKeyV21(attachment, preloadAttachment.getValue1());
            attachmentSetVersionTokenV21(attachment, "v1");
            attachmentSetDigestsV21(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresignedMetadati);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment preloadMetadataAttachment(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), loadToPresignedMetadati);
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

    private void loadToPresigned(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, "application/pdf", 0);
    }

    private void loadToPresignedMetadati(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, "application/json", 0);
    }

    private void loadToPresignedZip(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, "application/zip", 0);
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
                throw new RuntimeException(ex);
            }
            loadToPresigned(url, secret, sha256, resource, resourceType, depth + 1);
        }
    }

    private PreLoadResponse getPreLoadResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256(sha256)
                .contentType("application/pdf");
        return client.presignedUploadRequest(
                Collections.singletonList(preLoadRequest)
        ).get(0);
    }

    private PreLoadResponse getPreLoadMetaDatiResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256(sha256)
                .contentType("application/json");
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
            throw new RuntimeException(exc);
        }
    }

    private Pair<String, String> preloadGeneric(String resourceName, String loadToMetadata) throws IOException {
        String sha256 = computeSha256(resourceName);
        PreLoadResponse preLoadResponse;

        if (loadToMetadata.equals("loadToPresignedMetadati")) {
            preLoadResponse = getPreLoadMetaDatiResponse(sha256);
        } else {
            preLoadResponse = getPreLoadResponse(sha256);
        }

        String key = preLoadResponse.getKey();
        String secret = preLoadResponse.getSecret();
        String url = preLoadResponse.getUrl();
        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourceName, sha256, secret, url));

        if (loadToMetadata.equals("loadToPresignedMetadati")) {
            loadToPresignedMetadati(url, secret, sha256, resourceName);
        } else if (loadToMetadata.equals("loadToPresigned")) {
            loadToPresigned(url, secret, sha256, resourceName);
        }
        return new Pair<>(key, sha256);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
                .contentType("application/pdf")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument newDocumentV1(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument newDocumentV2(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument newDocumentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationDocument()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment newAttachmentV1(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment newAttachmentV2(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment newAttachmentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment()
                .contentType("application/json")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment newMetadataAttachmentV21(String resourcePath) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationMetadataAttachment()
                .contentType("application/json")
                .ref(new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentBodyRef().key(resourcePath));
    }

    private Integer getWorkFlowWait() {
        return timingConfigs.getWorkflowWaitMillis();
    }

    private Integer getAcceptedWait() {
        return timingConfigs.getWorkflowWaitAcceptedMillis();
    }

    //metodo per stampa pdf per verifiche manuali
    public void stampaPdfTramiteByte(byte[] file, String path) {
        try {
            // Create file
            OutputStream out = new FileOutputStream(path);
            out.write(file);
            out.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
}