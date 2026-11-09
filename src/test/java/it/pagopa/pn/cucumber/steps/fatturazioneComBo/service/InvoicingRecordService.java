package it.pagopa.pn.cucumber.steps.fatturazioneComBo.service;

import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InternalTimelineElement;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InvoicingRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;

/**
 * Lettura delle tabelle di fatturazione. Le tabelle sono partizionate per ente e giorno di fatturazione
 * ({@code <paId>_<YYYY-MM-DD>}, giorno in ora italiana) e filtrate per IUN.
 * <p>
 * I record arrivano in modo asincrono: la timeline viene letta dallo stream CDC e l'event source mapping della
 * lambda di fatturazione raggruppa i record fino a 5 minuti. Per questo le assenze si verificano osservando la
 * tabella per tutta una finestra di assestamento, e non con una lettura singola.
 */
@Slf4j
@Component
public class InvoicingRecordService {

    private static final ZoneId INVOICING_ZONE = ZoneId.of("Europe/Rome");
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(30);

    private final DynamoDbService dynamoDbService;
    private final Duration recordTimeout;
    private final Duration settleWindow;

    public InvoicingRecordService(DynamoDbService dynamoDbService,
                                  @Value("${pn.combo-invoicing.record-timeout-minutes:15}") long recordTimeoutMinutes,
                                  @Value("${pn.combo-invoicing.settle-window-minutes:7}") long settleWindowMinutes) {
        this.dynamoDbService = dynamoDbService;
        this.recordTimeout = Duration.ofMinutes(recordTimeoutMinutes);
        this.settleWindow = Duration.ofMinutes(settleWindowMinutes);
    }

    public static LocalDate invoicingDay(Instant timestamp) {
        return timestamp.atZone(INVOICING_ZONE).toLocalDate();
    }

    public List<InvoicingRecord> records(CommunicationDomain domain, String paId, LocalDate invoicingDay, String iun) {
        List<InvoicingRecord> records = dynamoDbService.callAllPages(domain.getInvoicingTable(), Map.of(
                        ":v_paId_invoicingDay", AttributeValue.builder().s(paId + "_" + invoicingDay).build(),
                        ":v_iun", AttributeValue.builder().s(iun).build()))
                .stream()
                .map(InvoicingRecord::from)
                .toList();
        log.info("{} - record di {} del {}: {}", domain.getInvoicingTable().getValue(), iun, invoicingDay, records);
        return records;
    }

    private boolean tableExists(CommunicationDomain domain, String paId, LocalDate invoicingDay, String iun) {
        try {
            records(domain, paId, invoicingDay, iun);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    /**
     * Attende il record di fatturazione dell'elemento di timeline indicato.
     */
    public InvoicingRecord awaitRecordOf(CommunicationDomain domain, String iun, InternalTimelineElement element) {
        LocalDate day = invoicingDay(element.timestamp());
        AtomicReference<InvoicingRecord> found = new AtomicReference<>();
        try {
            await().atMost(recordTimeout).pollInterval(POLL_INTERVAL).ignoreExceptions().until(() -> {
                records(domain, element.paId(), day, iun).stream()
                        .filter(invoicingRecord -> element.timelineElementId().equals(invoicingRecord.timelineElementId()))
                        .findFirst()
                        .ifPresent(found::set);
                return found.get() != null;
            });
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            throw new AssertionError("Entro %s l'elemento %s della notifica %s non risulta su %s"
                    .formatted(recordTimeout, element.timelineElementId(), iun, domain.getInvoicingTable().getValue()), e);
        }
        return found.get();
    }

    /**
     * Verifica che per tutta la finestra di assestamento nessun record della notifica soddisfi la condizione.
     * Se la tabella non esiste (componente non ancora rilasciato nell'ambiente) nessun record può esservi scritto:
     * la verifica è soddisfatta, così i test di non regressione girano prima e dopo il rilascio.
     */
    public void assertNeverRecorded(CommunicationDomain domain, String paId, LocalDate invoicingDay, String iun,
                                    Predicate<InvoicingRecord> unexpected, String description) {
        if (!tableExists(domain, paId, invoicingDay, iun)) {
            log.warn("La tabella {} non esiste in questo ambiente: nessun record può esservi scritto", domain.getInvoicingTable().getValue());
            return;
        }
        AtomicReference<List<InvoicingRecord>> lastRead = new AtomicReference<>(List.of());
        try {
            await().during(settleWindow).atMost(settleWindow.plus(POLL_INTERVAL.multipliedBy(2))).pollInterval(POLL_INTERVAL)
                    .until(() -> {
                        lastRead.set(records(domain, paId, invoicingDay, iun));
                        return lastRead.get().stream().noneMatch(unexpected);
                    });
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            throw new AssertionError("Su %s non deve risultare %s per la notifica %s. Record letti: %s"
                    .formatted(domain.getInvoicingTable().getValue(), description, iun, lastRead.get()), e);
        }
    }
}
