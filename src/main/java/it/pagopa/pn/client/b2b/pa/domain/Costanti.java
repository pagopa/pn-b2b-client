package it.pagopa.pn.client.b2b.pa.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.List;

@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Costanti {

    // Nomi Utenti
    public static final String MARIO_GHERKIN = "Mario Gherkin";
    public static final String MARIO_CUCUMBER = "Mario Cucumber";
    public static final String GHERKIN_SPA = "GherkinSpa";
    public static final String CUCUMBER_SPA = "CucumberSpa";
    public static final String CUCUMBER_SPA_B2B = "CucumberSpaB2B";
    public static final String GHERKIN_SRL = "GherkinSrl";
    public static final String GHERKIN_SRL_B2B = "GherkinSrlB2B";
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
    public static final String UTENZA_CON_INDIRIZZO_NON_VALIDO = "Utenza con Errore D01";
    public static final String UTENZA_CON_INDIRIZZO_VALIDO_ANPR = "Utenza con Indirizzo Valido da ANPR";
    // PA
    public static final String COMUNE_1 = "Comune_1";
    public static final String COMUNE_2 = "Comune_2";
    public static final String COMUNE_MULTI = "Comune_Multi";
    public static final String COMUNE_SON = "Comune_Son";
    public static final String COMUNE_SON_2 = "Comune_Son_2";
    public static final String COMUNE_ROOT = "Comune_Root";
    public static final String DEFAULT_PA = COMUNE_1;
    // Tipologie destinatario
    public static final String PF = "PF";
    public static final String PG = "PG";
    // Tipologie indirizzo
    public static final String PEC = "PEC";
    // Notification Status
    public static final String NOTIFICATION_STATUS_ACCEPTED = "ACCEPTED";
    public static final String NOTIFICATION_STATUS_REFUSED = "REFUSED";
    public static final String NOTIFICATION_STATUS_CANCELLED = "CANCELLED";
    public static final String NOTIFICATION_STATUS_VIEWED = "VIEWED";
    // Stream Type
    public static final String STREAM_EVENT_TYPE_TIMELINE = "TIMELINE";
    public static final String STREAM_EVENT_TYPE_STATUS = "STATUS";

    public static final String INVALID_IUN = "INVALID-IUN";
    public static final String INEXISTENT_IUN = "TEST-INEX-ISTE-123456-Z-1";

    /**
     * 07/05/2025
     * I seguenti taxID per PG sono ora i soli validi per intraprendere il flusso analogico senza che recuperi indirizzi digitali dai registri nazionali
     * 80048790176 ---> GHERKIN_ANALOGIC
     * 02455090981 ---> GHERKIN_IRREPERIBILE
     * 12666810299 - Vita Nova Sas
     * 27957814470 - Convivio Spa
     * 70412331207 - DivinaCommedia Srl
     * 20517490320 - LuAnSe SpA
     */

    // TimelineElementCategory (V1)
    public static final String SENDER_ACK_CREATION_REQUEST = "SENDER_ACK_CREATION_REQUEST";
    public static final String VALIDATE_NORMALIZE_ADDRESSES_REQUEST = "VALIDATE_NORMALIZE_ADDRESSES_REQUEST";
    public static final String NORMALIZED_ADDRESS = "NORMALIZED_ADDRESS";
    public static final String REQUEST_ACCEPTED = "REQUEST_ACCEPTED";
    public static final String SEND_COURTESY_MESSAGE = "SEND_COURTESY_MESSAGE";
    public static final String GET_ADDRESS = "GET_ADDRESS";
    public static final String PUBLIC_REGISTRY_CALL = "PUBLIC_REGISTRY_CALL";
    public static final String PUBLIC_REGISTRY_RESPONSE = "PUBLIC_REGISTRY_RESPONSE";
    public static final String SCHEDULE_ANALOG_WORKFLOW = "SCHEDULE_ANALOG_WORKFLOW";
    public static final String SCHEDULE_DIGITAL_WORKFLOW = "SCHEDULE_DIGITAL_WORKFLOW";
    public static final String PREPARE_DIGITAL_DOMICILE = "PREPARE_DIGITAL_DOMICILE";
    public static final String SEND_DIGITAL_DOMICILE = "SEND_DIGITAL_DOMICILE";
    public static final String SEND_DIGITAL_PROGRESS = "SEND_DIGITAL_PROGRESS";
    public static final String SEND_DIGITAL_FEEDBACK = "SEND_DIGITAL_FEEDBACK";
    public static final String REFINEMENT = "REFINEMENT";
    public static final String SCHEDULE_REFINEMENT = "SCHEDULE_REFINEMENT";
    public static final String DIGITAL_DELIVERY_CREATION_REQUEST = "DIGITAL_DELIVERY_CREATION_REQUEST";
    public static final String DIGITAL_SUCCESS_WORKFLOW = "DIGITAL_SUCCESS_WORKFLOW";
    public static final String DIGITAL_FAILURE_WORKFLOW = "DIGITAL_FAILURE_WORKFLOW";
    public static final String ANALOG_SUCCESS_WORKFLOW = "ANALOG_SUCCESS_WORKFLOW";
    public static final String ANALOG_FAILURE_WORKFLOW = "ANALOG_FAILURE_WORKFLOW";
    public static final String PREPARE_SIMPLE_REGISTERED_LETTER = "PREPARE_SIMPLE_REGISTERED_LETTER";
    public static final String SEND_SIMPLE_REGISTERED_LETTER = "SEND_SIMPLE_REGISTERED_LETTER";
    public static final String SEND_SIMPLE_REGISTERED_LETTER_PROGRESS = "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS";
    public static final String NOTIFICATION_VIEWED_CREATION_REQUEST = "NOTIFICATION_VIEWED_CREATION_REQUEST";
    public static final String NOTIFICATION_VIEWED = "NOTIFICATION_VIEWED";
    public static final String PREPARE_ANALOG_DOMICILE = "PREPARE_ANALOG_DOMICILE";
    public static final String SEND_ANALOG_DOMICILE = "SEND_ANALOG_DOMICILE";
    public static final String SEND_ANALOG_PROGRESS = "SEND_ANALOG_PROGRESS";
    public static final String SEND_ANALOG_FEEDBACK = "SEND_ANALOG_FEEDBACK";
    public static final String PAYMENT = "PAYMENT";
    public static final String COMPLETELY_UNREACHABLE = "COMPLETELY_UNREACHABLE";
    public static final String COMPLETELY_UNREACHABLE_CREATION_REQUEST = "COMPLETELY_UNREACHABLE_CREATION_REQUEST";
    public static final String REQUEST_REFUSED = "REQUEST_REFUSED";
    public static final String AAR_CREATION_REQUEST = "AAR_CREATION_REQUEST";
    public static final String AAR_GENERATION = "AAR_GENERATION";
    public static final String NOT_HANDLED = "NOT_HANDLED";
    public static final String PROBABLE_SCHEDULING_ANALOG_DATE = "PROBABLE_SCHEDULING_ANALOG_DATE";
    // TimelineElementCategoryV20
    public static final String NOTIFICATION_CANCELLATION_REQUEST = "NOTIFICATION_CANCELLATION_REQUEST";
    public static final String NOTIFICATION_CANCELLED = "NOTIFICATION_CANCELLED";
    public static final String PREPARE_ANALOG_DOMICILE_FAILURE = "PREPARE_ANALOG_DOMICILE_FAILURE";
    // TimelineElementCategoryV23
    public static final String NOTIFICATION_RADD_RETRIEVED = "NOTIFICATION_RADD_RETRIEVED";
    // TimelineElementCategoryV26
    public static final String ANALOG_WORKFLOW_RECIPIENT_DECEASED = "ANALOG_WORKFLOW_RECIPIENT_DECEASED";
    // TimelineElementCategoryV27
    public static final String PUBLIC_REGISTRY_VALIDATION_CALL = "PUBLIC_REGISTRY_VALIDATION_CALL";
    public static final String PUBLIC_REGISTRY_VALIDATION_RESPONSE = "PUBLIC_REGISTRY_VALIDATION_RESPONSE";
    // TimelineElementCategoryV28
    public static final String NOTIFICATION_TIMELINE_REWORKED = "NOTIFICATION_TIMELINE_REWORKED";

    //TimelineEquality errors
    public static final String EQUALITY_DIGITAL_ADDRESS = "digitalAddress";
    public static final String EQUALITY_REC_INDEX = "recIndex";
    public static final String EQUALITY_REFUSAL_REASON_SIZE = "refusalReason_size";
    public static final String EQUALITY_ERROR_CODE = "errorCode";
    public static final String EQUALITY_GENERATED_AAR_URL = "generated_AAR_url";
    public static final String EQUALITY_RESPONSE_STATUS = "responseStatus";
    public static final String EQUALITY_RESPONSE_STATUS_VALUE = "responseStatus_value";
    public static final String EQUALITY_SENDING_RECEIPTS_SIZE = "sendingReceipts_size";
    public static final String EQUALITY_SENDING_RECEIPT_NUMBER = "sendingReceipt_number";
    public static final String EQUALITY_LEGAL_FACTS_IDS = "legalFactsIds";
    public static final String EQUALITY_LEGAL_FACTS_IDS_SIZE = "legalFactsIds_size";
    public static final String EQUALITY_LEGAL_FACT_ID_NUMBER = "legalFactId_number";
    public static final String EQUALITY_CATEGORY = "category";
    public static final String EQUALITY_KEY = "key";
    public static final String EQUALITY_DIGITAL_ADDRESS_SOURCE = "digitalAddressSource";
    public static final String EQUALITY_IS_AVAILABLE = "isAvailable";
    public static final String EQUALITY_DELIVERY_DETAIL_CODE = "deliveryDetailCode";
    public static final String EQUALITY_PHYSICAL_ADDRESS = "physicalAddress";
    public static final String EQUALITY_FAILURE_CAUSES = "failureCauses";
    public static final String EQUALITY_ATTACHMENTS_NULL = "attachments_null";
    public static final String EQUALITY_ATTACHMENTS_SIZE = "attachments_size";
    public static final String EQUALITY_DOCUMENT_TYPE = "documentType";
    public static final String EQUALITY_DELIVERY_FAILURE_CAUSE = "deliveryFailureCause";
    public static final String EQUALITY_ANALOG_COST = "analogCost";
    public static final String EQUALITY_LEGAL_FACT_ID = "legalFactId";
    public static final String EQUALITY_DELEGATE_TAX_ID = "delegate_taxId";
    public static final String EQUALITY_DELEGATE_TYPE = "delegate_type";
    public static final String EQUALITY_DELEGATE_DENOMINATION = "delegate_denomination";
    public static final String EQUALITY_MUNICIPALITY = "municipality";
    public static final String EQUALITY_ADDRESS = "address";
    public static final String EQUALITY_ZIP_CODE = "zipCode";
    public static final String EQUALITY_REGISTRY = "registry";
    public static final String INVALID_TIMELINE_CATEGORY = "Valore non valido per timelineEventCategory: ";
    // Messaggi di errore WebhookSteps
    public static final String NOT_NULL_P_R_E = "Il progressResponseElement non dev'essere null";
    public static final String MUST_NOT_BE_NULL = "non dev'essere null";
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
    // Async Validation Errors
    public static final String WRONG_EXTENSION = "WRONG_EXTENSION";
    public static final String OVERSIZE_ALLEGATO = "OVERSIZE_ALLEGATO";
    public static final String NOTIFICATION_INJECTION_ALLEGATO = "NOTIFICATION_INJECTION_ALLEGATO";
    public static final String OVER_15_ALLEGATO = "OVER_15_ALLEGATO";
    public static final String NOT_FOUND_NO_PRELOAD = "NOT_FOUND_NO_PRELOAD";
    public static final String NOT_FOUND_ON_SAFE_STORAGE = "NOT_FOUND_ON_SAFE_STORAGE";
    public static final String NOT_FOUND_ALLEGATO_JSON = "NOT_FOUND_ALLEGATO_JSON";
    public static final String NOT_EQUAL_SHA = "NOT_EQUAL_SHA";
    public static final String NOT_EQUAL_SHA_JSON = "NOT_EQUAL_SHA_JSON";
    // Sync Validation
    public static final String INVALID_DOCUMENT_KEY_SYNC = "INVALID_DOCUMENT_KEY_SYNC";
    // Error causes
    public static final String ALLEGATO = "ALLEGATO";
    public static final String EXTENSION = "EXTENSION";
    public static final String SHA_256 = "SHA_256";
    public static final String TAX_ID = "TAX_ID";
    public static final String ADDRESS = "ADDRESS";
    // Error messages
    public static final String EQUALITY_CHECK_FAILED = "Equality check failed";
    public static final String FILE_NOTFOUND = "FILE_NOTFOUND";
    public static final String FILE_SHA_ERROR = "FILE_SHA_ERROR";
    public static final String TAXID_NOT_VALID = "TAXID_NOT_VALID";
    public static final String INVALID_PARAMETER_MAX_ATTACHMENT = "INVALID_PARAMETER_MAX_ATTACHMENT";
    public static final String FILE_PDF_INVALID_ERROR = "FILE_PDF_INVALID_ERROR";
    public static final String NOT_VALID_ADDRESS = "NOT_VALID_ADDRESS";
    // LoadTimelineFrom
    public static final String LOAD_FROM_B2B = "fromB2b";
    public static final String LOAD_FROM_DELIVERY_PUSH = "fromDeliveryPush";
    // Regex
    public static final String PHYSICAL_ADDRESS_REGEX = "^[A-Z0-9_.\\-:;@' \\[\\] ]*$";
    // Properties
    public static final String COSTO_BASE_NOTIFICA = "pn.external.costo_base_notifica";
    public static final String TECHNICAL_REFUSAL_COST_MODE = "pn.technical_refusal_cost_mode";
    @Value("${pn.external.utilized.pec:testpagopa3@pec.pagopa.it}")
    public static String DIGITAL_ADDRESS;
    public static final String DEFAULT_DIGITAL_ADDRESS = "testpagopa3@pec.pagopa.it";
    // Versioni
    public static final String MOST_RECENT = "più recente";

    public static final List<String> REWORK_ELEMENTS_BASE_LIST =
            List.of(
                    "SEND_ANALOG_PROGRESS",
                    "SEND_ANALOG_FEEDBACK",
                    "ANALOG_SUCCESS_WORKFLOW",
                    "ANALOG_FAILURE_WORKFLOW",
                    "SCHEDULE_REFINEMENT",
                    "REFINEMENT",
                    "COMPLETELY_UNREACHABLE_CREATION_REQUEST",
                    "COMPLETELY_UNREACHABLE",
                    "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
            );

    public static final List<String> REWORK_ELEMENTS_EXTENDED_LIST =
            List.of(
                    "PREPARE_ANALOG_DOMICILE",
                    "PREPARE_ANALOG_DOMICILE_FAILURE",
                    "SEND_ANALOG_DOMICILE",
                    "SEND_ANALOG_PROGRESS",
                    "SEND_ANALOG_FEEDBACK",
                    "ANALOG_SUCCESS_WORKFLOW",
                    "ANALOG_FAILURE_WORKFLOW",
                    "SCHEDULE_REFINEMENT",
                    "REFINEMENT",
                    "COMPLETELY_UNREACHABLE_CREATION_REQUEST",
                    "COMPLETELY_UNREACHABLE",
                    "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
            );


    public static String getDigitalAddressValue() {
        if (DIGITAL_ADDRESS == null || DIGITAL_ADDRESS.equalsIgnoreCase("${pn.external.digitalDomicile.address}"))
            return DEFAULT_DIGITAL_ADDRESS;
        return DIGITAL_ADDRESS;
    }

}
