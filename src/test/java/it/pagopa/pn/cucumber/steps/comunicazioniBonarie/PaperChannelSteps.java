package it.pagopa.pn.cucumber.steps.comunicazioniBonarie;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.AnalogAddress;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalProposalProductTypeEnum;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperChannelClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class PaperChannelSteps {


    private final IPnPaperChannelClient paperChannelClient;

    private InformalPrepareRequest informalPrepareRequest;

    private AnalogAddress analogAddress;

    private HttpStatus httpStatusCode;

    private ResponseEntity<InformalPrepareResponse> informalPrepareResponse;
    private String xClientId;

    private static final String IUN = "ABCD-HILM-YKWX-202202-1";
    private static final String REQUEST_ID = "ABCD-HILM-YKWX-202202-1_rec0_try";
    private static final String RECEIVER_TYPE = "PF";
    private static final String PRINT_TYPE = "FRONTE_RETRO";
    private static final String NOTIFICATION_SENT_ID = "2022-07-27T12:22:33.444Z";
    private static final String PROPOSAL_PRODUCT_TYPE = InformalProposalProductTypeEnum.RS.getValue();

    private static final String ATTACHMENT_URLS = "safestorage://PN_AAR-4219a7d53a0941d0aac2af3be9e7be53.pdf?docTag=AAR,safestorage://PN_AAR-4219a7d53a0941d0aac2af3be9e7be53.pdf?docTag=AAR";

    private static final String FULL_NAME = "Mario Rossi";

    private static final String CITY = "Milano";

    private static final String ADDRESS = "Via Roma";

    private static final String X_CLIENT_ID = "pn-test";

    private static final String NULL_VALUE = "[NULL_VALUE]";

    private static String REQUIRED_ID_200_201;

    public PaperChannelSteps(IPnPaperChannelClient paperChannelClient) {
        this.paperChannelClient = paperChannelClient;
    }

    @Given("inizializzata una comunicazione bonaria con valori di default")
    public void newPaperChannelInformalRequestDefault() {
        newPaperChannelInformalRequest(DataTable.emptyDataTable());
    }

    @Given("inizializzata una comunicazione bonaria con i parametri:")
    public void newPaperChannelInformalRequest(DataTable dataTable) {
        Map<String, String> data;
        if (dataTable.isEmpty()) data = Map.of();
        else data = dataTable.asMaps().get(0);

        // Recupera il valore dalla mappa usando la CHIAVE (stringa o costante)
        String ppt = getFieldValue(data,"proposalProductType", PROPOSAL_PRODUCT_TYPE);
        String pt = getFieldValue(data,"printType", PRINT_TYPE);
        String urlsStr = getFieldValue(data,"attachmentUrls", ATTACHMENT_URLS);

        this.xClientId = getFieldValue(data,"xClientId", X_CLIENT_ID);

        analogAddress = new AnalogAddress()
                .fullname(getFieldValue(data,"fullname", FULL_NAME))
                .city(getFieldValue(data,"city", CITY))
                .address(getFieldValue(data,"address", ADDRESS));

        informalPrepareRequest = new InformalPrepareRequest()
                .iun(getFieldValue(data,"iun", IUN))
                .requestId(getFieldValue(data,"requestId", REQUEST_ID + getRandomId()))
                .receiverType(getFieldValue(data,"receiverType", RECEIVER_TYPE))
                .printType(fromPreparePTString(pt))
                .notificationSentAt(getFieldValue(data,"notificationSentAt", NOTIFICATION_SENT_ID))

                .attachmentUrls(
                        ("".equals(urlsStr) ?  new ArrayList<>() : (urlsStr != null ? new ArrayList<>(Arrays.asList(urlsStr.split(","))) : null)))
                .receiverAddress(analogAddress)
                .proposalProductType(fromPPTString(ppt));

        //ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        //String json = ow.writeValueAsString(informalPrepareRequest);
        //System.out.println(json);

    }

    @Given("inizializzata una comunicazione bonaria con parametro required mancante:")
    public void newPaperChannelInformalRequestRequiredMissing(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        String pt = getFieldValue(data,"printType", PRINT_TYPE);

        informalPrepareRequest = new InformalPrepareRequest()
                .iun(data.get("iun"))
                .requestId(data.get("requestId"))
                .receiverType(data.get("receiverType"))
                .printType(fromPreparePTString(pt))
                //.attachmentUrls(null)
                .proposalProductType(InformalProposalProductTypeEnum.RS);
    }

    @When("si richiede la prepare della comunicazione bonaria")
    public void callPaperChannelInformal() {
        try {
            this.informalPrepareResponse = paperChannelClient.sendInformalPrepareRequest(informalPrepareRequest, this.xClientId);
        } catch (HttpStatusCodeException ex) {
            httpStatusCode = ex.getStatusCode();
        }
    }

    @Then("si riceve un errore con codice di stato {int}")
    public void verifyResponseCode(int expectedCode) {
        // Verifichiamo che lo stato salvato non sia null e corrisponda a quello atteso
        assertNotNull("Il codice di stato non è stato valorizzato (nessun errore HTTP intercettato)", httpStatusCode);
        assertEquals(expectedCode, httpStatusCode.value());

        //assertNotNull("Mi aspettavo un errore di comunicazione", encounteredException);
        //assertTrue(encounteredException instanceof RestClientException);
    }

    @Then("si riceve una response con codice di stato {int}")
    public void verificaStatoSuccesso(int expectedCode) {

        // Verifica che l'oggetto risposta sia popolato
        assertNotNull("La risposta InformalPrepareResponse è nulla", this.informalPrepareResponse);
        assertNotNull("Il body della risposta è nullo", this.informalPrepareResponse.getBody());
        assertNotNull("Il requestId è nullo", this.informalPrepareResponse.getBody().getRequestId());

        // Verifica che lo stato sia 200 o 201
        assertEquals(expectedCode, this.informalPrepareResponse.getStatusCode().value());
    }


    private String getFieldValue(Map<String, String> data, String field, String defVal) {
        // 1. Recupera il valore o usa il default
        String rawValue = data.getOrDefault(field, defVal);

        // 2. Gestisci il caso in cui il valore sia fisicamente null (es. colonna mancante)
        if (rawValue == null) {
            return null;
        }

        if("attachmentUrls".equals(field)) {
            rawValue = rawValue.replaceAll("\\[EMPTY\\]", "");
            rawValue = rawValue.replaceAll("\\[SOLO_SPAZI\\]", "   ");
        }

        if("requestId".equals(field) && rawValue.contains("[REQUEST_ID]")) {
            if(REQUIRED_ID_200_201 == null) {
                REQUIRED_ID_200_201 = REQUEST_ID + getRandomId();
            }
            rawValue = REQUIRED_ID_200_201;
        }

        // 3. Risolvi i placeholder tramite lo switch
        return switch (rawValue) {
            case "[NULL]" -> {
                System.out.println("L'input è interpretato come null");
                yield null;
            }
            case "[EMPTY]" -> "";
            case "[SOLO_SPAZI]" -> "   ";
            default -> rawValue; // Ritorna il testo originale
        };
    }

    private int getRandomId() {
        Random random = new Random();

        int limiteMassimo = 10000000; // L'indice sarà compreso tra 0 e 10m
        return random.nextInt(limiteMassimo);
    }

    private static InformalProposalProductTypeEnum fromPPTString(String value) {
        for (InformalProposalProductTypeEnum type : InformalProposalProductTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null; // Oppure un valore di default come UNKNOWN
    }

    private static InformalPrepareRequest.PrintTypeEnum fromPreparePTString(String value) {
        for (InformalPrepareRequest.PrintTypeEnum type : InformalPrepareRequest.PrintTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null; // Oppure un valore di default come UNKNOWN
    }
}
