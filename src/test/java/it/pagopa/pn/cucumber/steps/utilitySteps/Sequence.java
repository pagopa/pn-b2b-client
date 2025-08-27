package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Sequence {

    //AR
    OK_AR("OK_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "CON018", "RECRN001A", "RECRN001B[DOC:AR]", "RECRN001C"), 0, 0),
    FAIL_AR("FAIL_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002A[FAILCAUSE:M05]", "RECRN002B[DOC:Plico]", "RECRN002C"), 0, 0),
    OK_RETRY_AR("OK-Retry_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN006[FAILCAUSE:F01]", "CON080_ATTEMPT_1", "CON020[DOC:7ZIP;PAGES:3]_ATTEMPT_1", "RECRN001A_ATTEMPT_1", "RECRN001B[DOC:AR]_ATTEMPT_1", "RECRN001C_ATTEMPT_1"), 1, 0),
    OK_GIACENZA_AR("OK-Giacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN011", "RECRN003A", "RECRN003B[DOC:AR]", "RECRN003C"), 0, 0),
    FAIL_GIACENZA_AR("FAIL-Giacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN011", "RECRN004A", "RECRN004B[DOC:Plico]", "RECRN004C"), 0, 0),
    FAIL_IRREPERIBILE_AR("FAIL-Irreperibile_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002D[FAILCAUSE:M04]", "RECRN002E[DOC:Plico]", "RECRN002F"), 0, 0),
    FAIL_COMPIUTA_GIACENZA_AR("FAIL-CompiutaGiacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN005A", "RECRN005B[DOC:Plico]", "RECRN005C"), 0, 0),
    FAIL_DISCOVERY_AR("FAIL-Discovery_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002D[DISCOVERY;FAILCAUSE:M01]", "RECRN002E[DOC:Plico;DOC:Indagine]", "RECRN002F", "CON080_ATTEMPT_1", "CON020[DOC:7ZIP;PAGES:3]_ATTEMPT_1", "RECRN001A_ATTEMPT_1", "RECRN001B[DOC:AR]_ATTEMPT_1", "RECRN001C_ATTEMPT_1"), 0, 1),
    //RIR
    OK_RIR("OK_RIR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRI001", "RECRI002", "RECRI003A", "RECRI003B[DOC:AR]", "RECRI003C"), 0, 0),
    FAIL_RIR("FAIL_RIR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRI001", "RECRI002", "RECRI004A", "RECRI004B[DOC:Plico]", "RECRI004C"), 0, 0),
    //ERROR
    FAIL_CON996_PCRETRY_FURTO_AR("FAIL_CON996_PCRETRY_FURTO_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002D[FAILCAUSE:M04]", "RECRN002E[DOC:Plico]", "RECRN002F"), 0, 0),
    OK_AR_TIMESTAMP_ERR("OK_AR_TIMESTAMP_ERR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "CON018", "RECRN001A", "RECRN001B", "RECRN001A", "RECRN001B", "RECRN001C"), 0, 0),
    OK_AR_NOT_ORDERED("OK_AR_NOT_ORDERED", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "CON018", "RECRN01A", "RECRN01B[DOC:AR]", "RECRN01A", "RECRN01C"), 0, 0),
    OK_AR_BAD_EVENT("OK_AR_BAD_EVENT", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "CON018", "RECRN01A", "RECRN02A", "RECRN01B[DOC:AR]", "RECRN01C"), 0, 0);


    private String name;
    private List<String> events;
    private int pcRetry;
    private int attempts;

    private static final List<String> FEEDBACK_EVENTS = Arrays.asList(
            "RECRN001C", "RECRN002C", "RECRN003C", "RECRN004C", "RECRN005C",
            "RECAG001C", "RECAG002C", "RECAG003C", "RECAG005C", "RECAG006C", "RECAG007C",
            "RECRN002F", "RECAG003F",
            "PNAG012", "RECAG012", "PNRN012",
            "RECRI003C", "RECRI004C");

    private Sequence(String name, List<String> events, int pcRetry, int attempts) {
        this.name = name;
        this.events = events;
        this.pcRetry = pcRetry;
        this.attempts = attempts;
    }

    public static Sequence getByName(String sequenceName) {
        return Arrays.stream(Sequence.values()).filter(s -> s.name.equalsIgnoreCase(sequenceName)).findFirst().orElse(null);
    }

    public static boolean isFeedback(String eventCode) {
        return FEEDBACK_EVENTS.contains(eventCode);
    }

    public static int getPcRetry(Sequence sequence) {
        return sequence.getPcRetry();
    }

}