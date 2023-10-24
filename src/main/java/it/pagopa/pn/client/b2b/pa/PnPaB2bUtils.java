package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebPaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static Logger log = LoggerFactory.getLogger(PnPaB2bUtils.class);

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Value("${pn.configuration.workflow.wait.accepted.millis:91000}")
    private Integer workFlowAcceptedWait;

    private final RestTemplate restTemplate;
    private final ApplicationContext ctx;

    private IPnPaB2bClient client;


    public PnPaB2bUtils(ApplicationContext ctx, IPnPaB2bClient client) {
        this.restTemplate = newRestTemplate();
        this.ctx = ctx;
        this.client = client;
    }



    public void setClient(IPnPaB2bClient client) {
        this.client = client;
    }

    private static final RestTemplate newRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(800_000);
        requestFactory.setReadTimeout(800_000);
        requestFactory.setConnectionRequestTimeout(800_000);
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }


    public NewNotificationResponse uploadNotification( NewNotificationRequestV21 request) throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocument(doc));
        }
        request.setDocuments(newdocs);

        //PRELOAD DOCUMENTI DI PAGAMENTO
        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {

                    if (paymentInfo.getPagoPa()!= null) {
                        paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    }
                    if (paymentInfo.getF24()!= null) {
                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }

                }

               // paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }

        log.info("New Notification Request {}", request);
        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        if (response != null)
        {
            try {
                log.info("New Notification\n IUN {}", new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception e) {
            }
        }
        return response;
    }


    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse uploadNotificationV1( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest request) throws IOException {

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocumentV1(doc));
        }
        request.setDocuments(newdocs);

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient : request.getRecipients()) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachmentV1(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }

        log.info("New Notification Request {}", request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response = client.sendNewNotificationV1( request );
        log.info("New Notification Request response {}", response);
        return response;
    }

    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse uploadNotificationV2( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest request) throws IOException {

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument> newdocs = new ArrayList<>();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocumentV2(doc));
        }
        request.setDocuments(newdocs);

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationRecipient recipient : request.getRecipients()) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachmentV2(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
        }

        log.info("New Notification Request {}", request);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response = client.sendNewNotificationV2( request );
        log.info("New Notification Request response {}", response);
        return response;
    }

    public NewNotificationResponse uploadNotificationNotFindAllegato( NewNotificationRequestV21 request, boolean noUpload) throws IOException {
//TODO Modificare.............
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            if(noUpload){
                newdocs.add(this.preloadDocumentWithoutUpload(doc));
            }else{

                newdocs.add(this.preloadDocument(doc));
            }
        }
        request.setDocuments(newdocs);

        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }

            }
        }

        //for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
            }
             **/
        //}

        //for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         }
         **/
        //}

        log.info("New Notification Request {}", request);
        if ((request.getDocuments()!= null && request.getDocuments().size()>0) && !noUpload){
            NotificationDocument notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf");
        }

        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }

    public NewNotificationResponse uploadNotificationNotEqualSha( NewNotificationRequestV21 request) throws IOException {
//TODO Modificare.............
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocument(doc));
        }
        request.setDocuments(newdocs);

        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }

            }
        }

       // for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
            }
             **/
        //}

        // for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         }
         **/
        //}

        log.info("New Notification Request {}", request);
        if (request.getDocuments()!= null && request.getDocuments().size()>0){
            NotificationDocument notificationDocument = request.getDocuments().get(0);
            // the document uploaded to safe storage is multa.pdf
            // I compute a different sha256 and I replace the old one
            String sha256 = computeSha256( "classpath:/multa.pdf" );
            notificationDocument.getDigests().setSha256(sha256);
        }

        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }

    public NewNotificationResponse uploadNotificationWrongExtension( NewNotificationRequestV21 request) throws IOException {
//TODO Modificare.............
        if (request.getDocuments()!= null && request.getDocuments().size()>0){
            NotificationDocument notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("classpath:/sample.txt");
        }

        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocument(doc));
        }
        request.setDocuments(newdocs);

        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }

            }
        }

       // for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
             **/
       // }

        // for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }

        log.info("New Notification Request {}", request);
        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }




    public NewNotificationResponse uploadNotificationOverSizeAllegato( NewNotificationRequestV21 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/200MB_PDF.pdf");

        List<NotificationDocument> newdocs = new ArrayList<>();
        newdocs.add(this.preloadDocument(notificationDocument));

        request.setDocuments(newdocs);

        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }
            }
        }

       // for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
             **/
       // }

        // for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }

        log.info("New Notification Request {}", request);

        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }


    public NewNotificationResponse uploadNotificationInjectionAllegato( NewNotificationRequestV21 request) throws IOException {
        //TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample_injection.xml.pdf");

        List<NotificationDocument> newdocs = new ArrayList<>();
        newdocs.add(this.preloadDocument(notificationDocument));

        request.setDocuments(newdocs);
        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }

            }
        }


    //    for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
             **/
       // }


        //    for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }

        log.info("New Notification Request {}", request);

        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }

    public NewNotificationResponse uploadNotificationOver15Allegato( NewNotificationRequestV21 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample.pdf");
        List<NotificationDocument> newdocs = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            notificationDocument =  newDocument("classpath:/sample.pdf");
            newdocs.add(this.preloadDocument(notificationDocument));
        }

        request.setDocuments(newdocs);

        for (NotificationRecipientV21 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if(paymentList != null){
                for (NotificationPaymentItem paymentInfo: paymentList) {
                    paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
                    paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                }
            }
        }

      //  for (NotificationRecipientV21 recipient : request.getRecipients()) {
            /**
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
//                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
//                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }
             **/
       // }

        //  for (NotificationRecipientV21 recipient : request.getRecipients()) {
        /**
         NotificationPaymentInfo paymentInfo = recipient.getPayment();
         if(paymentInfo != null){
         paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
         //                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
         //                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
         }
         **/
        // }

        log.info("New Notification Request {}", request);

        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }


    public FullSentNotificationV21 waitForRequestAcceptation( NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        NewNotificationRequestStatusResponseV21 status = null;
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < 5; i++ ) {

            try {
                Thread.sleep( getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException( exc );
            }

            status = client.getNotificationRequestStatus( response.getNotificationRequestId() );

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ( "ACCEPTED".equals( status.getNotificationRequestStatus() )) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms",(endTime - startTime));
        String iun = status.getIun();

        return iun == null? null : client.getSentNotification( iun );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification waitForRequestAcceptationV1( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse status = null;
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < 5; i++ ) {

            try {
                Thread.sleep( getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException( exc );
            }

            status = client.getNotificationRequestStatusV1( response.getNotificationRequestId() );

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ( "ACCEPTED".equals( status.getNotificationRequestStatus() )) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms",(endTime - startTime));
        String iun = status.getIun();

        return iun == null? null : client.getSentNotificationV1( iun );
    }


    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 waitForRequestAcceptationV2( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse status = null;
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < 5; i++ ) {

            try {
                Thread.sleep( getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException( exc );
            }

            status = client.getNotificationRequestStatusV2( response.getNotificationRequestId() );

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ( "ACCEPTED".equals( status.getNotificationRequestStatus() )) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms",(endTime - startTime));
        String iun = status.getIun();

        return iun == null? null : client.getSentNotificationV2(iun);
    }


    public String waitForRequestRefused( NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        NewNotificationRequestStatusResponseV21 status = null;
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < 2; i++ ) {

            try {
                Thread.sleep( getAcceptedWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException( exc );
            }

            status = client.getNotificationRequestStatus( response.getNotificationRequestId() );

            log.info("New Notification Request status {}", status.getNotificationRequestStatus());
            if ( "REFUSED".equals( status.getNotificationRequestStatus() )) {
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms",(endTime - startTime));

        String error = null;
        if (status != null && status.getErrors()!= null && status.getErrors().size()>0) {
            log.info("Detail status {}", status.getErrors().get(0).getDetail());
            error = status.getErrors().get(0).getCode();
        }
        return error == null? null : error;
    }


    public void verifyNotification(FullSentNotificationV21 fsn) throws IOException, IllegalStateException {

        for (NotificationDocument doc: fsn.getDocuments()) {

            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocument(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (NotificationRecipientV21 recipient : fsn.getRecipients()) {

            if(fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getPagoPa() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA",0);

                checkAttachment( resp );
            }
            if(fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getF24() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

               resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24" ,0);
                if (resp!= null && resp.getRetryAfter()!= null && resp.getRetryAfter()>0){
                    try {
                        Thread.sleep(resp.getRetryAfter()*3);
                        resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24" ,0);
                    } catch (InterruptedException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }

            i++;

        }

        for ( LegalFactsId legalFactsId: fsn.getTimeline().get(0).getLegalFactsIds()) {

            LegalFactDownloadMetadataResponse resp;

            resp = client.getLegalFact(
                    fsn.getIun(),
                    LegalFactCategory.SENDER_ACK,
                    URLEncoder.encode(legalFactsId.getKey(), StandardCharsets.UTF_8.toString())
            );

            byte[] content = downloadFile(resp.getUrl());
            String  pdfPrefix = new String( Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
            if( ! pdfPrefix.contains("PDF") ) {
                throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId );
            }
        }

        if(
                fsn.getNotificationStatus() == null
                        ||
                        fsn.getNotificationStatus().equals( NotificationStatus.REFUSED )
        ) {
            throw new IllegalStateException("WRONG STATUS: " + fsn.getNotificationStatus() );
        }
    }


    public void verifyNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification fsn) throws IOException, IllegalStateException {

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc: fsn.getDocuments()) {

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocumentV1(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationRecipient recipient : fsn.getRecipients()) {

            if(fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null){
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachmentV1(fsn.getIun(), i, "PAGOPA");
                checkAttachmentV1( resp );
            }
            i++;

        }

        for ( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId: fsn.getTimeline().get(0).getLegalFactsIds()) {

            LegalFactDownloadMetadataResponse resp;

            resp = client.getLegalFact(
                    fsn.getIun(),
                    LegalFactCategory.SENDER_ACK,
                    URLEncoder.encode(legalFactsId.getKey(), StandardCharsets.UTF_8.toString())
            );

            byte[] content = downloadFile(resp.getUrl());
            String  pdfPrefix = new String( Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
            if( ! pdfPrefix.contains("PDF") ) {
                throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId );
            }
        }

        if(
                fsn.getNotificationStatus() == null
                        ||
                        fsn.getNotificationStatus().equals( NotificationStatus.REFUSED )
        ) {
            throw new IllegalStateException("WRONG STATUS: " + fsn.getNotificationStatus() );
        }
    }


    public void verifyNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 fsn) throws IOException, IllegalStateException {


        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc: fsn.getDocuments()) {

            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocument(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId: fsn.getTimeline().get(0).getLegalFactsIds()) {

            if(fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null){
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachmentV2(fsn.getIun(), i, "PAGOPA");
                checkAttachmentV2( resp );
            }
            i++;

        }

        for ( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId: fsn.getTimeline().get(0).getLegalFactsIds()) {

            LegalFactDownloadMetadataResponse resp;

            resp = client.getLegalFact(
                    fsn.getIun(),
                    LegalFactCategory.SENDER_ACK,
                    URLEncoder.encode(legalFactsId.getKey(), StandardCharsets.UTF_8.toString())
            );

            byte[] content = downloadFile(resp.getUrl());
            String  pdfPrefix = new String( Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
            if( ! pdfPrefix.contains("PDF") ) {
                throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId );
            }
        }

        if(
                fsn.getNotificationStatus() == null
                        ||
                        fsn.getNotificationStatus().equals( NotificationStatus.REFUSED )
        ) {
            throw new IllegalStateException("WRONG STATUS: " + fsn.getNotificationStatus() );
        }
    }


    public void verifyNotificationAndSha256AllegatiPagamento(FullSentNotificationV21 fsn, String attachname) throws IOException, IllegalStateException {

        for (NotificationDocument doc: fsn.getDocuments()) {

            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocument(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (NotificationRecipientV21 recipient : fsn.getRecipients()) {

            if(fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getPagoPa() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA",0);

                checkAttachment( resp );
            }
            if(fsn.getRecipients().get(i).getPayments() != null &&
                    fsn.getRecipients().get(i).getPayments().get(0).getF24() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24" ,0);

                checkAttachment( resp );
            }

            i++;

        }
    }


    private void checkAttachment(NotificationAttachmentDownloadMetadataResponse resp) throws IOException {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename() );
        }
    }

    private void checkAttachmentV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp) throws IOException {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename() );
        }
    }

    private void checkAttachmentV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp) throws IOException {
        byte[] content = downloadFile(resp.getUrl());
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if( ! sha256.equals(resp.getSha256()) ) {
            throw new IllegalStateException("SHA256 differs " + resp.getFilename() );
        }
    }


    public NotificationDocument preloadDocument( NotificationDocument document) throws IOException {

        String resourceName = document.getRef().getKey();
        String sha256 = computeSha256( resourceName );
        PreLoadResponse preloadResp = getPreLoadResponse(sha256);
        String key = preloadResp.getKey();
        String secret = preloadResp.getSecret();
        String url = preloadResp.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourceName, sha256, secret, url));
        loadToPresigned( url, secret, sha256, resourceName );

        document.getRef().setKey( key );
        document.getRef().setVersionToken("v1");
        document.digests( new NotificationAttachmentDigests().sha256( sha256 ));

        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument preloadDocumentV1( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument document) throws IOException {

        String resourceName = document.getRef().getKey();
        String sha256 = computeSha256( resourceName );
        PreLoadResponse preloadResp = getPreLoadResponse(sha256);
        String key = preloadResp.getKey();
        String secret = preloadResp.getSecret();
        String url = preloadResp.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourceName, sha256, secret, url));
        loadToPresigned( url, secret, sha256, resourceName );

        document.getRef().setKey( key );
        document.getRef().setVersionToken("v1");
        document.digests( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDigests().sha256( sha256 ));

        return document;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument preloadDocumentV2( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument document) throws IOException {

        String resourceName = document.getRef().getKey();
        String sha256 = computeSha256( resourceName );
        PreLoadResponse preloadResp = getPreLoadResponse(sha256);
        String key = preloadResp.getKey();
        String secret = preloadResp.getSecret();
        String url = preloadResp.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourceName, sha256, secret, url));
        loadToPresigned( url, secret, sha256, resourceName );

        document.getRef().setKey( key );
        document.getRef().setVersionToken("v1");
        document.digests( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDigests().sha256( sha256 ));

        return document;
    }


    public NotificationDocument preloadDocumentWithoutUpload( NotificationDocument document) throws IOException {
        String resourceName = document.getRef().getKey();
        resourceName= "classpath:/test.xml";
        String sha256 = computeSha256( resourceName );

        PreLoadResponse preloadResp = getPreLoadResponse(sha256);
        String key = preloadResp.getKey();
        String secret = preloadResp.getSecret();
        String url = preloadResp.getUrl();

        log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                resourceName, sha256, secret, url));
        document.getRef().setKey( key );
        document.getRef().setVersionToken("v1");
        document.digests( new NotificationAttachmentDigests().sha256( sha256 ));

        return document;
    }

    public NotificationPaymentAttachment preloadAttachment( NotificationPaymentAttachment attachment) throws IOException {
        if( attachment != null ) {
            String resourceName = attachment.getRef().getKey();

            String sha256 = computeSha256( resourceName );

            PreLoadResponse preloadResp = getPreLoadResponse( sha256 );
            String key = preloadResp.getKey();
            String secret = preloadResp.getSecret();
            String url = preloadResp.getUrl();

            log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                    resourceName, sha256, secret, url));

            loadToPresigned( url, secret, sha256, resourceName );

            attachment.getRef().setKey( key );
            attachment.getRef().setVersionToken("v1");
            attachment.digests( new NotificationAttachmentDigests().sha256( sha256 ));

            return attachment;
        }
        else {
            return null;
        }

    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment preloadAttachmentV1( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment attachment) throws IOException {
        if( attachment != null ) {
            String resourceName = attachment.getRef().getKey();

            String sha256 = computeSha256( resourceName );

            PreLoadResponse preloadResp = getPreLoadResponse( sha256 );
            String key = preloadResp.getKey();
            String secret = preloadResp.getSecret();
            String url = preloadResp.getUrl();

            log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                    resourceName, sha256, secret, url));

            loadToPresigned( url, secret, sha256, resourceName );

            attachment.getRef().setKey( key );
            attachment.getRef().setVersionToken("v1");
            attachment.digests( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDigests().sha256( sha256 ));

            return attachment;
        }
        else {
            return null;
        }

    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment preloadAttachmentV2( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment attachment) throws IOException {
        if( attachment != null ) {
            String resourceName = attachment.getRef().getKey();

            String sha256 = computeSha256( resourceName );

            PreLoadResponse preloadResp = getPreLoadResponse( sha256 );
            String key = preloadResp.getKey();
            String secret = preloadResp.getSecret();
            String url = preloadResp.getUrl();

            log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                    resourceName, sha256, secret, url));

            loadToPresigned( url, secret, sha256, resourceName );

            attachment.getRef().setKey( key );
            attachment.getRef().setVersionToken("v1");
            attachment.digests( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDigests().sha256( sha256 ));

            return attachment;
        }
        else {
            return null;
        }

    }



    public NotificationMetadataAttachment preloadMetadataAttachment( NotificationMetadataAttachment attachment) throws IOException {
        if( attachment != null ) {
            String resourceName = attachment.getRef().getKey();

            String sha256 = computeSha256( resourceName );

            PreLoadResponse preloadResp = getPreLoadMetaDatiResponse( sha256 );
            String key = preloadResp.getKey();
            String secret = preloadResp.getSecret();
            String url = preloadResp.getUrl();

            log.info(String.format("Attachment resourceKey=%s sha256=%s secret=%s presignedUrl=%s\n",
                    resourceName, sha256, secret, url));

            loadToPresignedMetadati( url, secret, sha256, resourceName );

            attachment.getRef().setKey( key );
            attachment.getRef().setVersionToken("v1");
            attachment.digests( new NotificationAttachmentDigests().sha256( sha256 ));

            return attachment;
        }
        else {
            return null;
        }

    }




    private void loadToPresigned( String url, String secret, String sha256, String resource ) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/pdf");
        headers.add("x-amz-checksum-sha256", sha256);
        headers.add("x-amz-meta-secret", secret);

        HttpEntity<Resource> req = new HttpEntity<>( ctx.getResource( resource), headers);
        restTemplate.exchange( URI.create(url), HttpMethod.PUT, req, Object.class);
    }

    private void loadToPresignedMetadati( String url, String secret, String sha256, String resource ) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/json");
        headers.add("x-amz-checksum-sha256", sha256);
        headers.add("x-amz-meta-secret", secret);

        HttpEntity<Resource> req = new HttpEntity<>( ctx.getResource( resource), headers);
        restTemplate.exchange( URI.create(url), HttpMethod.PUT, req, Object.class);
    }


    private PreLoadResponse getPreLoadResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256( sha256 )
                .contentType("application/pdf");
        return client.presignedUploadRequest(
                Collections.singletonList( preLoadRequest)
        ).get(0);
    }

    private PreLoadResponse getPreLoadMetaDatiResponse(String sha256) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256( sha256 )
                .contentType("application/json");
        return client.presignedUploadRequest(
                Collections.singletonList( preLoadRequest)
        ).get(0);
    }

    private String computeSha256( String resName ) throws IOException {
        Resource res = ctx.getResource( resName );
        return computeSha256( res );
    }

    private String computeSha256( Resource res ) throws IOException {

        return computeSha256(res.getInputStream());
    }

    public String computeSha256(InputStream inStrm) {

        try(inStrm) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest( StreamUtils.copyToByteArray( inStrm ) );
            return bytesToBase64( encodedhash );
        } catch (IOException | NoSuchAlgorithmException exc) {
            throw new RuntimeException( exc );
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] downloadFile(String surl) throws IOException{
        try {
            URL url = new URL(surl);
            return IOUtils.toByteArray(url);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly();
        }

    }

    private static String bytesToBase64(byte[] hash) {
        return Base64Utils.encodeToString( hash );
    }

    public FullSentNotificationV21 getNotificationByIun(String iun) {
        return client.getSentNotification( iun );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification getNotificationByIunV1(String iun) {
        return client.getSentNotificationV1( iun );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 getNotificationByIunV2(String iun){
        return client.getSentNotificationV2( iun );
}


    public NotificationDocument newDocument(String resourcePath ) {
        return new NotificationDocument()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument newDocumentV1(String resourcePath ) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument()
                .contentType("application/pdf")
                .ref( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key( resourcePath ));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument newDocumentV2(String resourcePath ) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument()
                .contentType("application/pdf")
                .ref( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key( resourcePath ));
    }

    public NotificationPaymentAttachment newAttachment(String resourcePath ) {
        return new NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment newAttachmentV1(String resourcePath ) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentBodyRef().key( resourcePath ));
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment newAttachmentV2(String resourcePath ) {
        return new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref( new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentBodyRef().key( resourcePath ));
    }

    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath ) {
        return new NotificationMetadataAttachment()
                .contentType("application/json")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }



    private Integer getWorkFlowWait() {
        if(workFlowWait == null)return 31000;
        return workFlowWait;
    }

    private Integer getAcceptedWait() {
        if(workFlowAcceptedWait == null)return 91000;
        return workFlowAcceptedWait;
    }

    public void verifyCanceledNotification(FullSentNotificationV21 fsn) throws IOException, IllegalStateException {
//TODO Modificare.............
        for (NotificationDocument doc: fsn.getDocuments()) {

            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocument(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (NotificationRecipientV21 recipient : fsn.getRecipients()) {

            /**
            if(fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA");
                checkAttachment( resp );
            }
            i++;
             **/

        }

        for ( LegalFactsId legalFactsId: fsn.getTimeline().get(0).getLegalFactsIds()) {

            LegalFactDownloadMetadataResponse resp;

            resp = client.getLegalFact(
                    fsn.getIun(),
                    LegalFactCategory.SENDER_ACK,
                    URLEncoder.encode(legalFactsId.getKey(), StandardCharsets.UTF_8.toString())
            );

            byte[] content = downloadFile(resp.getUrl());
            String  pdfPrefix = new String( Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
            if( ! pdfPrefix.contains("PDF") ) {
                throw new IllegalStateException("LegalFact is not a PDF " + legalFactsId );
            }
        }

        //TODO Verificare................................
        if(
                fsn.getNotificationStatus() == null
                        ||
                        !fsn.getNotificationStatus().equals( NotificationStatus.CANCELLED )
        ) {
            throw new IllegalStateException("WRONG STATUS: " + fsn.getNotificationStatus() );
        }
    }


}
