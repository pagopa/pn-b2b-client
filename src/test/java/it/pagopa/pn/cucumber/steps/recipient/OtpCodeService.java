package it.pagopa.pn.cucumber.steps.recipient;

import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.wrapper.LegalCourtesyAddressWrapper;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Recupera da Dynamo (tabella pn-user-attributes) il verification code (OTP) di un
 * destinatario, garantendo di restituire sempre un codice "nuovo".
 * <p>
 * pn-user-attributes scrive un item per ogni richiesta di verifica, con lo stesso
 * {@code pk} (VC#&lt;tipoRecipient&gt;-&lt;uid&gt;) e con il canale codificato nel
 * suffisso della {@code sk} (es. {@code ...#PEC}, {@code ...#SMS}, {@code ...#EMAIL}).
 * Subito dopo una POST il nuovo OTP potrebbe non essere ancora arrivato: senza
 * accorgimenti si rischia di rileggere quello dell'iterazione precedente.
 * <p>
 * Per questo il service tiene memoria, per ogni {@code (pk, tipo)}, dell'ultimo OTP
 * gia' restituito: ad ogni chiamata interroga Dynamo e, finche' trova lo stesso codice,
 * riprova (con una pausa, per non intasare Dynamo) fino ad un numero massimo di tentativi.
 * Essendo un singleton Spring, lo stato sopravvive tra i diversi scenari della suite.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpCodeService {

    private static final int DEFAULT_MAX_RETRIES = 5;
    private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(5);

    private final DynamoDbService dynamoDbService;

    // (pk + tipo canale) -> ultimo verification code gia' restituito ai test
    private final Map<String, String> lastReturnedOtp = new ConcurrentHashMap<>();

    /**
     * Restituisce un OTP nuovo per il destinatario ed il canale indicati, usando i valori
     * di default per numero di tentativi e intervallo di polling.
     */
    public String getNewOtp(Destinatario destinatario, LegalCourtesyAddressWrapper.ChannelType channelType) {
        return getNewOtp(destinatario, channelType, DEFAULT_MAX_RETRIES, DEFAULT_POLL_INTERVAL);
    }

    /**
     * Restituisce un OTP nuovo (diverso dall'ultimo gia' restituito per la stessa
     * {@code (pk, tipo)}) ritentando la lettura da Dynamo fino a {@code maxRetries} volte.
     *
     * @throws AssertionFailedError se entro i tentativi previsti non compare un OTP nuovo.
     */
    public String getNewOtp(
            Destinatario destinatario,
            LegalCourtesyAddressWrapper.ChannelType channelType,
            int maxRetries,
            Duration pollInterval
    ) {
        String pk = buildPk(destinatario);
        String trackingKey = pk + "#" + channelType.getValue();
        String alreadyReturned = lastReturnedOtp.get(trackingKey);

        String lastRead = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            lastRead = readLatestOtp(pk, channelType).orElse(null);

            if (lastRead != null && !Objects.equals(lastRead, alreadyReturned)) {
                lastReturnedOtp.put(trackingKey, lastRead);
                log.info("OTP nuovo per {} (tentativo {}): {}", trackingKey, attempt, lastRead);
                return lastRead;
            }

            log.debug("OTP non ancora aggiornato per {} (tentativo {}): letto={}, gia'Restituito={}",
                    trackingKey, attempt, lastRead, alreadyReturned);
            sleep(pollInterval);
        }

        throw new AssertionFailedError(
                "Nessun OTP nuovo per " + trackingKey + " dopo " + maxRetries
                        + " tentativi. Ultimo letto=" + lastRead + ", gia'Restituito=" + alreadyReturned
        );
    }

    /**
     * "Warm-up": registra come gia' visti tutti gli OTP attualmente presenti su Dynamo
     * per la pk del destinatario, su ogni canale. Da chiamare nel setup dello scenario
     * (dopo la pulizia dei recapiti) cosi' che il primo {@link #getNewOtp} di quello
     * scenario attenda un codice effettivamente nuovo, invece di accettare un OTP residuo
     * di run o iterazioni precedenti.
     */
    public void markExistingOtpAsSeen(Destinatario destinatario) {
        String pk = buildPk(destinatario);
        QueryResponse queryResponse = dynamoDbService.call(
                DynamoTableName.PN_USER_ATTRIBUTES,
                Map.of(":v_pk", AttributeValue.builder().s(pk).build())
        );
        if (queryResponse.items() == null) {
            return;
        }

        // per ogni tipo (suffisso della sk) tengo solo l'item piu' recente
        Map<String, Map<String, AttributeValue>> latestByType = new HashMap<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            if (!item.containsKey("verificationCode")) {
                continue;
            }
            String type = channelTypeOf(item);
            if (type == null) {
                continue;
            }
            Map<String, AttributeValue> current = latestByType.get(type);
            if (current == null || lastUpdateOf(item).isAfter(lastUpdateOf(current))) {
                latestByType.put(type, item);
            }
        }

        latestByType.forEach((type, item) -> {
            String trackingKey = pk + "#" + type;
            String code = item.get("verificationCode").s();
            lastReturnedOtp.put(trackingKey, code);
            log.info("Warm-up OTP: marcato come gia' visto {} -> {}", trackingKey, code);
        });
    }

    /**
     * Legge da Dynamo l'OTP piu' recente per il canale indicato, senza alcun controllo
     * sui codici gia' restituiti. Utile quando interessa solo il valore corrente.
     */
    public Optional<String> readLatestOtp(Destinatario destinatario, LegalCourtesyAddressWrapper.ChannelType channelType) {
        return readLatestOtp(buildPk(destinatario), channelType);
    }

    private Optional<String> readLatestOtp(String pk, LegalCourtesyAddressWrapper.ChannelType channelType) {
        QueryResponse queryResponse = dynamoDbService.call(
                DynamoTableName.PN_USER_ATTRIBUTES,
                Map.of(":v_pk", AttributeValue.builder().s(pk).build())
        );
        log.info("OTP RESPONSE per pk={}: {}", pk, queryResponse);

        if (queryResponse.items() == null) {
            return Optional.empty();
        }

        return queryResponse.items().stream()
                .filter(item -> item.containsKey("verificationCode"))
                .filter(item -> matchesChannel(item, channelType))
                .max(Comparator.comparing(OtpCodeService::lastUpdateOf))
                .map(item -> item.get("verificationCode").s());
    }

    private static String buildPk(Destinatario destinatario) {
        return String.format("VC#%s-%s", destinatario.getRecipientType(), destinatario.getUid());
    }

    private void sleep(Duration pollInterval) {
        try {
            Thread.sleep(pollInterval.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Polling OTP interrotto", e);
        }
    }

    // Il canale e' codificato nel suffisso della sk (es. "<hash>#PEC", "<hash>#SMS"):
    // si confronta tale suffisso con il valore del ChannelType richiesto.
    private static boolean matchesChannel(Map<String, AttributeValue> item, LegalCourtesyAddressWrapper.ChannelType channelType) {
        String type = channelTypeOf(item);
        return type != null && channelType.getValue().equalsIgnoreCase(type);
    }

    // Estrae il tipo di canale dal suffisso della sk (la parte dopo l'ultimo '#').
    private static String channelTypeOf(Map<String, AttributeValue> item) {
        AttributeValue sk = item.get("sk");
        if (sk == null || sk.s() == null) {
            return null;
        }
        String skValue = sk.s();
        int separator = skValue.lastIndexOf('#');
        if (separator < 0) {
            return null;
        }
        return skValue.substring(separator + 1);
    }

    private static Instant lastUpdateOf(Map<String, AttributeValue> item) {
        if (item.containsKey("lastModified")) {
            return parseInstant(item.get("lastModified").s());
        }
        if (item.containsKey("created")) {
            return parseInstant(item.get("created").s());
        }
        return Instant.EPOCH;
    }

    private static Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return OffsetDateTime.parse(value).toInstant();
            } catch (DateTimeParseException e2) {
                log.warn("Timestamp non parsabile sull'item del verification code: {}", value);
                return Instant.EPOCH;
            }
        }
    }
}
