package it.pagopa.pn.cucumber.steps.delayer.loader;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.*;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class DelayerCsvLoader {

    private static final String CSV_PATH = "it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/delayer/csv";

    private final DelayerContext context;

    public void readCsv(String csvFileName, int expectedCount) {
        List<List<String>> rawCsv = FileUtils.readCsvSafe(String.join("/", CSV_PATH, csvFileName), ";", false);
        List<String> header = rawCsv.get(0);
        int actualCount = rawCsv.size() - 1;

        context.numeroNotifiche = actualCount;

        if (actualCount != expectedCount) {
            throw new RuntimeException("Numero di notifiche nel CSV (%d) diverso da quello atteso (%d)".formatted(actualCount, expectedCount));
        }

        for (int i = 1; i <= actualCount; i++) {
            DelayerPaperDelivery delivery = new DelayerPaperDelivery(header, rawCsv.get(i));
            context.actualCsv.add(delivery);
        }
    }

    public Stream<DelayerPaperDelivery> downloadResidualPapers(String url) {
        try {
            Resource resource = new UrlResource(url);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            )) {

                List<String> lines = reader.lines().toList();

                if (lines.isEmpty()) return Stream.of();

                List<String> header = Arrays.stream(lines.get(0).split(";", -1))
                        .map(String::trim)
                        .toList();

                return lines.stream()
                        .skip(1)
                        .filter(line -> line != null && !line.isBlank())
                        .map(line -> Arrays.stream(line.split(";", -1))
                                .map(String::trim)
                                .toList())
                        .map(row -> new DelayerPaperDelivery(header, row));
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore download CSV", e);
        }
    }

    public void initializeExpectedDeliveryDate(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        if (context.expectedDeliveryDate == null) {
            String deliveryDate = rows.get(0).getOrDefault("deliveryWeek", null);
            if(deliveryDate != null) {
                context.expectedDeliveryDate = deliveryDate.equalsIgnoreCase("NEXT_MONDAY")
                        ? getNextMonday(1)
                        : LocalDate.parse(deliveryDate).format(DateTimeFormatter.ISO_LOCAL_DATE);

            } else context.expectedDeliveryDate = getCurrentMonday();
        }
    }

    public void initializeLimits() {
        for (DelayerPaperDelivery delivery : context.actualCsv) {
            String senderKey = getSenderKey(delivery);
            String unifiedDeliveryDriverKey = getUnifiedDeliveryDriverKey(delivery);
            String capDeliveryDriverKey = getCapDeliveryDriverKey(delivery);

            // Inizializza limite mittente se non presente
            context.senderLimitMap.putIfAbsent(senderKey, 0);

            // Se driverKey non esiste, crea nuova mappa cap -> 0
            context.driverCapacityMap.computeIfAbsent(unifiedDeliveryDriverKey, k -> new HashMap<>());
            context.usedDriverCapacityMap.computeIfAbsent(unifiedDeliveryDriverKey, k -> new HashMap<>());

            // Se cap non esiste per quel driverKey, aggiungilo
            Map<String, Integer> capMap = context.driverCapacityMap.get(unifiedDeliveryDriverKey);
            capMap.putIfAbsent(capDeliveryDriverKey, 0);

            Map<String, Integer> capMap2 = context.usedDriverCapacityMap.get(unifiedDeliveryDriverKey);
            capMap2.putIfAbsent(capDeliveryDriverKey, 0);
        }
    }


    public void initializeSeeds(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String seed = row.get("seed");
            int expectedCount = Integer.parseInt(row.get("quantita"));

            List<DelayerPaperDelivery> matching = context.actualCsv.stream()
                    .filter(d -> hasSeedInRequestId(seed, d))
                    .toList();

            if (matching.size() != expectedCount) {
                throw new IllegalStateException("Seed '%s': trovate %d notifiche, attese %d"
                        .formatted(seed, matching.size(), expectedCount));
            }

            context.groupedBySeed.put(seed, matching);

            // Inizializza strutture di pianificazione per quel seed
            Map<String, List<DelayerPaperDelivery>> workflowMap = new HashMap<>();
            for (var step : EnumSet.allOf(WorkflowSteps.class)) {
                workflowMap.put(step.name(), new ArrayList<>());
            }

            context.expectedPianification.put(seed, new HashMap<>(workflowMap));
            context.actualPianification.put(seed, new HashMap<>(workflowMap));
        }
    }
}
