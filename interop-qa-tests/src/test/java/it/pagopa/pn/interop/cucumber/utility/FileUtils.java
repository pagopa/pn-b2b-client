package it.pagopa.pn.interop.cucumber.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.JsonValidationResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class FileUtils {

    private static final Map<String, Object> fileLocks = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // supporto per Instant, LocalDate, ecc.
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static Object getFileLock(String pathRelativo) {
        return fileLocks.computeIfAbsent(pathRelativo, k -> new Object());
    }

    /**
     * Legge un file CSV dal classpath e restituisce una lista di righe, ognuna composta da una lista di valori.
     *
     * @param pathRelativo      path relativo all'interno di src/test/resources
     * @param separatore        il separatore dei campi (es. "," o ";")
     * @param saltaIntestazione true se vuoi ignorare la prima riga
     * @return lista di righe del CSV, ciascuna come lista di valori
     */
    public static List<List<String>> readCsv(String pathRelativo, String separatore, boolean saltaIntestazione) {
        List<List<String>> righe = new ArrayList<>();
        File file = new File("src/test/resources/" + pathRelativo);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            boolean primaRiga = true;

            while ((line = reader.readLine()) != null) {
                if (primaRiga && saltaIntestazione) {
                    primaRiga = false;
                    continue;
                }

                String[] valori = line.split(separatore);
                List<String> riga = new ArrayList<>();
                for (String valore : valori) {
                    riga.add(valore.trim());
                }
                righe.add(riga);
            }

        } catch (Exception e) {
            System.err.println("Errore durante la lettura del file CSV: " + e.getMessage());
            e.printStackTrace();
        }

        return righe;
    }

    /**
     * Legge un file CSV in modo thread-safe dal classpath e restituisce una lista di righe, ognuna composta da una lista di valori.
     *
     * @param pathRelativo      path relativo all'interno di src/test/resources
     * @param separatore        il separatore dei campi (es. "," o ";")
     * @param saltaIntestazione true se vuoi ignorare la prima riga
     * @return lista di righe del CSV, ciascuna come lista di valori
     */
    public static List<List<String>> readCsvSafe(String pathRelativo, String separatore, boolean saltaIntestazione) {
        synchronized (getFileLock(pathRelativo)) {
            return readCsv(pathRelativo, separatore, saltaIntestazione);
        }
    }

    /**
     * Scrive una lista in un file CSV nella directory src/test/resources.
     * ATTENZIONE: Sovrascrive il file se esiste già.
     *
     * @param pathRelativo path relativo a src/test/resources
     * @param righe        la righe da scrivere
     */
    public static void writeCsv(String pathRelativo, List<List<String>> righe) {
        File file = new File("src/test/resources/" + pathRelativo);

        // Validazione: controlla che tutte le righe abbiano lo stesso numero di colonne
        if (righe == null || righe.isEmpty()) {
            System.err.println("Nessuna riga da scrivere. Il CSV non è stato generato.");
            return;
        }

        int colonneAttese = righe.get(0).size();
        for (int i = 0; i < righe.size(); i++) {
            List<String> riga = righe.get(i);
            if (riga.size() != colonneAttese) {
                System.err.printf("Riga %d ha %d colonne invece di %d: %s%n", i + 1, riga.size(), colonneAttese, riga);
                System.err.println("Il file CSV non è stato scritto per evitare inconsistenza.");
                return;
            }
        }

        // Scrittura su file
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            for (List<String> riga : righe) {
                String linea = String.join(",", riga);
                writer.println(linea);
            }

            System.out.println("File CSV scritto correttamente: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Scrive in modo thread-safe una lista in un file CSV nella directory src/test/resources.
     * ATTENZIONE: Sovrascrive il file se esiste già.
     *
     * @param pathRelativo path relativo a src/test/resources
     * @param righe        la righe da scrivere
     */
    public static void writeCsvSafe(String pathRelativo, List<List<String>> righe) {
        synchronized (getFileLock(pathRelativo)) {
            writeCsv(pathRelativo, righe);
        }
    }

    /**
     * Legge un file CSV, applica una trasformazione alle righe e salva il risultato in modo thread-safe.
     * Questo metodo esegue la lettura, modifica e scrittura del file CSV all'interno di un blocco sincronizzato
     * per garantire che le modifiche siano atomiche e consistenti anche in presenza di accessi concorrenti.
     *
     * @param pathRelativo Il percorso relativo del file CSV rispetto a src/test/resources (es. "dati/miofile.csv")
     * @param separatore   Il separatore dei campi nel CSV (es. "," o ";")
     * @param modifica     Una funzione che riceve la lista di righe lette dal file e restituisce la lista modificata da salvare
     */
    public static void modifyCsvSafe(String pathRelativo, String separatore,
                                     Function<List<List<String>>, List<List<String>>> modifica) {
        synchronized (getFileLock(pathRelativo)) {
            List<List<String>> righe = readCsv(pathRelativo, separatore, false);
            List<List<String>> righeModificate = modifica.apply(righe);
            writeCsv(pathRelativo, righeModificate);
        }
    }

    /**
     * Legge un file JSON dal classpath (src/test/resources) e lo restituisce come JsonNode.
     *
     * @param pathRelativo path relativo all'interno di src/test/resources (es. "data/miofile.json")
     * @return JsonNode radice del JSON
     */
    public static JsonNode readJson(String pathRelativo) {
        File file = new File("src/test/resources/" + pathRelativo);

        try (InputStream in = new FileInputStream(file)) {
            return objectMapper.readTree(in);
        } catch (Exception e) {
            System.err.println("Errore durante la lettura del file JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Legge un file JSON in modo thread-safe dal classpath (src/test/resources).
     *
     * @param pathRelativo path relativo all'interno di src/test/resources (es. "data/miofile.json")
     * @return JsonNode radice del JSON
     */
    public static JsonNode readJsonSafe(String pathRelativo) {
        synchronized (getFileLock(pathRelativo)) {
            return readJson(pathRelativo);
        }
    }

    /**
     * Legge un file JSON dal classpath e lo deserializza in una classe Java.
     *
     * @param pathRelativo path relativo a src/test/resources
     * @param valueType    classe target
     * @return istanza della classe deserializzata
     */
    public static <T> T readJsonAs(String pathRelativo, Class<T> valueType) {
        try (InputStream in = resolveInputStream(pathRelativo)) {
            if (in == null) {
                throw new FileNotFoundException("File non trovato: " + pathRelativo);
            }
            return objectMapper.readValue(in, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la deserializzazione del file JSON: " + pathRelativo, e);
        }
    }

    private static InputStream resolveInputStream(String path) throws FileNotFoundException {
        if (path.startsWith("classpath:")) {
            // rimuovo il prefisso "classpath:" e l'eventuale "/" iniziale
            String cleanPath = path.replace("classpath:", "").replaceFirst("^/", "");
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(cleanPath);
            if (in == null) {
                throw new FileNotFoundException("Risorsa non trovata sul classpath: " + cleanPath);
            }
            return in;
        } else {
            return new FileInputStream(new File(path));
        }
    }

    /**
     * Versione thread-safe di readJsonAs.
     */
    public static <T> T readJsonAsSafe(String pathRelativo, Class<T> valueType) {
        synchronized (getFileLock(pathRelativo)) {
            return readJsonAs(pathRelativo, valueType);
        }
    }

    /**
     * Legge un PDF da uno {@link InputStream} ed estrae il testo,
     * verificando che TUTTE le parole specificate siano presenti.
     *
     * @param inputStream InputStream contenente il PDF da analizzare
     * @param words       lista di parole da verificare (tutte devono essere presenti)
     * @return true se tutte le parole sono contenute nel PDF, false altrimenti
     * @throws RuntimeException in caso di errore nella lettura del PDF
     */
    public static boolean pdfContainsAllWords(InputStream inputStream, List<String> words) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document).toLowerCase();

            for (String word : words) {
                if (!text.contains(word.toLowerCase())) {
                    return false;
                }
            }
            return true;

        } catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura del PDF", e);
        }
    }

    /**
     * Versione thread-safe di {@link #pdfContainsAllWords(InputStream, List)}.
     * Legge il PDF da src/test/resources e verifica che tutte le parole siano presenti.
     *
     * @param pathRelativo percorso del PDF relativo a src/test/resources
     * @param words        lista di parole da verificare
     * @return true se tutte le parole sono presenti, false altrimenti
     */
    public static boolean pdfContainsAllWordsSafe(String pathRelativo, List<String> words) {
        synchronized (getFileLock(pathRelativo)) {
            File file = new File("src/test/resources/" + pathRelativo);
            try (InputStream in = new FileInputStream(file)) {
                return pdfContainsAllWords(in, words);
            } catch (Exception e) {
                throw new RuntimeException("Errore durante la lettura del PDF", e);
            }
        }
    }


    /**
     * Recupera un nodo JSON tramite un percorso "path" annidato.
     * Supporta sia oggetti che array tramite notazione es: "a.b[2].c".
     * <p>
     * Esempi validi:
     * - "utente.nome"
     * - "ordine.articoli[0].prezzo"
     * - "a.b.c[2].x"
     *
     * @param root nodo JSON radice
     * @param path percorso annidato, usando "." per gli oggetti e "[index]" per gli array
     * @return il nodo JSON corrispondente, oppure null se non trovato
     */
    public static JsonNode getNodeByPath(JsonNode root, String path) {
        String[] tokens = path.split("\\.");

        JsonNode current = root;

        for (String token : tokens) {

            // Gestione array: es "items[3]"
            if (token.contains("[") && token.contains("]")) {
                String fieldName = token.substring(0, token.indexOf("["));
                int index = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.indexOf("]")));

                current = current.get(fieldName);
                if (current == null || !current.isArray() || index >= current.size()) {
                    return null;
                }
                current = current.get(index);

            } else {
                // Accesso chiave oggetto semplice
                current = current.get(token);
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    /**
     * Verifica che un oggetto JSON contenga TUTTE le coppie chiave-valore specificate.
     * La chiave può essere un path annidato usando "." e "[index]".
     * <p>
     * Esempi di path:
     * - "utente.nome"
     * - "ordine.articoli[1].descrizione"
     *
     * @param json       oggetto JSON da verificare
     * @param conditions mappa di path → valore atteso
     * @return true se tutte le condizioni sono soddisfatte, false altrimenti
     */
    public static boolean jsonMatchesAll(JsonNode json, Map<String, String> conditions) {
        for (Map.Entry<String, String> entry : conditions.entrySet()) {

            JsonNode node = getNodeByPath(json, entry.getKey());
            if (node == null || node.isMissingNode()) {
                return false;
            }

            if (!node.asText().equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Legge un file NDJSON da uno {@link InputStream} e verifica se
     * almeno una riga soddisfa tutte le condizioni specificate.
     *
     * <p>Ogni riga del NDJSON deve contenere un oggetto JSON valido.</p>
     *
     * <p>Le condizioni sono espresse tramite una mappa nella forma
     * {@code path → valoreAtteso}, dove il path può essere semplice
     * (es. "id") oppure annidato, usando "." per gli oggetti e "[index]"
     * per accedere agli array.</p>
     *
     * <p><b>Esempi di path supportati:</b></p>
     * <ul>
     *   <li>"id"</li>
     *   <li>"utente.nome"</li>
     *   <li>"utente.indirizzi[1].citta"</li>
     *   <li>"ordine.articoli[0].prezzo"</li>
     *   <li>"ordine.articoli[2].varianti[1].colore"</li>
     * </ul>
     *
     * <p><b>Esempio completo di costruzione della mappa {@code conditions}
     * per JSON complessi:</b></p>
     *
     * <pre>{@code
     * Map<String, String> conditions = Map.of(
     *     // Oggetti annidati
     *     "utente.info.nome", "Mario",
     *
     *     // Accesso a un array (secondo elemento)
     *     "utente.indirizzi[1].citta", "Milano",
     *
     *     // Oggetto dentro array
     *     "ordine.articoli[0].codice", "ABC123",
     *
     *     // Array annidati
     *     "ordine.articoli[2].varianti[1].colore", "rosso"
     * );
     * }</pre>
     *
     * <p>Il metodo restituirà {@code true} se almeno una riga del NDJSON
     * contiene tutte le coppie path → valore indicate.</p>
     *
     * @param inputStream InputStream contenente il NDJSON da analizzare
     * @param conditions  mappa path → valore atteso (vedi esempi sopra)
     * @return true se almeno una riga soddisfa tutte le condizioni, false altrimenti
     * @throws RuntimeException in caso di errore di parsing o I/O
     */
    public static boolean ndjsonContainsAll(InputStream inputStream, Map<String, String> conditions) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;

            while ((line = reader.readLine()) != null) {
                JsonNode json = objectMapper.readTree(line);

                if (jsonMatchesAll(json, conditions)) {
                    return true;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura del NDJSON da InputStream", e);
        }

        return false;
    }

    /**
     * Versione thread-safe di {@link #ndjsonContainsAll(InputStream, Map)}.
     * Legge il NDJSON da src/test/resources/pathRelativo.
     *
     * @param pathRelativo percorso del file NDJSON
     * @param conditions   mappa path → valore atteso
     * @return true se matcha almeno una riga, false altrimenti
     */
    public static boolean ndjsonContainsAllSafe(String pathRelativo, Map<String, String> conditions) {
        synchronized (getFileLock(pathRelativo)) {
            File file = new File("src/test/resources/" + pathRelativo);

            try (InputStream in = new FileInputStream(file)) {
                return ndjsonContainsAll(in, conditions);
            } catch (Exception e) {
                throw new RuntimeException("Errore durante la lettura del NDJSON (safe): " + pathRelativo, e);
            }
        }
    }

    public static void validateNdjson(
            InputStream is,
            Predicate<JsonNode> candidateSelector,
            Function<JsonNode, JsonValidationResult> validator,
            String notFoundMessage
    ) {
        List<String> aggregatedErrors = new ArrayList<>();
        boolean foundCandidate = false;
        boolean foundValid = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                JsonNode json = objectMapper.readTree(line);

                if (!candidateSelector.test(json)) continue;
                foundCandidate = true;

                JsonValidationResult result = validator.apply(json);

                if (result.isValid()) {
                    foundValid = true;
                    break; // una riga valida è sufficiente
                } else {
                    String errorBlock = String.format(
                            "Riga %d NON valida:%nJSON: %s%nErrori:%n  - %s%n%n",
                            lineNumber,
                            result.rawJson(),
                            String.join("\n  - ", result.errors())
                    );
                    aggregatedErrors.add(errorBlock);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore nella validazione NDJSON", e);
        }

        if (!foundCandidate) {
            throw new RuntimeException(notFoundMessage);
        }

        if (!foundValid) {
            String message = "Righe candidate trovate, ma tutte non valide:\n" +
                    String.join("\n", aggregatedErrors);
            throw new RuntimeException(message);
        }
    }


    public static boolean validateNdjsonAnyMatch(
            InputStream is,
            Predicate<JsonNode> candidateSelector,
            Function<JsonNode, JsonValidationResult> validator
    ) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean foundCandidate = false;

            while ((line = reader.readLine()) != null) {

                JsonNode json = objectMapper.readTree(line);

                if (!candidateSelector.test(json)) {
                    continue;
                }

                foundCandidate = true;

                JsonValidationResult result = validator.apply(json);

                if (result.isValid()) {
                    return true; // basta UNA riga valida
                }
            }

            // nessuna riga candidata o tutte invalide
            return false;

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la validazione NDJSON", e);
        }
    }


}
