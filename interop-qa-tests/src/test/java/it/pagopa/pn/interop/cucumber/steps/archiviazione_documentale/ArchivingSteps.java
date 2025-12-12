package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.ArchivingClient;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.ArchivingContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.FileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import org.assertj.core.api.Assertions;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.within;

public class ArchivingSteps {

    private final ArchivingContext context;
    private final ArchivingClient client;
    private final IFileValidator fileValidator;
    private final TokenResolver tokenResolver;

    public ArchivingSteps( @Value("${s3.unsigned-document-base-path}") String unsignedDocumentBasePath,
                           @Value("${s3.signed-document-base-path}") String signedDocumentBasePath,
                           SharedStepsContext sharedStepsContext ) {
        this.context = new ArchivingContext(unsignedDocumentBasePath, signedDocumentBasePath, sharedStepsContext);
        this.tokenResolver = new TokenResolver(sharedStepsContext);
        this.client = new ArchivingClient(sharedStepsContext, this.tokenResolver);
        this.fileValidator = new FileValidator();
    }

    @Then("verifica nel bucket S3 {bucketType} l'esistenza del file {documentType}")
    public void checkS3Bucket(boolean isSigned, FileType fileType){
        S3BucketInfo bucketInfo = context.getBucket(isSigned, fileType);

        ArchivingClient.SearchFileSeed seed =
                ArchivingClient.SearchFileSeed.builder()
                        .centerTimestamp(context.getCenterTimestamp())
                        .timeoutMs(600_000)
                        .pollIntervalMs(30_000)
                        .deltaSeconds(300)
                        .bucketInfo(bucketInfo)
                        .type(fileType)
                        .isSigned(isSigned)
                        .build();

        ArchivedFile file = client.findS3FileInInterval(seed);
        Assertions.assertThat(file)
                .as("Atteso file %s nel bucket %s ma non è stato trovato", bucketInfo.key(), bucketInfo.bucket())
                .isNotNull();

        context.setCurrentFile(file);
    }

    @Then("verifica che il file nel bucket SIGNED abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    @Then("verifica che il file nel bucket WORM abbia la proprietà \"Retain until date\" pari a 10 anni dalla data di creazione")
    public void checkRetainUntilDate() {

        Instant creationDate = context.getCurrentFile().getCreationDate();
        Instant expectedRetainUntil = creationDate.atZone(ZoneOffset.UTC).plusYears(10).toInstant();
        Instant actualRetainUntilDate = context.getCurrentFile().getRetainUntilDate();

        // Tolleranza di 1 giorno
        Assertions.assertThat(actualRetainUntilDate)
                .as("La Retain until date non è 10 anni dopo la creation date. Creation=%s, Retain=%s", creationDate, actualRetainUntilDate)
                .isCloseTo(expectedRetainUntil, within(1, ChronoUnit.DAYS));
    }

    @Then("verifica che il file contenga le opportune informazioni")
    public void validateFile() throws IOException {
        if(context.getCurrentFile() == null)
            throw new RuntimeException("Nessun file trovato");

        IFileValidator.ValidatorStrategySeed seed = new IFileValidator.ValidatorStrategySeed();
        seed.setFile(context.getCurrentFile());
        seed.setTokenResolver(tokenResolver);

        fileValidator.validate(seed);
    }

    @Then("match test .gz")
    public boolean matchGzTest() throws IOException {

        // 1️⃣ Recupero il file dalle resources
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("events_20251211_012736_875cd6e9-f8c1-47d1-856a-c010fd4c540e.ndjson.gz");

        if (is == null) {
            throw new IllegalStateException("File di test non trovato nelle resources");
        }

        // 2️⃣ Decomprimo il file NDJSON.GZ
        try (GZIPInputStream gis = new GZIPInputStream(is)) {

            // 3️⃣ Condizioni da testare (scegli tu quali verificare)
            Map<String, String> conditions = Map.of(
                    "event_name", "EServiceDescriptorAdded",
                    "id", "11393372-d9ae-47f5-94dd-0a538c000921"
            );


            // 4️⃣ Invoco la tua utilità
            boolean match = FileUtils.ndjsonContainsAll(gis, conditions);
            return match;
        }
    }

    @Then("validation test .gz")
    public void validationGzTest()  throws IOException {
        // 1️⃣ Recupero il file dalle resources
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("events_20251211_012736_875cd6e9-f8c1-47d1-856a-c010fd4c540e.ndjson.gz");

        if (is == null) {
            throw new IllegalStateException("File di test non trovato nelle resources");
        }

        ArchivedFile file = ArchivedFile.builder()
                .type(FileType.CONSUMER_DELEGATION_APPROVED_EVENT)
                .content(is)
                .build();

        IFileValidator.ValidatorStrategySeed seed = new IFileValidator.ValidatorStrategySeed();
        seed.setFile(file);
        seed.setTokenResolver(tokenResolver);

        fileValidator.validate(seed);
    }

    @Then("match test .pm7")
    public boolean matchPm7Test() throws IOException {

        // Recupero il file dalle resources
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("INTEROP_LEGAL_FACTS-100677e1fccc4313ac65bc67b805a3ca-signed.json.gz.p7m");

        if (is == null) {
            throw new IllegalStateException("File di test non trovato nelle resources");
        }

        try {
            // Estrai il contenuto originale dal file .p7m
            CMSSignedData signedData = new CMSSignedData(is);
            CMSProcessable signedContent = signedData.getSignedContent();
            byte[] originalBytes = (byte[]) signedContent.getContent();

            // Convertilo in InputStream (è un .gz)
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(originalBytes))) {

                Map<String, String> conditions = Map.of(
                        "event_name", "EServiceDescriptorAdded",
                        "id", "11393372-d9ae-47f5-94dd-0a538c000921"
                );

                boolean match = FileUtils.ndjsonContainsAll(gis, conditions);
                return match;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("validation test .pm7")
    public void validationPm7Test()  throws IOException {
        // Recupero il file dalle resources
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("INTEROP_LEGAL_FACTS-100677e1fccc4313ac65bc67b805a3ca-signed.json.gz.p7m");

        if (is == null) {
            throw new IllegalStateException("File di test non trovato nelle resources");
        }

        ArchivedFile file = ArchivedFile.builder()
                .type(FileType.CONSUMER_DELEGATION_APPROVED_EVENT_SIGNED)
                .content(is)
                .build();

        IFileValidator.ValidatorStrategySeed seed = new IFileValidator.ValidatorStrategySeed();
        seed.setFile(file);
        seed.setTokenResolver(tokenResolver);

        fileValidator.validate(seed);
    }

}