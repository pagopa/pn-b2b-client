package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
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
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }


    public NewNotificationResponse uploadNotification( NewNotificationRequest request) throws IOException {

        List<NotificationDocument> newdocs = new ArrayList<>();
        for (NotificationDocument doc : request.getDocuments()) {
            newdocs.add(this.preloadDocument(doc));
        }
        request.setDocuments(newdocs);

        for (NotificationRecipient recipient : request.getRecipients()) {
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if(paymentInfo != null){
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
                paymentInfo.setF24flatRate(preloadAttachment(paymentInfo.getF24flatRate()));
                paymentInfo.setF24standard(preloadAttachment(paymentInfo.getF24standard()));
            }

        }

        log.info("New Notification Request {}", request);
        NewNotificationResponse response = client.sendNewNotification( request );
        log.info("New Notification Request response {}", response);
        return response;
    }


    public FullSentNotification waitForRequestAcceptation( NewNotificationResponse response) {

        log.info("Request status for " + response.getNotificationRequestId() );
        NewNotificationRequestStatusResponse status = null;
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < 10; i++ ) {

            try {
                Thread.sleep( getWorkFlowWait());
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

    public void verifyNotification(FullSentNotification fsn) throws IOException, IllegalStateException {

        for (NotificationDocument doc: fsn.getDocuments()) {

            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationDocument(fsn.getIun(), Integer.parseInt(doc.getDocIdx()));
            byte[] content = downloadFile(resp.getUrl());
            String sha256 = computeSha256(new ByteArrayInputStream(content));

            if( ! sha256.equals(resp.getSha256()) ) {
                throw new IllegalStateException("SHA256 differs " + doc.getDocIdx() );
            }
        }

        int i = 0;
        for (NotificationRecipient recipient : fsn.getRecipients()) {

            if(fsn.getRecipients().get(i).getPayment() != null &&
                    fsn.getRecipients().get(i).getPayment().getPagoPaForm() != null){
                NotificationAttachmentDownloadMetadataResponse resp;

                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "PAGOPA");
                checkAttachment( resp );
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

    private void checkAttachment(NotificationAttachmentDownloadMetadataResponse resp) throws IOException {
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


    private void loadToPresigned( String url, String secret, String sha256, String resource ) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/pdf");
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

    public FullSentNotification getNotificationByIun(String iun) {
        return client.getSentNotification( iun );
    }


    public NotificationDocument newDocument(String resourcePath ) {
        return new NotificationDocument()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }


    public NotificationPaymentAttachment newAttachment(String resourcePath ) {
        return new NotificationPaymentAttachment()
                .contentType("application/pdf")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }


    private Integer getWorkFlowWait() {
        if(workFlowWait == null)return 31000;
        return workFlowWait;
    }


}
