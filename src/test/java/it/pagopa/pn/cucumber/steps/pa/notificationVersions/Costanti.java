package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Costanti {

    // Nomi Utenti
    public static final String MARIO_GHERKIN = "Mario Gherkin";
    public static final String MARIO_CUCUMBER = "Mario Cucumber";
    public static final String GHERKIN_SPA = "GherkinSpa";
    public static final String CUCUMBER_SPA = "CucumberSpa";
    public static final String GHERKIN_SRL = "GherkinSrl";
    public static final String CUCUMBER_SRL = "CucumberSrl";
    public static final String GHERKIN_ANALOGIC = "Gherkin Analogic";
    public static final String CUCUMBER_ANALOGIC = "Cucumber Analogic";
    public static final String GHERKIN_IRREPERIBILE = "Gherkin Irreperibile";
    public static final String CUCUMBER_SOCIETY = "Cucumber Society";
    public static final String CRISTOFORO_COLOMBO = "Cristoforo Colombo";
    public static final String ETTORE_FIERAMOSCA = "Ettore Fieramosca";
    public static final String GALILEO_GALILEI = "Galileo Galilei";
    public static final String LEONARDO_DA_VINCI = "Leonardo da Vinci";
    public static final String DINO_SAURO = "Dino Sauro";
    public static final String LUCIO_ANNEO_SENECA = "Lucio Anneo Seneca";
    public static final String SIGNOR_CASUALE = "Signor Casuale";
    public static final String SIGNOR_GENERATO = "Signor Generato";
    public static final String ALDA_MERINI = "Alda Merini";
    public static final String MARIO_CREDENZIALI_SCADUTE = "Mario Credenziali Scadute";
    public static final String NESSUNO = "nessuno";
    // PA
    public static final String COMUNE_1 = "Comune_1";
    public static final String COMUNE_2 = "Comune_2";
    public static final String COMUNE_MULTI = "Comune_Multi";
    public static final String COMUNE_SON = "Comune_Son";
    public static final String COMUNE_ROOT = "Comune_Root";
    public static final String DEFAULT_PA = COMUNE_1;

    //TODO MATTEO: capire come riuscire a prendere i valori annotati con value
    // (non so perchè me li dà null quando commento il valore e scommento il @Value)

    @Value("${pn.external.utilized.pec:testpagopa3@pec.pagopa.it}")
    public static String DIGITAL_ADDRESS;
    public static final String DEFAULT_DIGITAL_ADDRESS = "testpagopa3@pec.pagopa.it";
    //    @Value("${pn.bearer-token.user1.taxID}")
    public static String MARIO_CUCUMBER_TAX_ID = "FRMTTR76M06B715E";
    //    @Value("${pn.bearer-token.user2.taxID}")
    public static String MARIO_GHERKIN_TAX_ID = "CLMCST42R12D969Z";
    //    @Value("${pn.bearer-token.user4.taxID}")

    // Tax ID
    public static final String CUCUMBER_SRL_TAX_ID = "20517490320";
    public static final String GHERKIN_SRL_TAX_ID = "12666810299";
    public static final String CUCUMBER_SPA_TAX_ID = "20517490320";
    public static final String GHERKIN_SPA_TAX_ID = "12666810299";
    public static final String CUCUMBER_ANALOGIC_TAX_ID = "LBPHLS94A56C826R";
    public static final String GHERKIN_ANALOGIC_TAX_ID = "05722930657";
    public static final String CUCUMBER_SOCIETY_TAX_ID = "20517490320";
    public static final String GHERKIN_IRREPERIBILE_TAX_ID = "00749900049";
    public static final String LEONARDO_DA_VINCI_TAX_ID = "DVNLRD52D15M059P";
    public static final String GALILEO_GALILEI_TAX_ID = "GLLGLL64B15G702I";
    public static final String COMUNE_1_TAX_ID = "01199250158";
    public static final String COMUNE_2_TAX_ID = "00215150236";
    public static final String COMUNE_MULTI_TAX_ID = "80016350821";
    public static final String COMUNE_SON_TAX_ID = "03509990788";
    public static final String COMUNE_ROOT_TAX_ID = "03509990788";
    // TimelineElementCategory
    public static final String AAR_GENERATION = "AAR_GENERATION";
    public static final String ANALOG_FAILURE_WORKFLOW = "ANALOG_FAILURE_WORKFLOW";
    public static final String ANALOG_SUCCESS_WORKFLOW = "ANALOG_SUCCESS_WORKFLOW";
    public static final String ANALOG_WORKFLOW_RECIPIENT_DECEASED = "ANALOG_WORKFLOW_RECIPIENT_DECEASED";
    public static final String COMPLETELY_UNREACHABLE = "COMPLETELY_UNREACHABLE";
    public static final String DIGITAL_DELIVERY_CREATION_REQUEST = "DIGITAL_DELIVERY_CREATION_REQUEST";
    public static final String DIGITAL_FAILURE_WORKFLOW = "DIGITAL_FAILURE_WORKFLOW";
    public static final String DIGITAL_SUCCESS_WORKFLOW = "DIGITAL_SUCCESS_WORKFLOW";
    public static final String GET_ADDRESS = "GET_ADDRESS";
    public static final String NOTIFICATION_VIEWED = "NOTIFICATION_VIEWED";
    public static final String PREPARE_ANALOG_DOMICILE = "PREPARE_ANALOG_DOMICILE";
    public static final String PREPARE_SIMPLE_REGISTERED_LETTER = "PREPARE_SIMPLE_REGISTERED_LETTER";
    public static final String REFINEMENT = "REFINEMENT";
    public static final String REQUEST_ACCEPTED = "REQUEST_ACCEPTED";
    public static final String REQUEST_REFUSED = "REQUEST_REFUSED";
    public static final String SCHEDULE_ANALOG_WORKFLOW = "SCHEDULE_ANALOG_WORKFLOW";
    public static final String SCHEDULE_REFINEMENT = "SCHEDULE_REFINEMENT";
    public static final String SEND_ANALOG_DOMICILE = "SEND_ANALOG_DOMICILE";
    public static final String SEND_ANALOG_FEEDBACK = "SEND_ANALOG_FEEDBACK";
    public static final String SEND_ANALOG_PROGRESS = "SEND_ANALOG_PROGRESS";
    public static final String SEND_COURTESY_MESSAGE = "SEND_COURTESY_MESSAGE";
    public static final String SEND_DIGITAL_DOMICILE = "SEND_DIGITAL_DOMICILE";
    public static final String SEND_DIGITAL_FEEDBACK = "SEND_DIGITAL_FEEDBACK";
    public static final String SEND_SIMPLE_REGISTERED_LETTER = "SEND_SIMPLE_REGISTERED_LETTER";
    public static final String SEND_SIMPLE_REGISTERED_LETTER_PROGRESS = "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS";
    // PollingStrategy (generiche)
    public static final String TIMELINE_RAPID = "TIMELINE_RAPID";
    public static final String TIMELINE_SLOW = "TIMELINE_SLOW";
    public static final String STATUS_RAPID = "STATUS_RAPID";
    public static final String STATUS_SLOW = "STATUS_SLOW";
    public static final String TIMELINE_SLOW_E2E = "TIMELINE_SLOW_E2E";
    public static final String TIMELINE_EXTRA_RAPID = "TIMELINE_EXTRA_RAPID";
    public static final String STATUS_EXTRA_RAPID = "STATUS_EXTRA_RAPID";
    public static final String VALIDATION_STATUS = "VALIDATION_STATUS";
    public static final String VALIDATION_STATUS_ACCEPTATION_SHORT = "VALIDATION_STATUS_ACCEPTATION_SHORT";
    public static final String VALIDATION_STATUS_EXTRA_RAPID = "VALIDATION_STATUS_EXTRA_RAPID";
    public static final String VALIDATION_STATUS_NO_ACCEPTATION = "VALIDATION_STATUS_NO_ACCEPTATION";
    public static final String WEBHOOK = "WEBHOOK";
    // Workflow wait (milliseconds)
    public static final Integer WAIT_EXTRA_RAPID = 500;
    public static final Integer WORKFLOW_WAIT_DEFAULT = 31000;
    public static final Integer SCHEDULING_DELTA_DEFAULT = 500;
    public static final Integer WAIT_DEFAULT = 10000;
    public static final Integer WORKFLOW_WAIT_UPPER_BOUND = 2900;
    public static final Integer WAITING_GPD = 1000;
    public static final Integer WAIT_UPPER_BOUND = 950;
    // Duration
    public static final Duration DURATION_DIGITAL_REFINEMENT_DEFAULT_SUCCESS = DurationStyle.detectAndParse("6m");
    public static final Duration DURATION_DIGITAL_REFINEMENT_DEFAULT_FAILURE = DurationStyle.detectAndParse("6m");
    public static final Duration DURATION_ANALOG_REFINEMENT_DEFAULT_SUCCESS = DurationStyle.detectAndParse("2m");
    public static final Duration DURATION_ANALOG_REFINEMENT_DEFAULT_FAILURE = DurationStyle.detectAndParse("4m");
    public static final Duration DURATION_TIME_TO_ADD_IN_NON_VISIBILITY_TIME_CASE_DEFAULT = DurationStyle.detectAndParse("10m");
    public static final Duration DURATION_SECOND_NOTIFICATION_WORKFLOW_WAITING_TIME_DEFAULT = DurationStyle.detectAndParse("6m");
    public static final Duration DURATION_WAIT_READ_COURTESY_MESSAGE_DEFAULT = DurationStyle.detectAndParse("5m");
    // Notification Status
    public static final String NOTIFICATION_STATUS_ACCEPTED = "ACCEPTED";
    public static final String NOTIFICATION_STATUS_REFUSED = "REFUSED";
    public static final String NOTIFICATION_STATUS_NOT_REFUSED = "NOT_REFUSED";
    // Error causes
    public static final String ALLEGATO = "ALLEGATO";
    public static final String EXTENSION = "EXTENSION";
    public static final String SHA_256 = "SHA_256";
    public static final String TAX_ID = "TAX_ID";
    public static final String ADDRESS = "ADDRESS";
    // Error messages
    public static final String FILE_NOTFOUND = "FILE_NOTFOUND";
    public static final String FILE_SHA_ERROR = "FILE_SHA_ERROR";
    public static final String TAXID_NOT_VALID = "TAXID_NOT_VALID";
    public static final String INVALID_PARAMETER_MAX_ATTACHMENT = "INVALID_PARAMETER_MAX_ATTACHMENT";
    public static final String FILE_PDF_INVALID_ERROR = "FILE_PDF_INVALID_ERROR";
    public static final String NOT_VALID_ADDRESS = "NOT_VALID_ADDRESS";
    // Tipologie destinatario
    public static final String PF = "PF";
    public static final String PG = "PG";
    // Tipologie indirizzo
    public static final String PEC = "PEC";
    // Versioni
    public static final String MOST_RECENT = "più recente";

    public static String getDigitalAddressValue() {
        if (DIGITAL_ADDRESS == null || DIGITAL_ADDRESS.equalsIgnoreCase("${pn.external.digitalDomicile.address}"))
            return DEFAULT_DIGITAL_ADDRESS;
        return DIGITAL_ADDRESS;
    }

    public static String getSenderTaxIdFromProperties(String paName) {
        return switch (paName) {
            case COMUNE_1 -> COMUNE_1_TAX_ID;
            case COMUNE_2 -> COMUNE_2_TAX_ID;
            case COMUNE_MULTI -> COMUNE_MULTI_TAX_ID;
            case COMUNE_SON -> COMUNE_SON_TAX_ID;
            case COMUNE_ROOT -> COMUNE_ROOT_TAX_ID;
            default -> throw new IllegalArgumentException();
        };
    }
}
