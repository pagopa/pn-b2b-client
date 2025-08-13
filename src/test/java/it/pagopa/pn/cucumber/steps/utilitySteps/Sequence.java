package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Sequence {

    //AR
    OK_AR("OK_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "CON018", "RECRN001A", "RECRN001B[DOC:AR]", "RECRN001C")),
    FAIL_AR("FAIL_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002A[FAILCAUSE:M05]", "RECRN002B[DOC:Plico]", "RECRN002C")),
    OK_RETRY_AR("OK-Retry_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN006[FAILCAUSE:F01]", "CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN001A", "RECRN001B[DOC:AR]", "RECRN001C")),
    OK_GIACENZA_AR("OK-Giacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN011", "RECRN003A", "RECRN003B[DOC:AR]", "RECRN003C")),
    FAIL_GIACENZA_AR("FAIL-Giacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN011", "RECRN004A", "RECRN004B[DOC:Plico]", "RECRN004C")),
    FAIL_IRREPERIBILE_AR("FAIL-Irreperibile_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002D[FAILCAUSE:M04]", "RECRN002E[DOC:Plico]", "RECRN002F")),
    FAIL_COMPIUTA_GIACENZA_AR("FAIL-CompiutaGiacenza_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN010", "RECRN005A", "RECRN005B[DOC:Plico]", "RECRN005C")),
    FAIL_DISCOVERY_AR("FAIL-Discovery_AR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN002D[DISCOVERY;FAILCAUSE:M01]", "RECRN002E[DOC:Plico;DOC:Indagine]", "RECRN002F", "CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRN001A", "RECRN001B[DOC:AR]", "RECRN001C")),
    //RIR
    OK_RIR("OK_RIR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRI001", "RECRI002", "RECRI003A", "RECRI003B[DOC:AR]", "RECRI003C")),
    FAIL_RIR("FAIL_RIR", List.of("CON080", "CON020[DOC:7ZIP;PAGES:3]", "RECRI001", "RECRI002", "RECRI004A", "RECRI004B[DOC:Plico]", "RECRI004C"));

    private String name;
    private List<String> events;

    private static final List<String> FEEDBACK_EVENTS = Arrays.asList(
            "RECRN001C", "RECRN002C", "RECRN003C", "RECRN004C", "RECRN005C",
            "RECAG001C", "RECAG002C", "RECAG003C", "RECAG005C", "RECAG006C", "RECAG007C",
            "RECRN002F", "RECAG003F",
            "PNAG012", "RECAG012", "PNRN012",
            "RECRI003C", "RECRI004C");

    private Sequence(String name, List<String> events) {
        this.name = name;
        this.events = events;
    }

    public static Sequence getByName(String sequenceName) {
        return Arrays.stream(Sequence.values()).filter(s -> s.name.equals(sequenceName)).findFirst().orElse(null);
    }

    public static boolean isFeedback(String eventCode) {
        return FEEDBACK_EVENTS.contains(eventCode);
    }
}