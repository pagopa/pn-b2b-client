package it.pagopa.pn.client.b2b.pa;

import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceValidationStatusV26;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV25;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV28;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceValidationStatusV28;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
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


@Data
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

    private final RestTemplate restTemplate;
    private final ApplicationContext ctx;
    private IPnPaB2bClient client;
    private PnPollingFactory pollingFactory;
    private final IPnRaddFsuClient raddFsuClient;
    private final IPnRaddAlternativeClient raddAltClient;
    private final Random random = new Random();
    // Costanti
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REFUSED = "REFUSED";
    public static final String PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF = "PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf";
    public static final String PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON = "PN_F24_META-ab2acab392d042a1afd66f59732791f2.json";
    public static final String LEGAL_FACT_IS_NOT_A_PDF = "LegalFact is not a PDF ";
    public static final String WRONG_STATUS = "WRONG STATUS: ";
    public static final String PAGOPA = "PAGOPA";
    public static final String F_24 = "F24";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL = "Attachment: resourceKey = {}, sha256 = {}, secret = {}, presignedUrl = {}";
    public static final String SHA_256_DIFFERS = "SHA256 differs ";
    public static final String NEW_NOTIFICATION_REQUEST = "New Notification Request {}";
    public static final String NEW_NOTIFICATION_REQUEST_RESPONSE = "New Notification Request response {}";
    public static final String LOAD_TO_PRESIGNED = "LOAD_TO_PRESIGNED";
    public static final String LOAD_TO_PRESIGNED_METADATI = "LOAD_TO_PRESIGNED_METADATI";
    public static final String NEW_NOTIFICATION_IUN = "New Notification\n IUN {}";


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

    public void setClient(IPnPaB2bClient client, PnPollingFactory pollingFactory) {
        this.client = client;
        this.pollingFactory = pollingFactory;
    }

    //TODO: non usare. chiamare piuttosto sharedSteps.getB2bClient().getSentNotificationV26(iun)
    //Pressochè inutile, usato ormai solo in SearchNotification.
    public FullSentNotificationV27 getNotificationByIun(String iun) {
        return client.getSentNotificationV27(iun);
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

    private void checkSha256(String url, String sha, int docIdx) {
        byte[] content = downloadFile(url);
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(sha)) {
            throw new IllegalStateException(SHA_256_DIFFERS + docIdx);
        }
    }

    private void checkAttachment(String filename, String url, String sha) {
        byte[] content = downloadFile(url);
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(sha)) {
            throw new PnB2bException(SHA_256_DIFFERS + filename);
        }
    }

    private LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactsId) {
        return client.getLegalFact(iun, LegalFactCategory.SENDER_ACK, URLEncoder.encode(legalFactsId, StandardCharsets.UTF_8));
    }

    private void checkLegalFactFormat(String url, Object legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException(LEGAL_FACT_IS_NOT_A_PDF + legalFactsId);
        }
    }

    public Pair<String, String> preloadRaddFsuDocument(String resourcePath, boolean usePresignedUrl) throws IOException {
        String sha256 = computeSha256(resourcePath);
        DocumentUploadResponse documentUploadResponse = getPreloadRaddResponse(sha256);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, secret, url);
        if (usePresignedUrl) {
            loadToPresigned(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    public void preloadRaddCsvDocument(String resourcePath, String sha256, RegistryUploadResponse responseUploadCsv, boolean usePresignedUrl) {
        String secret = responseUploadCsv.getSecret();
        String url = responseUploadCsv.getUrl();
        if (usePresignedUrl) {
            loadToPresignedCsv(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD CSV COMPLETE");
        } else {
            log.info("UPLOAD RADD CSV COMPLETE WITHOUT UPLOAD");
        }
    }

    public Pair<String, String> preloadRaddAlternativeDocument(String resourcePath, boolean usePresignedUrl, String operationId) throws IOException {
        String sha256 = computeSha256(resourcePath);
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse documentUploadResponse = getPreloadRaddAlternativeResponse(sha256, operationId);
        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, secret, url);

        if (usePresignedUrl) {
            loadToPresignedZip(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    public Pair<String, String> preloadRaddOperatoreAlternativeDocument(String resourcePath, boolean usePresignedUrl, String operationId, RaddOperator uidRaddOperator) throws IOException {
        String sha256 = computeSha256(resourcePath);
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse documentUploadResponse = getDocumentUploadResponsePerOperatore(operationId, uidRaddOperator, sha256);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, secret, url);

        if (usePresignedUrl) {
            loadToPresignedZip(url, secret, sha256, resourcePath);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getDocumentUploadResponsePerOperatore(String operationId, RaddOperator raddOperator, String sha256) {
        raddAltClient.setAuthTokenRadd(raddOperator.getIssuerType());
        return getPreloadRaddOperatorAlternativeResponse(sha256, operationId, raddOperator.getUid());
    }

    private DocumentUploadResponse getPreloadRaddResponse(String sha256) {
        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest()
                .bundleId("TEST")
                .checksum(sha256)
                .contentType(APPLICATION_PDF);
        return raddFsuClient.documentUpload("1234556", documentUploadRequest);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getPreloadRaddAlternativeResponse(String sha256, String operationid) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest documentUploadRequest = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest()
                .operationId(operationid)
                .checksum(sha256);
        return raddAltClient.documentUpload("1234556", documentUploadRequest);
    }

    private it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getPreloadRaddOperatorAlternativeResponse(String sha256, String operationid, String uidRaddOperator) {
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest documentUploadRequest = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest()
                .operationId(operationid)
                .checksum(sha256);
        return raddAltClient.documentUpload(uidRaddOperator, documentUploadRequest);
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
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

    public NotificationMetadataAttachment preloadWithMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    public NotificationMetadataAttachment preloadWithoutMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
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

    public void loadToPresignedCsv(String url, String secret, String sha256, String resource) {
        loadToPresigned(url, secret, sha256, resource, "text/csv", 0);
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
            byte[] encodedHash = digest.digest(StreamUtils.copyToByteArray(inStrm));
            return Base64Utils.encodeToString(encodedHash);
        } catch (IOException | NoSuchAlgorithmException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    public Pair<String, String> preloadGeneric(String resourceName, String loadToMetadata) throws IOException {
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
        if (downloadUrl == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "download Url cannot be null");
        }
        try {
            URL url = new URL(downloadUrl);
            return IOUtils.toByteArray(url);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly();
        }
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

    /**
     * Metodi per le notifiche V1
     */

    public void verifyNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification fsn) throws IllegalStateException {
        //Verify Sha
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse response = client.getSentNotificationDocumentV1(fsn.getIun(), docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
        //Verify Attachments
        fsn.getRecipients().stream().filter(recipient -> recipient.getPayment() != null && recipient.getPayment().getPagoPaForm() != null)
                .forEach(recipient -> {
                    int i = fsn.getRecipients().indexOf(recipient);
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse resp;
                    resp = client.getSentNotificationAttachmentV1(fsn.getIun(), i, PAGOPA);
                    checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
                });
        //Verify LegalFacts format
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId> legalFactsIdList = fsn.getTimeline().get(0).getLegalFactsIds();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(fsn.getIun(), legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
        //Verify Status
        if (fsn.getNotificationStatus().getValue().equals(REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + fsn.getNotificationStatus());
        }
    }

    /**
     * Metodi per le notifiche V20
     */

    public void verifyNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 fsn) throws IllegalStateException {
        //Verify Sha
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse response = client.getSentNotificationDocumentV2(fsn.getIun(), docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
        //Verify Attachments
        fsn.getRecipients().stream().filter(recipient -> recipient.getPayment() != null && recipient.getPayment().getPagoPaForm() != null)
                .forEach(recipient -> {
                    int index = fsn.getRecipients().indexOf(recipient);
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse resp;
                    resp = client.getSentNotificationAttachmentV2(fsn.getIun(), index, PAGOPA);
                    checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
                });
        //Verify LegalFacts format
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId> legalFactsIdList = fsn.getTimeline().get(0).getLegalFactsIds();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.LegalFactsId legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(fsn.getIun(), legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
        //Verify Status
        if (fsn.getNotificationStatus().getValue().equals(REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + fsn.getNotificationStatus());
        }
    }

    /**
     * Metodi per le notifiche V24
     */
    public void verifyNotification(FullSentNotificationV26 fsn) throws IllegalStateException {
        //Verify Sha
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            NotificationAttachmentDownloadMetadataResponse response = client.getSentNotificationDocument(fsn.getIun(), docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
        //Verify Attachments
        fsn.getRecipients().stream().filter(recipient -> recipient.getPayments() != null && !recipient.getPayments().isEmpty())
                .forEach(recipient -> {
                    extractAndCheckAttachment(fsn, recipient);
                    extractAttachment(fsn, recipient);
                });
        //Verify LegalFacts format
        List<LegalFactsIdV20> legalFactsIdList = Objects.requireNonNull(fsn.getTimeline().get(0).getLegalFactsIds());
        for (LegalFactsIdV20 legalFactsId : legalFactsIdList) {
            LegalFactDownloadMetadataResponse resp = getLegalFact(fsn.getIun(), legalFactsId.getKey());
            checkLegalFactFormat(resp.getUrl(), legalFactsId);
        }
        //Verify status
        if (fsn.getNotificationStatus().getValue().equals(REFUSED)) {
            throw new IllegalStateException(WRONG_STATUS + fsn.getNotificationStatus());
        }
    }

    private void verifySha256Notification(FullSentNotificationV26 fsn) {
        for (NotificationDocument doc : fsn.getDocuments()) {
            int docIdx = Integer.parseInt(Objects.requireNonNull(doc.getDocIdx()));
            NotificationAttachmentDownloadMetadataResponse response = client.getSentNotificationDocument(fsn.getIun(), docIdx);
            checkSha256(response.getUrl(), response.getSha256(), docIdx);
        }
    }

    public void verifyNotificationAndSha256AllegatiPagamento(FullSentNotificationV27 fsn, String attachment) throws IllegalStateException {
        verifySha256Notification(fsn);
        for (int i = 0; i < fsn.getRecipients().size(); i++) {
            NotificationRecipientV24 recipient = fsn.getRecipients().get(i);
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, PAGOPA, 0);
                checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
            }
            if (fsn.getRecipients().get(i).getPayments() != null &&
                    Objects.requireNonNull(recipient.getPayments()).get(0).getF24() != null) {
                NotificationAttachmentDownloadMetadataResponse resp;
                resp = client.getSentNotificationAttachment(fsn.getIun(), i, "F24", 0);
                checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
            }
        }
    }

    public NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationPaymentAttachment newAttachment(String resourcePath) {
        return new NotificationPaymentAttachment().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    public NotificationMetadataAttachment newMetadataAttachment(String resourcePath) {
        return new NotificationMetadataAttachment().contentType(APPLICATION_JSON).ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private void setAttachmentAndMetadata(NewNotificationRequestV24 newNotificationRequestV24, boolean noUpload) throws IOException {
        for (NotificationRecipientV23 recipient : newNotificationRequestV24.getRecipients()) {
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
                paymentInfo.getF24().setMetadataAttachment(preloadWithoutMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            } else {
                paymentInfo.getF24().setMetadataAttachment(preloadWithMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    private void extractAttachment(FullSentNotificationV27 fsn, NotificationRecipientV24 recipient) {
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

    private void extractAndCheckAttachment(FullSentNotificationV27 fsn, NotificationRecipientV24 recipient) {
        if (Objects.requireNonNull(recipient.getPayments()).get(0).getPagoPa() != null) {
            NotificationAttachmentDownloadMetadataResponse resp = client.getSentNotificationAttachment(fsn.getIun(), fsn.getRecipients().indexOf(recipient), PAGOPA, 0);
            checkAttachment(resp.getFilename(), resp.getUrl(), resp.getSha256());
        }
    }

    public void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    public void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    public void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
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


    /* TODO: I seguenti metodi (alcuni dei quali richiamati da step non più utilizzati) andrebbero rimossi da qua
        trovando un modo per manipolare le request all'interno delle interfacce */
    public NewNotificationResponse uploadNotificationNotFindAllegato(NewNotificationRequestV24 request, boolean noUpload) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty() && !noUpload) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey(PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF);
        }
        composeNewNotification(request, notificationDocument, true, noUpload, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotFindAllegatoJson(NewNotificationRequestV24 request, boolean noUpload) throws IOException {
        NotificationDocument notificationDocument = null;

        if ((!request.getRecipients().isEmpty()) && !noUpload) {
            NotificationRecipientV23 notificationRecipientV23 = request.getRecipients().get(0);
            Objects.requireNonNull(Objects.requireNonNull(notificationRecipientV23.getPayments()).get(0).getF24()).getMetadataAttachment().getRef().setKey(PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON);
        }
        composeNewNotification(request, notificationDocument, true, noUpload, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotEqualSha(NewNotificationRequestV24 request) throws IOException {
        NotificationDocument notificationDocument = null;
        composeNewNotification(request, notificationDocument, true, false, 0);
        if (!request.getDocuments().isEmpty()) {
            notificationDocument = request.getDocuments().get(0);
            // the document uploaded to safe storage is multa.pdf
            // I compute a different sha256 and I replace the old one
            String sha256 = computeSha256("classpath:/multa.pdf");
            notificationDocument.setDigests(new NotificationAttachmentDigests().sha256(sha256));
        }
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationNotEqualShaJson(NewNotificationRequestV24 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        String sha256 = null;
        if (!request.getRecipients().isEmpty()) {
            // the document uploaded to safe storage is multa.pdf
            // I compute a different sha256 and I replace the old one
            sha256 = computeSha256("classpath:/multa.pdf");
        }
        composeNewNotification(request, notificationDocument, true, false, 0);
        Objects.requireNonNull(Objects.requireNonNull(request.getRecipients().get(0).getPayments()).get(0).getF24()).getMetadataAttachment().getDigests().setSha256(sha256);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationWrongExtension(NewNotificationRequestV24 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = null;
        if (!request.getDocuments().isEmpty()) {
            notificationDocument = request.getDocuments().get(0);
            notificationDocument.getRef().setKey("classpath:/sample.txt");
        }
        composeNewNotification(request, notificationDocument, true, false, 0);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationOver15Allegato(NewNotificationRequestV24 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample.pdf");
        composeNewNotification(request, notificationDocument, false, false, 20);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationOverSizeAllegato(NewNotificationRequestV24 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/200MB_PDF.pdf");
        composeNewNotification(request, notificationDocument, false, false, 1);
        return sendNewNotification(request);
    }

    public NewNotificationResponse uploadNotificationInjectionAllegato(NewNotificationRequestV24 request) throws IOException {
//TODO Modificare.............
        NotificationDocument notificationDocument = newDocument("classpath:/sample_injection.xml.pdf");
        composeNewNotification(request, notificationDocument, false, false, 1);
        return sendNewNotification(request);
    }

    private NewNotificationResponse sendNewNotification(NewNotificationRequestV24 request) {
        NewNotificationResponse response = client.sendNewNotificationV24(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        return response;
    }

    private void composeNewNotification(NewNotificationRequestV24 request, NotificationDocument notificationDocument, boolean isAlist, boolean noUpload, int overAllegato) throws IOException {
        List<NotificationDocument> newDocs = new ArrayList<>();
        if (isAlist) {
            for (NotificationDocument doc : request.getDocuments()) {
                if (noUpload) {
                    newDocs.add(this.preloadDocumentWithoutUpload(doc));
                } else {
                    newDocs.add(this.preloadDocument(doc));
                }
            }
        } else {
            for (int i = 0; i < overAllegato; i++) {
                newDocs.add(this.preloadDocument(notificationDocument));
            }
        }
        request.setDocuments(newDocs);
        setAttachmentAndMetadata(request, noUpload);
        log.info(NEW_NOTIFICATION_REQUEST, request);
    }

    public String waitForRequestRefused(NewNotificationResponse response) {
        log.info("Request status for " + response.getNotificationRequestId());
        long startTime = System.currentTimeMillis();
        PnPollingServiceValidationStatusV26 validationStatus = (PnPollingServiceValidationStatusV26) pollingFactory.getPollingService(PnPollingStrategy.VALIDATION_STATUS_V26);
        PnPollingResponseV26 pollingResponse = validationStatus.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));
        StringBuilder error = new StringBuilder();
        if (pollingResponse.getStatusResponse() != null && pollingResponse.getStatusResponse().getErrors() != null && !pollingResponse.getStatusResponse().getErrors().isEmpty()) {
            for (ProblemError err : pollingResponse.getStatusResponse().getErrors()) {
                error.append(" ").append(err.getDetail());
            }
        }
        log.info("Detail status {}", error);
        return error.toString();
    }

}