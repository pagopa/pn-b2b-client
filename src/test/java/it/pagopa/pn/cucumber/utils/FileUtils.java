package it.pagopa.pn.cucumber.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FileUtils {

    private static final Map<String, Object> fileLocks = new ConcurrentHashMap<>();

    private static Object getFileLock(String pathRelativo) {
        return fileLocks.computeIfAbsent(pathRelativo, k -> new Object());
    }

    /**
     * Legge un file CSV dal classpath e restituisce una lista di righe, ognuna composta da una lista di valori.
     * @param pathRelativo path relativo all'interno di src/test/resources
     * @param separatore il separatore dei campi (es. "," o ";")
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
     * @param pathRelativo path relativo all'interno di src/test/resources
     * @param separatore il separatore dei campi (es. "," o ";")
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
     * @param pathRelativo path relativo a src/test/resources
     * @param righe la righe da scrivere
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
     * @param pathRelativo path relativo a src/test/resources
     * @param righe la righe da scrivere
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
     * @param pathRelativo       Il percorso relativo del file CSV rispetto a src/test/resources (es. "dati/miofile.csv")
     * @param separatore         Il separatore dei campi nel CSV (es. "," o ";")
     * @param modifica           Una funzione che riceve la lista di righe lette dal file e restituisce la lista modificata da salvare
     */
    public static void modifyCsvSafe(String pathRelativo, String separatore,
                                     Function<List<List<String>>, List<List<String>>> modifica) {
        synchronized (getFileLock(pathRelativo)) {
            List<List<String>> righe = readCsv(pathRelativo, separatore, false);
            List<List<String>> righeModificate = modifica.apply(righe);
            writeCsv(pathRelativo, righeModificate);
        }
    }
}
