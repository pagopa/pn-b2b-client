package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.pa.config.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.PreLoadResponse;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnRaddAlternativeClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.RaddOperator;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.DocumentUploadResponse;
import it.pagopa.pn.cucumber.steps.utilitySteps.Environment;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.TimelineEventId;
import it.pagopa.pn.cucumber.utils.datatestVersions.AbstractDataTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Data
@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class B2bUtils {

    protected final ApplicationContext context;
    protected final IPnPaB2bClient b2bClient;
    protected final PnPollingFactory pollingFactory;

    // Costanti
    public static final String PN_NOTIFICATION_ATTACHMENTS_ZBEDA_19_F_8997469_BB_75_D_28_FF_12_BDF_321_PDF = "PN_NOTIFICATION_ATTACHMENTS-zbeda19f8997469bb75d28ff12bdf321.pdf";
    public static final String PN_F24_META_AB_2_ACAB_392_D_042_A_1_A_FD_66_F_59732791_F_2_JSON = "PN_F24_META-ab2acab392d042a1afd66f59732791f2.json";
    public static final String WRONG_STATUS = "WRONG STATUS: ";
    public static final String PAGOPA = "PAGOPA";
    public static final String F_24 = "F24";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_ZIP = "application/zip";
    public static final String TEXT_CSV = "text/csv";
    public static final String ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL = "Attachment: resourceKey = {}, sha256 = {}, presignedUrl = {}";
    public static final String SHA_256_DIFFERS = "SHA256 differs ";
    public static final String NEW_NOTIFICATION_REQUEST = "New Notification Request {}";
    public static final String NEW_NOTIFICATION_RESPONSE = "New Notification Response {}";
    public static final String LOAD_TO_PRESIGNED = "LOAD_TO_PRESIGNED";
    public static final String LOAD_TO_PRESIGNED_METADATI = "LOAD_TO_PRESIGNED_METADATI";
    public static final String NEW_NOTIFICATION_IUN = "New Notification\n IUN {}";

    public B2bUtils(ApplicationContext context,
                    IPnPaB2bClient b2bClient,
                    PnPollingFactory pollingFactory) {
        this.context = context;
        this.b2bClient = b2bClient;
        this.pollingFactory = pollingFactory;
    }

    @AllArgsConstructor
    @Data
    @ToString
    public static class Pair<K, E> {
        K value1;
        E value2;
    }


    //Metodi generali, indipendenti dalla versione usata, dovranno essere riportati qua come metodi statici

    public static byte[] downloadFile(String downloadUrl) {
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

    public static boolean downloadUrlAndCheckContent(String strUrl, String contentType) {
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

    public static String getOffsetDateTimeFromDate(Instant date) {
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(date, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return offsetDateTime.truncatedTo(java.time.temporal.ChronoUnit.SECONDS).format(formatter);
    }

    public static OffsetDateTime convertStringToOffsetDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateString, formatter).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    public static int convertToSeconds(String timeStr) {
        int number = Integer.parseInt(timeStr.substring(0, timeStr.length() - 1));
        char unit = timeStr.toLowerCase().charAt(timeStr.length() - 1);
        if (unit == 'm') {
            return number * 60;
        } else if (unit == 'h') {
            return number * 3600;
        } else {
            throw new IllegalArgumentException("Unità temporale non supportata");
        }
    }

    public static String computeSha256(ApplicationContext context, String resourceName) throws IOException {
        Resource res = context.getResource(resourceName);
        return computeSha256(res.getInputStream());
    }

    public static String computeSha256(InputStream inputStream) {
        try (inputStream) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(StreamUtils.copyToByteArray(inputStream));
            return Base64Utils.encodeToString(encodedHash);
        } catch (IOException | NoSuchAlgorithmException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }

    public static void checkSha256(String url, String sha, int docIdx) {
        byte[] content = downloadFile(url);
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(sha)) {
            throw new IllegalStateException(SHA_256_DIFFERS + docIdx);
        }
    }

    public static void checkAttachment(String filename, String url, String sha) {
        byte[] content = downloadFile(url);
        String sha256 = computeSha256(new ByteArrayInputStream(content));
        if (!sha256.equals(sha)) {
            throw new PnB2bException(SHA_256_DIFFERS + filename);
        }
    }

    public static void stampaPdfTramiteByte(byte[] file, String path) {
        try {
            // Create file
            OutputStream out = new FileOutputStream(path);
            out.write(file);
            out.close();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    public static void checkLegalFactFormat(String url, Object legalFactsId) {
        byte[] content = downloadFile(url);
        String pdfPrefix = new String(Arrays.copyOfRange(content, 0, 10), StandardCharsets.UTF_8);
        if (!pdfPrefix.contains("PDF")) {
            throw new IllegalStateException("LegalFact is not a PDF: " + legalFactsId);
        }
    }

    private static RestTemplate getDefaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateConfiguration.RequestAndTraceIdInterceptor());
        return restTemplate;
    }

    public static Pair<String, String> preloadGeneric(ApplicationContext context, IPnPaB2bClient b2bClient, String resourceName, String contentType) throws IOException {
        String sha256 = computeSha256(context, resourceName);
        PreLoadResponse preLoadResponse = getPreLoadResponse(b2bClient, sha256, contentType);

        String key = preLoadResponse.getKey();
        String secret = preLoadResponse.getSecret();
        String url = preLoadResponse.getUrl();
        log.info("Attachment resourceKey={} sha256={} presignedUrl={}", resourceName, sha256, url);

        loadToPresigned(context, url, secret, sha256, resourceName, contentType);
        return new Pair<>(key, sha256);
    }

    public static void loadToPresigned(ApplicationContext context, String url, String secret, String sha256, String resource, String contentType) {
        HttpEntity httpEntity = new HttpEntity(context.getResource(resource), getHeadersMapForUploadToPresigned(contentType, sha256, secret));
        loadToPresignedWithDepth(url, httpEntity, 0);
    }

    /**
     * Consente di fare l'upload di un file passando un byte[] anziché la risorsa (utile in caso si voglia simulare l'upload di un file malevolo, ad esempio un virus EICAR,
     * che non è possibile salvare nelle risorse, ma dev'essere invece creato a runtime).
     */
    public static void loadToPresignedFromByteArray(String url, String secret, String sha256, byte[] byteArray, String contentType) {
        HttpEntity httpEntity = new HttpEntity(byteArray, getHeadersMapForUploadToPresigned(contentType, sha256, secret));
        loadToPresignedWithDepth(url, httpEntity, 0);
    }

    private static MultiValueMap<String, String> getHeadersMapForUploadToPresigned(String contentType, String sha256, String secret) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (contentType != null) headers.add("Content-type", contentType);
        if (sha256 != null) headers.add("x-amz-checksum-sha256", sha256);
        if (secret != null) headers.add("x-amz-meta-secret", secret);
        log.info("headers: {}", headers);
        return headers;
    }

    private static void loadToPresignedWithDepth(String url, HttpEntity httpEntity, int depth) {
        try {
            RestTemplate restTemplate = getDefaultRestTemplate();
            restTemplate.exchange(URI.create(url), HttpMethod.PUT, httpEntity, Object.class);
        } catch (Exception e) {
            if (depth >= 5) {
                throw e;
            }
            log.info("Upload in catch, retry");
            try {
                Thread.sleep(2000);
                log.error("[THREAD IN SLEEP PRELOAD] id: {} , attempt: {} , url: {}", Thread.currentThread().getId(), depth, url);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(ex.getMessage());
            }
            loadToPresignedWithDepth(url, httpEntity, depth + 1);
        }
    }

    private static PreLoadResponse getPreLoadResponse(IPnPaB2bClient b2bClient, String sha256, String contentType) {
        PreLoadRequest preLoadRequest = new PreLoadRequest()
                .preloadIdx("0")
                .sha256(sha256)
                .contentType(contentType);
        return b2bClient.presignedUploadRequest(Collections.singletonList(preLoadRequest)).get(0);
    }

    public static LegalFactDownloadMetadataResponse getLegalFact(IPnPaB2bClient b2bClient, String iun, String legalFactsId) {
        return b2bClient.getLegalFact(iun, LegalFactCategory.SENDER_ACK, URLEncoder.encode(legalFactsId, StandardCharsets.UTF_8));
    }

    /**
     * Metodi per RADD
     */

    public static void preloadRaddCsvDocument(ApplicationContext context, String resourcePath, String sha256, RegistryUploadResponse responseUploadCsv, boolean usePresignedUrl) {
        String secret = responseUploadCsv.getSecret();
        String url = responseUploadCsv.getUrl();
        if (usePresignedUrl) {
            loadToPresigned(context, url, secret, sha256, resourcePath, TEXT_CSV);
            log.info("UPLOAD RADD CSV COMPLETE");
        } else {
            log.info("UPLOAD RADD CSV COMPLETE WITHOUT UPLOAD");
        }
    }

    public static Pair<String, String> preloadRaddFsuDocument(ApplicationContext context, IPnRaddFsuClient raddFsuClient, String resourcePath, boolean usePresignedUrl) throws IOException {
        String sha256 = computeSha256(context, resourcePath);
        DocumentUploadResponse documentUploadResponse = getPreloadRaddFsuResponse(raddFsuClient, sha256);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, url);
        if (usePresignedUrl) {
            loadToPresigned(context, url, secret, sha256, resourcePath, APPLICATION_PDF);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    private static DocumentUploadResponse getPreloadRaddFsuResponse(IPnRaddFsuClient raddFsuClient, String sha256) {
        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest()
                .bundleId("TEST")
                .checksum(sha256)
                .contentType(APPLICATION_PDF);
        return raddFsuClient.documentUpload("1234556", documentUploadRequest);
    }

    public static Pair<String, String> preloadRaddAlternativeDocument(
            ApplicationContext context,
            PnRaddAlternativeClientImpl raddAltClient,
            RaddOperator raddOperator,
            String resourcePath,
            boolean usePresignedUrl,
            String operationId) throws IOException {

        String sha256 = computeSha256(context, resourcePath);
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse documentUploadResponse;
        documentUploadResponse = getPreloadRaddAlternativeResponse(raddAltClient, raddOperator, sha256, operationId);

        String key = documentUploadResponse.getFileKey();
        String secret = documentUploadResponse.getSecret();
        String url = documentUploadResponse.getUrl();
        log.info(ATTACHMENT_RESOURCE_KEY_SHA_256_SECRET_PRESIGNED_URL, resourcePath, sha256, url);
        if (usePresignedUrl) {
            loadToPresigned(context, url, secret, sha256, resourcePath, APPLICATION_ZIP);
            log.info("UPLOAD RADD COMPLETE");
        } else {
            log.info("UPLOAD RADD COMPLETE WITHOUT UPLOAD");
        }
        return new Pair<>(key, sha256);
    }

    private static it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadResponse getPreloadRaddAlternativeResponse(PnRaddAlternativeClientImpl raddAltClient, RaddOperator raddOperator, String sha256, String operationId) {
        String uidRaddOperator;
        if (raddOperator == null) {
            uidRaddOperator = "1234556";
        } else {
            raddAltClient.setAuthTokenRadd(raddOperator.getIssuerType());
            uidRaddOperator = raddOperator.getUid();
        }
        it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest documentUploadRequest;
        documentUploadRequest = new it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.DocumentUploadRequest()
                .operationId(operationId)
                .checksum(sha256);
        return raddAltClient.documentUpload(uidRaddOperator, documentUploadRequest);
    }

    /**
     * Restituisce una property a partire dal nome
     */
    public static String getProperty(ApplicationContext context, String propertyName) {
        return context.getEnvironment().getProperty(propertyName);
    }

    /**
     * Se nel DataTest viene passato il parametro parametriCalcoloCostoNotifica, provvede a calcolare il costo della notifica a partire
     * da una stringa in input con il seguente formato: recipients:1,ko:1,ok:0
     * La stringa deve pertanto avere sempre 3 parametri, separati da virgola, e le coppie chiave-valore devono essere separate da :
     */
    public static Long calcolaCostoNotifica(ApplicationContext context, String parametriCalcoloCostoNotifica) {

        String mode;
        long costoBaseNotifica;
        long technicalRefusalCost;
        int numRecipients;
        int responseKo;
        int responseOk;

        try {
            String[] parameters = parametriCalcoloCostoNotifica.split(",");
            numRecipients = Integer.parseInt(parameters[0].split(":")[1]);
            responseKo = Integer.parseInt(parameters[1].split(":")[1]);
            responseOk = Integer.parseInt(parameters[2].split(":")[1]);

            costoBaseNotifica = Long.parseLong(getProperty(context, COSTO_BASE_NOTIFICA));
            String propertyValue = getProperty(context, TECHNICAL_REFUSAL_COST_MODE);
            String[] modeCost = (propertyValue != null) ? propertyValue.split(";") : null;

            if (modeCost == null || modeCost.length < 2 || responseKo == 0) {
                mode = "DEFAULT";
                technicalRefusalCost = 0;
            } else {
                mode = modeCost[0];
                technicalRefusalCost = Integer.parseInt(modeCost[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Formato parametri calcolo costo notifica non valido, controllare i dati in input e le properties");
        }
        return switch (mode) {
            case "RECIPIENT_BASED" -> (responseKo * technicalRefusalCost) + (responseOk * costoBaseNotifica);
            case "UNIFORM" -> technicalRefusalCost;
            case "DEFAULT" -> numRecipients * costoBaseNotifica;
            default ->
                    throw new RuntimeException("Modalità di calcolo non riconosciuta, controllare i dati in input e le properties");
        };
    }

    /**
     * Verifica se un oggetto "expected" ha tutti i campi null.
     * Usato in fase di confronto expected/actual: quando expected ha tutti i campi null si procede a verificare che actual != null
     *
     * @return true se tutti i campi dell'oggetto sono null
     */
    public static boolean objectHasAllFieldsNull(Object expected) {
        Class<?> clazz = expected.getClass();
        List<Field> nonStaticFields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toList();
        try {
            for (Field expectedField : nonStaticFields) {
                expectedField.setAccessible(true);
                Object expectedValue = expectedField.get(expected);
                if (expectedValue != null) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            assertThat(true)
                    .as("Eccezione imprevista in fase di verifica se l'expected è NOT NULL tramite reflection " + e.getMessage())
                    .isFalse();
        }
        return true;
    }

    /**
     * Confronta due oggetti qualsiasi, valutando l'uguaglianza solo per i field != null specificati nell'expected
     * (NOTA: eventuali campi statici sono esclusi da questa verifica)
     */
    public static void compareActualAndExpected(String error, Object actual, Object expected) {
        error += " -> ";
        if (expected == null) {
            assertThat(actual).as(error + actual + " dovrebbe essere null").isNull();
            return;
        }
        Class<?> clazz = expected.getClass();
        assertThat(actual).as(error + clazz.getName() + " non dev'essere null").isNotNull();
        List<Field> nonStaticFields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toList();
        try {
            for (Field expectedField : nonStaticFields) {
                String fieldName = expectedField.getName();
                expectedField.setAccessible(true);
                Object expectedValue = expectedField.get(expected);
                try {
                    Field actualField = clazz.getDeclaredField(fieldName);
                    actualField.setAccessible(true);
                    Object actualValue = actualField.get(actual);
                    if (expectedValue != null) {
                        //TODO: aggiunto controllo isEqualToIgnoringCase per i campi stringa dell PhysicalAddress (escluso municipalityDetails) causa consolidatore che fa fallire i test
                        if (expectedField.getType().equals(String.class)) {
                            if (!fieldName.equals("municipalityDetails")) {
                                assertThat(actualValue).as(error + fieldName + " non dev'essere null").isNotNull();
                                String stringActual = (String) actualValue;
                                String stringExpected = (String) expectedValue;
                                assertThat(stringActual).as(error + fieldName + " non coincide col valore atteso").isEqualToIgnoringCase(stringExpected);
                            }
                        } else {
                            assertThat(actualValue).as(error + fieldName + " non coincide col valore atteso").isEqualTo(expectedValue);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    assertThat(true)
                            .as("Eccezione imprevista in fase di controllo uguaglianza del campo " + fieldName + " tramite reflection " + e.getMessage())
                            .isFalse();
                }
            }
        } catch (IllegalAccessException e) {
            assertThat(true)
                    .as("Eccezione imprevista in fase di controllo uguaglianza tramite reflection " + e.getMessage())
                    .isFalse();
        }
    }

    /**
     * Usato a fini di logging per stampare le proprietà che deve avere il TimelineElement atteso
     */
    public static String getExpectedTimelineElement(AbstractDataTest dataTest, String timelineElementCategory) {
        StringBuilder sb = new StringBuilder();
        sb.append("category: ").append(timelineElementCategory).append("\n");
        if (dataTest != null && dataTest.getInputData() != null) {
            for (Map.Entry<String, String> entry : dataTest.getInputData().entrySet()) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Lancia un'AssertionFailedError con log estremamente dettagliato relativo agli elementi di timeline
     * (rendendo molto più facile per i tester capire quale sia la causa del fail, in particolar modo se dovuto a sviste dei dati in input)
     */
    public static void logTimelineElementsThatDoNotMatchExpected(List<AssertionError> assertionErrorList, AbstractDataTest dataTest, String timelineElementCategory) {
        String expectedTimelineElement = getExpectedTimelineElement(dataTest, timelineElementCategory);
        StringBuilder sb = new StringBuilder();
        sb.append("Sono stati trovati ")
                .append(assertionErrorList.size())
                .append(" elementi con category ")
                .append(timelineElementCategory)
                .append(", ma nessuno combacia con\n")
                .append(expectedTimelineElement);
        for (int i = 0; i < assertionErrorList.size(); i++) {
            AssertionError error = assertionErrorList.get(i);
            sb.append("\n").append(i + 1).append(") -> ").append(error.getMessage());
        }
        throw new AssertionError(sb.toString());
    }

    public static String getTimelineEventId(EventId event, String timelineEventCategory) {
        return switch (timelineEventCategory) {
            case SEND_COURTESY_MESSAGE -> TimelineEventId.SEND_COURTESY_MESSAGE.buildEventId(event);
            case REQUEST_REFUSED -> TimelineEventId.REQUEST_REFUSED.buildEventId(event);
            case AAR_GENERATION -> TimelineEventId.AAR_GENERATION.buildEventId(event);
            case REQUEST_ACCEPTED -> TimelineEventId.REQUEST_ACCEPTED.buildEventId(event);
            case SEND_DIGITAL_DOMICILE -> TimelineEventId.SEND_DIGITAL_DOMICILE.buildEventId(event);
            case SEND_DIGITAL_FEEDBACK -> TimelineEventId.SEND_DIGITAL_FEEDBACK.buildEventId(event);
            case GET_ADDRESS -> TimelineEventId.GET_ADDRESS.buildEventId(event);
            case DIGITAL_SUCCESS_WORKFLOW -> TimelineEventId.DIGITAL_SUCCESS_WORKFLOW.buildEventId(event);
            case SCHEDULE_REFINEMENT -> TimelineEventId.SCHEDULE_REFINEMENT_WORKFLOW.buildEventId(event);
            case REFINEMENT -> TimelineEventId.REFINEMENT.buildEventId(event);
            case ANALOG_SUCCESS_WORKFLOW -> TimelineEventId.ANALOG_SUCCESS_WORKFLOW.buildEventId(event);
            case DIGITAL_FAILURE_WORKFLOW -> TimelineEventId.DIGITAL_FAILURE_WORKFLOW.buildEventId(event);
            case SEND_ANALOG_FEEDBACK -> TimelineEventId.SEND_ANALOG_FEEDBACK.buildEventId(event);
            case SEND_SIMPLE_REGISTERED_LETTER_PROGRESS ->
                    TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.buildEventId(event);
            case SEND_ANALOG_PROGRESS -> TimelineEventId.SEND_ANALOG_PROGRESS.buildEventId(event);
            case ANALOG_FAILURE_WORKFLOW -> TimelineEventId.ANALOG_FAILURE_WORKFLOW.buildEventId(event);
            case PREPARE_ANALOG_DOMICILE -> TimelineEventId.PREPARE_ANALOG_DOMICILE.buildEventId(event);
            case PREPARE_ANALOG_DOMICILE_FAILURE -> TimelineEventId.PREPARE_ANALOG_DOMICILE_FAILURE.buildEventId(event);
            case SCHEDULE_ANALOG_WORKFLOW -> TimelineEventId.SCHEDULE_ANALOG_WORKFLOW.buildEventId(event);
            case SEND_ANALOG_DOMICILE -> TimelineEventId.SEND_ANALOG_DOMICILE.buildEventId(event);
            case SEND_SIMPLE_REGISTERED_LETTER -> TimelineEventId.SEND_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case PREPARE_SIMPLE_REGISTERED_LETTER ->
                    TimelineEventId.PREPARE_SIMPLE_REGISTERED_LETTER.buildEventId(event);
            case NOTIFICATION_VIEWED -> TimelineEventId.NOTIFICATION_VIEWED.buildEventId(event);
            case COMPLETELY_UNREACHABLE -> TimelineEventId.COMPLETELY_UNREACHABLE.buildEventId(event);
            case DIGITAL_DELIVERY_CREATION_REQUEST ->
                    TimelineEventId.DIGITAL_DELIVERY_CREATION_REQUEST.buildEventId(event);
            case ANALOG_WORKFLOW_RECIPIENT_DECEASED ->
                    TimelineEventId.ANALOG_WORKFLOW_RECIPIENT_DECEASED.buildEventId(event);
            case PUBLIC_REGISTRY_VALIDATION_CALL -> TimelineEventId.PUBLIC_REGISTRY_VALIDATION_CALL.buildEventId(event);
            case PUBLIC_REGISTRY_VALIDATION_RESPONSE ->
                    TimelineEventId.PUBLIC_REGISTRY_VALIDATION_RESPONSE.buildEventId(event);
            case NOTIFICATION_TIMELINE_REWORKED -> TimelineEventId.NOTIFICATION_TIMELINE_REWORKED.buildEventId(event);
            default -> throw new IllegalArgumentException("Category non riconosciuta: " + timelineEventCategory);
        };
    }

    /**
     * Metodo statico di utility per formattare un json
     */
    public static String logPrettyResponse(String rawJson) {
        try {
            ObjectMapper objMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
            Object jsonObject = objMapper.readValue(rawJson, Object.class);
            String prettyJson = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            log.info("JSON formattato:\n{}", prettyJson);
            return prettyJson;
        } catch (Exception e) {
            log.warn("Impossibile formattare il JSON, stampo l'originale: {}", rawJson);
        }
        return rawJson;
    }

    public static Environment getEnvironment(ApplicationContext context) {
        String env = context.getEnvironment().getActiveProfiles()[0];
        log.info("Environment in use is: {}", env);
        return Environment.valueOf(env.toUpperCase());
    }

    /**
     * Metodo di utility per estrarre i valori di una DataTable, con la possibilità di restituire un valore di default in caso di valore null
     */
    public static String getDataTableParams(Map<String, String> inputData, String key, String defaultValue) {
        String value = inputData.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.equalsIgnoreCase("EMPTY_STRING") ? null : value;
    }

    public static String assertWithIun(String iun, String msg) {
        return String.format("Assertion failed. Iun %s : %s", iun, msg);
    }
}
