package it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain;

/**
 * Seconda lingua dell'email di cortesia bilingue. Testi presi dai template di pn-templates-engine
 * (NotificationAARFor_Subject e NotificationAARForEMAILDigital/Analog): nel bilingue il testo nella seconda lingua
 * precede quello italiano, separato da " • ".
 */
public enum CourtesyEmailLanguage {
    DE("Neue rechtsgültige Zustellung von", "Hallo", "Auf Dokumente über SEND zugreifen"),
    FR("Nouvelle notification à valeur juridique de", "Bonjour", "Accéder aux documents sur SEND"),
    SL("Novo pravno veljavno obvestilo od", "Pozdravljeni", "Dostop do dokumentov na SEND");

    public static final String ITALIAN_SUBJECT = "Nuova notifica a valore legale da";
    public static final String ITALIAN_HELLO = "Ciao";
    public static final String ITALIAN_BUTTON = "Accedi ai documenti su SEND";
    public static final String SEPARATOR = " • ";

    private final String subject;
    private final String hello;
    private final String button;

    CourtesyEmailLanguage(String subject, String hello, String button) {
        this.subject = subject;
        this.hello = hello;
        this.button = button;
    }

    public String subject() {
        return subject;
    }

    public String hello() {
        return hello;
    }

    public String button() {
        return button;
    }
}
