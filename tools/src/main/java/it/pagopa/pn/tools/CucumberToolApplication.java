package it.pagopa.pn.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CLI Tool per la lettura e l'elaborazione di file per Cucumber.
 */
public class CucumberToolApplication {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("       Cucumber Tool CLI - Avvio         ");
        System.out.println("=========================================");

        // Determinazione della cartella da leggere (da parametro CLI oppure default)
        String folderPathStr;
        if (args.length > 0 && !args[0].trim().isEmpty()) {
            folderPathStr = args[0].trim();
        } else {
            // Cartella di default fallback (es. src/test/resources)
            folderPathStr = "src/test/resources";
            System.out.println("[INFO] Nessun percorso specificato. Utilizzo del percorso predefinito: " + folderPathStr);
        }

        Path targetDir = Paths.get(folderPathStr);

        if (!Files.exists(targetDir)) {
            System.err.println("[ERRORE] La cartella specificata non esiste: " + targetDir.toAbsolutePath());
            System.exit(1);
        }

        if (!Files.isDirectory(targetDir)) {
            System.err.println("[ERRORE] Il percorso specificato non è una cartella: " + targetDir.toAbsolutePath());
            System.exit(1);
        }

        System.out.println("[INFO] Lettura file dalla cartella: " + targetDir.toAbsolutePath());

        try {
            List<Path> filesFound = listFiles(targetDir);
            System.out.println("[INFO] Trovati " + filesFound.size() + " file.");

            for (Path filePath : filesFound) {
                processFile(filePath);
            }

            System.out.println("=========================================");
            System.out.println("[SUCCESS] Elaborazione completata con successo!");
            System.out.println("=========================================");

        } catch (IOException e) {
            System.err.println("[ERRORE] Si è verificato un errore durante la lettura dei file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Scansiona la cartella ed eventuali sottocartelle restituendo la lista dei file.
     */
    private static List<Path> listFiles(Path dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    // Puoi aggiungere filtri specifici, es. solo file .feature o .yaml
                    // .filter(p -> p.toString().endsWith(".feature"))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Logica di elaborazione del singolo file.
     */
    private static void processFile(Path filePath) {
        System.out.println(" -> Elaborazione file: " + filePath.getFileName() + " (" + filePath.toAbsolutePath() + ")");
        // TODO: Inserire qui la logica di lettura / analisi del file
    }
}
