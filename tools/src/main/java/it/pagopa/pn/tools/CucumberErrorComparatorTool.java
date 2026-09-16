package it.pagopa.pn.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CLI Tool per il matching di un singolo ID TEST ed ERROR LOG inseriti in input
 * da riga di comando o interattivi contro tutti i file CSV del DB (tools-resources).
 */
public class CucumberErrorComparatorTool {

    // Codici Colore ANSI per la Console Terminale
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    // Pattern Regex per estrazione degli elementi della Tupla
    private static final Pattern FEATURE_STEP_PATTERN = Pattern.compile("at (?:✽|\\?|\\u273D)?\\s*\\.(.+?)\\((?:classpath:)?(?:.*?/)?([^/:]+\\.feature):\\d+\\)", Pattern.DOTALL);
    private static final Pattern EXCEPTION_TYPE_PATTERN = Pattern.compile("(?:failed:\\s*)?([a-zA-Z0-9_\\.]+(?:Exception|Error|AssertionFailedError))");
    private static final Pattern HTTP_STATUS_PATTERN = Pattern.compile("(?:expected:\\s*<(\\d{3})>\\s*but was:\\s*<(\\d{3})>|expected:\\s*(\\d{3})\\s*but was\\s*:\\s*(\\d{3})|status code (\\d{3})|(\u0034\\d{2}|\u0035\\d{2}))");
    private static final Pattern BUSINESS_ERROR_CODE_PATTERN = Pattern.compile("\"code\"\\s*:\\s*\"([^\"]+)\"");

    /**
     * Rappresentazione immutabile della Tupla Minima di Identificazione (4 elementi).
     */
    public static class ErrorTuple {
        private final String testId;            // Elemento 1: ID TEST
        private final String stepAndFeature;    // Elemento 2: Step Gherkin + Feature File (escluso numero di riga)
        private final String exceptionClass;    // Elemento 3: Classe dell'eccezione Java
        private final String rootCause;         // Elemento 4: Causa radice / Contratto normalizzato

        public ErrorTuple(String testId, String stepAndFeature, String exceptionClass, String rootCause) {
            this.testId = testId != null ? testId.trim() : "";
            this.stepAndFeature = stepAndFeature != null ? stepAndFeature.trim() : "";
            this.exceptionClass = exceptionClass != null ? exceptionClass.trim() : "";
            this.rootCause = rootCause != null ? rootCause.trim() : "";
        }

        public String getTestId() { return testId; }
        public String getStepAndFeature() { return stepAndFeature; }
        public String getExceptionClass() { return exceptionClass; }
        public String getRootCause() { return rootCause; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErrorTuple that = (ErrorTuple) o;
            return testId.equalsIgnoreCase(that.testId) &&
                   stepAndFeature.equals(that.stepAndFeature) &&
                   exceptionClass.equals(that.exceptionClass) &&
                   rootCause.equals(that.rootCause);
        }

        @Override
        public int hashCode() {
            return Objects.hash(testId.toLowerCase(), stepAndFeature, exceptionClass, rootCause);
        }
    }

    public static class TestExecutionRecord {
        private final String rawLine;
        private final String testId;
        private final String rawErrorLog;
        private final ErrorTuple errorTuple;
        private final String result;
        private final String sourceFile;

        public TestExecutionRecord(String rawLine, String testId, String rawErrorLog, ErrorTuple errorTuple, String result, String sourceFile) {
            this.rawLine = rawLine;
            this.testId = testId;
            this.rawErrorLog = rawErrorLog;
            this.errorTuple = errorTuple;
            this.result = result;
            this.sourceFile = sourceFile;
        }

        public String getTestId() { return testId; }
        public String getRawErrorLog() { return rawErrorLog; }
        public ErrorTuple getErrorTuple() { return errorTuple; }
        public String getResult() { return result; }
        public String getSourceFile() { return sourceFile; }
    }

    public static class ComparisonResult {
        private final String testId;
        private final String status;
        private final ErrorTuple targetTuple;
        private final String matchedResult;
        private final String matchedSourceFile;
        private final String matchedRawErrorLog;
        private final String notes;

        public ComparisonResult(String testId, String status, ErrorTuple targetTuple, String matchedResult, String matchedSourceFile, String matchedRawErrorLog, String notes) {
            this.testId = testId;
            this.status = status;
            this.targetTuple = targetTuple;
            this.matchedResult = matchedResult;
            this.matchedSourceFile = matchedSourceFile;
            this.matchedRawErrorLog = matchedRawErrorLog;
            this.notes = notes;
        }

        public String getTestId() { return testId; }
        public String getStatus() { return status; }
        public ErrorTuple getTargetTuple() { return targetTuple; }
        public String getMatchedResult() { return matchedResult; }
        public String getMatchedSourceFile() { return matchedSourceFile; }
        public String getMatchedRawErrorLog() { return matchedRawErrorLog; }
        public String getNotes() { return notes; }
    }

    public static void main(String[] args) {
        System.out.println(ANSI_CYAN + ANSI_BOLD + "=================================================" + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "   Cucumber Single Error Matcher - Input vs DB   " + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "=================================================" + ANSI_RESET);

        String inputTestId = null;
        String inputErrorLog = null;
        String inputResult = null;
        String folderPathStr = "tools-resources";

        // Gestione parametri da riga di comando: ID, ERROR_LOG, [RESULT], [DB_FOLDER]
        if (args.length >= 2) {
            inputTestId = args[0];
            inputErrorLog = args[1];
            if (args.length >= 3) {
                if (Files.isDirectory(Paths.get(args[2]))) {
                    folderPathStr = args[2];
                } else {
                    inputResult = args[2];
                    if (args.length >= 4) {
                        folderPathStr = args[3];
                    }
                }
            }
        } else {
            // Modalità interattiva
            Scanner scanner = new Scanner(System.in);
            System.out.print(ANSI_YELLOW + "Inserisci l'ID TEST (es. [AGREEMENT_ACTIVATE_04A]): " + ANSI_RESET);
            inputTestId = scanner.nextLine().trim();

            System.out.print(ANSI_YELLOW + "Inserisci il RESULT attuale dell'input (opzionale, premi INVIO per saltare): " + ANSI_RESET);
            inputResult = scanner.nextLine().trim();

            System.out.println(ANSI_YELLOW + "Inserisci il testo dell'ERROR LOG (premi INVIO due volte per confermare):" + ANSI_RESET);
            StringBuilder sbLog = new StringBuilder();
            String line;
            while (scanner.hasNextLine() && !(line = scanner.nextLine()).isEmpty()) {
                sbLog.append(line).append("\n");
            }
            inputErrorLog = sbLog.toString().trim();
        }

        if (inputTestId == null || inputTestId.isEmpty() || inputErrorLog == null || inputErrorLog.isEmpty()) {
            System.err.println(ANSI_RED + "[ERRORE] ID TEST ed ERROR LOG sono obbligatori." + ANSI_RESET);
            System.exit(1);
        }

        Path resourcesDir = Paths.get(folderPathStr);
        if (!Files.exists(resourcesDir) || !Files.isDirectory(resourcesDir)) {
            System.err.println(ANSI_RED + "[ERRORE] La cartella DB specificata non esiste: " + resourcesDir.toAbsolutePath() + ANSI_RESET);
            System.exit(1);
        }

        try {
            List<Path> dbCsvFiles;
            try (Stream<Path> stream = Files.walk(resourcesDir)) {
                dbCsvFiles = stream
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".csv"))
                        .sorted()
                        .collect(Collectors.toList());
            }

            if (dbCsvFiles.isEmpty()) {
                System.out.println(ANSI_YELLOW + "[AVVISO] Nessun file CSV trovato nel DB " + resourcesDir.toAbsolutePath() + ANSI_RESET);
                return;
            }

            System.out.println(ANSI_YELLOW + "\n[INPUT RICEVUTO]:" + ANSI_RESET);
            System.out.println(ANSI_BOLD + "ID TEST: " + ANSI_RESET + ANSI_PURPLE + inputTestId + ANSI_RESET);
            if (inputResult != null && !inputResult.isEmpty()) {
                System.out.println(ANSI_BOLD + "RESULT Input Fornito: " + ANSI_RESET + ANSI_YELLOW + "\"" + inputResult + "\"" + ANSI_RESET);
            }
            
            ErrorTuple inputTuple = extractErrorTuple(inputTestId, inputErrorLog);
            System.out.println(ANSI_BOLD + "Tupla Estratta dall'Input:" + ANSI_RESET);
            System.out.println("  1. ID TEST: " + ANSI_PURPLE + inputTuple.getTestId() + ANSI_RESET);
            System.out.println("  2. Step & Feature: " + ANSI_CYAN + inputTuple.getStepAndFeature() + ANSI_RESET);
            System.out.println("  3. Classe Eccezione: " + ANSI_RED + inputTuple.getExceptionClass() + ANSI_RESET);
            System.out.println("  4. Root Cause / Contratto: " + ANSI_YELLOW + inputTuple.getRootCause() + ANSI_RESET);

            System.out.println(ANSI_YELLOW + "\n[INFO] File nel DB da scansionare (" + dbCsvFiles.size() + "):" + ANSI_RESET);
            for (Path dbF : dbCsvFiles) {
                System.out.println(ANSI_BLUE + "  - " + dbF.getFileName() + ANSI_RESET);
            }

            System.out.println(ANSI_CYAN + "\nRicerca match della Tupla su tutto il DB in corso...\n" + ANSI_RESET);

            Map<Path, List<TestExecutionRecord>> dbRecordsMap = new LinkedHashMap<>();
            for (Path dbFile : dbCsvFiles) {
                dbRecordsMap.put(dbFile, readAndNormalizeCsv(dbFile));
            }

            // Matching dell'input singolo contro tutti i record del DB
            ComparisonResult result = matchSingleInputAgainstDb(inputTestId, inputTuple, dbRecordsMap);

            printSingleReport(result);

        } catch (Exception e) {
            System.err.println(ANSI_RED + "[ERRORE] Errore durante l'elaborazione del matching: " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
        }
    }

    public static List<TestExecutionRecord> readAndNormalizeCsv(Path filePath) throws IOException {
        List<TestExecutionRecord> recordList = new ArrayList<>();
        List<List<String>> records = parseMultilineCsv(filePath);

        if (records.isEmpty()) {
            return recordList;
        }

        int testIdColIndex = 0;
        int errorLogColIndex = 1;
        int resultColIndex = 4;

        List<String> headers = records.get(0);
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i).trim().replaceAll("^\"|\"$", "").toUpperCase();
            if (h.contains("ID TEST") || h.contains("TEST ID") || h.contains("TEST_ID")) {
                testIdColIndex = i;
            } else if (h.contains("ERROR LOG") || h.contains("ERROR") || h.contains("LOG") || h.contains("STACKTRACE")) {
                errorLogColIndex = i;
            } else if (h.contains("RESULT") || h.contains("RISULTATO") || h.contains("NOTE")) {
                resultColIndex = i;
            }
        }

        for (int rowIdx = 1; rowIdx < records.size(); rowIdx++) {
            List<String> row = records.get(rowIdx);
            if (row.size() <= testIdColIndex) continue;

            String rawTestId = row.get(testIdColIndex).trim().replaceAll("^\"|\"$", "");
            String rawErrorLog = (row.size() > errorLogColIndex) ? row.get(errorLogColIndex).trim().replaceAll("^\"|\"$", "") : "";
            String resultVal = (row.size() > resultColIndex) ? row.get(resultColIndex).trim().replaceAll("^\"|\"$", "") : "";

            if (rawTestId.isEmpty()) continue;

            ErrorTuple tuple = extractErrorTuple(rawTestId, rawErrorLog);
            recordList.add(new TestExecutionRecord(String.join(",", row), rawTestId, rawErrorLog, tuple, resultVal, filePath.getFileName().toString()));
        }

        return recordList;
    }

    /**
     * Estrattore della Tupla Fissa di 4 elementi.
     */
    public static ErrorTuple extractErrorTuple(String testId, String rawLog) {
        if (rawLog == null || rawLog.trim().isEmpty()) {
            return new ErrorTuple(testId, "N/A", "N/A", "N/A");
        }

        String stepAndFeature = "N/A";
        Matcher featureMatcher = FEATURE_STEP_PATTERN.matcher(rawLog);
        if (featureMatcher.find()) {
            String stepText = featureMatcher.group(1).trim();
            String featureFile = featureMatcher.group(2).trim();
            stepAndFeature = featureFile + " -> " + stepText;
        }

        String exceptionClass = "N/A";
        Matcher excMatcher = EXCEPTION_TYPE_PATTERN.matcher(rawLog);
        if (excMatcher.find()) {
            String fullClass = excMatcher.group(1).trim();
            int lastDot = fullClass.lastIndexOf('.');
            exceptionClass = (lastDot != -1) ? fullClass.substring(lastDot + 1) : fullClass;
        }

        String rootCause = "N/A";
        Matcher busCodeMatcher = BUSINESS_ERROR_CODE_PATTERN.matcher(rawLog);
        if (busCodeMatcher.find()) {
            rootCause = "BusinessCode: " + busCodeMatcher.group(1).trim();
        } else if (rawLog.contains("expected:") || rawLog.contains("status code")) {
            Matcher httpMatcher = HTTP_STATUS_PATTERN.matcher(rawLog);
            if (httpMatcher.find()) {
                if (httpMatcher.group(1) != null && httpMatcher.group(2) != null) {
                    rootCause = "HTTP Expected " + httpMatcher.group(1) + " != Was " + httpMatcher.group(2);
                } else if (httpMatcher.group(3) != null && httpMatcher.group(4) != null) {
                    rootCause = "HTTP Expected " + httpMatcher.group(3) + " != Was " + httpMatcher.group(4);
                } else if (httpMatcher.group(5) != null) {
                    rootCause = "HTTP Status Code " + httpMatcher.group(5);
                } else {
                    rootCause = "HTTP Status " + httpMatcher.group(0);
                }
            } else {
                rootCause = "HTTP Status Mismatch";
            }
        } else if (rawLog.contains("Polling") || rawLog.contains("not found") || rawLog.contains("not Found") || rawLog.contains("Not Found")) {
            if (rawLog.contains("EService not found")) {
                rootCause = "EService not found";
            } else if (rawLog.contains("Not Found expected notification")) {
                rootCause = "Not Found expected notification";
            } else if (rawLog.contains("analisi del rischio")) {
                rootCause = "Risk Analysis Document Not Found";
            } else {
                rootCause = "Polling Timeout / Eventual Consistency Error";
            }
        } else {
            String firstLine = rawLog.split("\n")[0].replaceAll("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "<UUID>").trim();
            rootCause = firstLine.length() > 80 ? firstLine.substring(0, 80) + "..." : firstLine;
        }

        return new ErrorTuple(testId, stepAndFeature, exceptionClass, rootCause);
    }

    private static List<List<String>> parseMultilineCsv(Path filePath) throws IOException {
        List<List<String>> result = new ArrayList<>();
        String content = Files.readString(filePath);
        if (content == null || content.isEmpty()) return result;

        char delimiter = ',';
        int firstLineEnd = content.indexOf('\n');
        String firstLine = firstLineEnd != -1 ? content.substring(0, firstLineEnd) : content;
        if (firstLine.contains(";")) delimiter = ';';
        else if (firstLine.contains("\t")) delimiter = '\t';

        List<String> currentRow = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter && !inQuotes) {
                currentRow.add(currentField.toString());
                currentField.setLength(0);
            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                if (c == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    i++;
                }
                currentRow.add(currentField.toString());
                currentField.setLength(0);
                if (!currentRow.isEmpty() && !(currentRow.size() == 1 && currentRow.get(0).trim().isEmpty())) {
                    result.add(new ArrayList<>(currentRow));
                }
                currentRow.clear();
            } else {
                currentField.append(c);
            }
        }

        if (currentField.length() > 0 || !currentRow.isEmpty()) {
            currentRow.add(currentField.toString());
            if (!currentRow.isEmpty() && !(currentRow.size() == 1 && currentRow.get(0).trim().isEmpty())) {
                result.add(currentRow);
            }
        }

        return result;
    }

    /**
     * Esegue il matching di un singolo ID TEST + Tupla estratta contro tutti i CSV del DB.
     */
    public static ComparisonResult matchSingleInputAgainstDb(String inputTestId, ErrorTuple inputTuple, Map<Path, List<TestExecutionRecord>> dbRecordsMap) {
        TestExecutionRecord matchedDbRecord = null;
        boolean isExactTupleMatch = false;

        for (Map.Entry<Path, List<TestExecutionRecord>> entry : dbRecordsMap.entrySet()) {
            List<TestExecutionRecord> dbList = entry.getValue();

            for (TestExecutionRecord recDb : dbList) {
                if (inputTuple.equals(recDb.getErrorTuple())) {
                    matchedDbRecord = recDb;
                    isExactTupleMatch = true;
                    break;
                } else if (inputTestId.equalsIgnoreCase(recDb.getTestId()) && matchedDbRecord == null) {
                    matchedDbRecord = recDb;
                }
            }
            if (isExactTupleMatch) break;
        }

        if (isExactTupleMatch) {
            String note = "Coincidenza al 100% della Tupla d'Errore trovata nel file DB: " + matchedDbRecord.getSourceFile();
            return new ComparisonResult(
                    inputTestId,
                    "STESSO ERRORE",
                    inputTuple,
                    matchedDbRecord.getResult(),
                    matchedDbRecord.getSourceFile(),
                    matchedDbRecord.getRawErrorLog(),
                    note
            );
        } else if (matchedDbRecord != null) {
            String note = "ID TEST trovato nel file DB " + matchedDbRecord.getSourceFile() + " ma la Tupla d'Errore non coincide.";
            return new ComparisonResult(
                    inputTestId,
                    "ERRORE DIVERGENTE",
                    inputTuple,
                    matchedDbRecord.getResult(),
                    matchedDbRecord.getSourceFile(),
                    matchedDbRecord.getRawErrorLog(),
                    note
            );
        } else {
            return new ComparisonResult(
                    inputTestId,
                    "NESSUN MATCH IN DB",
                    inputTuple,
                    "",
                    "",
                    "",
                    "Nessun ID TEST o Tupla d'Errore corrispondente trovato in alcun file del DB"
            );
        }
    }

    private static void printSingleReport(ComparisonResult res) {
        System.out.println(ANSI_CYAN + ANSI_BOLD + "=================================================" + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "            RISULTATO DEL MATCHING               " + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "=================================================\n" + ANSI_RESET);

        if ("STESSO ERRORE".equalsIgnoreCase(res.getStatus())) {
            System.out.println(ANSI_BOLD + "ID TEST: " + ANSI_RESET + ANSI_PURPLE + res.getTestId() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "Stato Matching: " + ANSI_RESET + ANSI_GREEN + res.getStatus() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "File DB Matched: " + ANSI_RESET + ANSI_BLUE + res.getMatchedSourceFile() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "RESULT da Copiare: " + ANSI_RESET + ANSI_YELLOW + ANSI_BOLD + "\"" + res.getMatchedResult() + "\"" + ANSI_RESET);
            System.out.println(ANSI_BOLD + "ERROR LOG Trovato nel DB: " + ANSI_RESET);
            System.out.println(ANSI_CYAN + "-------------------------------------------------" + ANSI_RESET);
            System.out.println(res.getMatchedRawErrorLog());
            System.out.println(ANSI_CYAN + "-------------------------------------------------" + ANSI_RESET);
        } else if ("ERRORE DIVERGENTE".equalsIgnoreCase(res.getStatus())) {
            System.out.println(ANSI_BOLD + "ID TEST: " + ANSI_RESET + ANSI_PURPLE + res.getTestId() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "Stato Matching: " + ANSI_RESET + ANSI_RED + res.getStatus() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "File DB dove risiede l'ID TEST: " + ANSI_RESET + ANSI_BLUE + res.getMatchedSourceFile() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "ERROR LOG Presente nel DB: " + ANSI_RESET);
            System.out.println(ANSI_CYAN + "-------------------------------------------------" + ANSI_RESET);
            System.out.println(res.getMatchedRawErrorLog());
            System.out.println(ANSI_CYAN + "-------------------------------------------------" + ANSI_RESET);
        } else {
            System.out.println(ANSI_BOLD + "ID TEST: " + ANSI_RESET + ANSI_PURPLE + res.getTestId() + ANSI_RESET);
            System.out.println(ANSI_BOLD + "Stato Matching: " + ANSI_RESET + ANSI_YELLOW + res.getStatus() + ANSI_RESET);
        }
        System.out.println(ANSI_CYAN + "\n=================================================" + ANSI_RESET);
    }
}
