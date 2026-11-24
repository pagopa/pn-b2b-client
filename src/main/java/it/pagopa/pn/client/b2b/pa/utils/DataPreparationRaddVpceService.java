package it.pagopa.pn.client.b2b.pa.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataPreparationRaddVpceService {

    private static final Object FILE_LOCK = new Object();

    private static final Path WRITE_PATH = Paths.get("target/output/data-preparation.json");
    private static final Path READ_PATH = Paths.get("src/main/resources/output/data-preparation.json");

    private final ObjectMapper mapper = new ObjectMapper();

    public void save(String key, Map<String, String> data) throws IOException {

        synchronized (FILE_LOCK) {

            Files.createDirectories(WRITE_PATH.getParent());

            Map<String, Map<String, String>> allData = new HashMap<>();

            if (Files.exists(WRITE_PATH)) {
                allData = mapper.readValue(
                        WRITE_PATH.toFile(),
                        new TypeReference<>() {
                        }
                );
            }
            data.entrySet().removeIf(e -> e.getValue() == null || e.getValue().isBlank());
            allData.put(key, data);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(WRITE_PATH.toFile(), allData);
        }
    }

    public Map<String, String> load(String key) throws IOException {

        if (!Files.exists(READ_PATH)) {
            throw new RuntimeException("File non trovato: " + READ_PATH.toAbsolutePath());
        }

        Map<String, Map<String, String>> allData =
                mapper.readValue(READ_PATH.toFile(), new TypeReference<>() {
                });

        Map<String, String> data = allData.get(key);

        if (data == null) {
            throw new RuntimeException("Chiave non trovata: " + key);
        }
        return data;
    }


}