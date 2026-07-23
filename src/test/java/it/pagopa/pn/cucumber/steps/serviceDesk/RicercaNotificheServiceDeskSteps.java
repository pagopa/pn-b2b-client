package it.pagopa.pn.cucumber.steps.serviceDesk;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.common.util.DateUtils;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.NotificationSearchResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.NotificationStatusV26;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchCriteriaMapper;
import it.pagopa.pn.cucumber.utils.notificationsearch.NotificationSearchRowAssertions;
import it.pagopa.pn.cucumber.utils.token.TokenResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step per lo SCENARIO 6 del PST "Ricerca delle notifiche bonarie": ricerca notifiche da parte di
 * servicedesk tramite GET /delivery-private/search, esposta da {@link PnPaB2bInternalInformalClientImpl}.
 * L'API restituisce solo notifiche legali per contratto (nessun campo communicationType sulla riga), quindi
 * non serve un controllo dedicato per escludere le bonarie.
 */
public class RicercaNotificheServiceDeskSteps {

    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;
    private final NotificationSearchCriteriaMapper notificationSearchCriteriaMapper;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;

    private NotificationSearchResponse notificationSearchResponse;
    private HttpStatusCodeException notificationError;
    private OffsetDateTime lastStartDate;
    private OffsetDateTime lastEndDate;
    private String lastRecipientUid;
    private Boolean lastRecipientIdOpaque;
    private String lastSenderId;
    private List<NotificationStatusV26> lastStatus;
    private String lastMandateId;
    private String lastCxType;
    private Integer lastSize;

    @Autowired
    public RicercaNotificheServiceDeskSteps(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl,
                                             NotificationSearchCriteriaMapper notificationSearchCriteriaMapper,
                                             SharedSteps sharedSteps, SendSharedContext sendSharedContext) {
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.notificationSearchCriteriaMapper = notificationSearchCriteriaMapper;
        this.sharedSteps = sharedSteps;
        this.sendSharedContext = sendSharedContext;
    }

    @And("vengono recuperate le notifiche da servicedesk")
    public void vengonoRecuperateLeNotificheDaServicedesk(Map<String, String> data) {
        TokenResolver tokenResolver = new TokenResolver(sharedSteps, sendSharedContext);
        notificationError = null;
        lastStartDate = toStartOfDayUtc(data.get("startDate"));
        lastEndDate = toStartOfDayUtc(data.get("endDate"));
        lastRecipientUid = resolve(tokenResolver, data.get("recipientId"));
        lastRecipientIdOpaque = parseBoolean(data.get("recipientIdOpaque"));
        lastSenderId = resolve(tokenResolver, data.get("senderId"));
        lastStatus = parseStatus(data.get("status"));
        lastMandateId = resolve(tokenResolver, data.get("mandateId"));
        lastCxType = StringUtils.resolveValue(data.get("cxType"));
        lastSize = parseInteger(data.get("size"));
        try {
            notificationSearchResponse = pnPaB2bInternalInformalClientImpl.searchNotificationsPrivate(
                    lastStartDate, lastEndDate, lastRecipientUid, lastRecipientIdOpaque, lastSenderId,
                    lastStatus, lastMandateId, lastCxType, lastSize, StringUtils.resolveValue(data.get("nextPagesKey")));
        } catch (HttpStatusCodeException e) {
            notificationError = e;
        }
    }

    @And("l'elenco delle notifiche recuperate da servicedesk rispettare i seguenti criteri:")
    public void verifyServiceDeskNotificationSearchResponse(Map<String, String> criteria) {
        Map<String, List<String>> resolvedCriteria = notificationSearchCriteriaMapper.build(criteria, new TokenResolver(sharedSteps, sendSharedContext));
        NotificationSearchRowAssertions.assertAllRowsMatchCriteria(notificationSearchResponse.getResultsPage(), resolvedCriteria);
    }

    @And("si sfogliano tutte le pagine della ricerca da servicedesk e si verifica che vengano raccolte almeno {int} notifiche")
    public void sfogliaTutteLePagineServicedesk(int minimumExpectedCount) {
        int totalCollected = notificationSearchResponse.getResultsPage().size();
        while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())
                && notificationSearchResponse.getNextPagesKey() != null && !notificationSearchResponse.getNextPagesKey().isEmpty()) {
            notificationSearchResponse = pnPaB2bInternalInformalClientImpl.searchNotificationsPrivate(
                    lastStartDate, lastEndDate, lastRecipientUid, lastRecipientIdOpaque, lastSenderId,
                    lastStatus, lastMandateId, lastCxType, lastSize, notificationSearchResponse.getNextPagesKey().get(0));
            totalCollected += notificationSearchResponse.getResultsPage().size();
        }
        assertThat(totalCollected)
                .as("Numero totale di notifiche raccolte sfogliando tutte le pagine")
                .isGreaterThanOrEqualTo(minimumExpectedCount);
    }

    @Then("si verifica che la ricerca notifiche da servicedesk abbia prodotto un errore di tipo {string}")
    public void verifyServiceDeskApiErrorType(String errorType) {
        assertThat(notificationError)
                .as("La ricerca notifiche da servicedesk non ha prodotto l'errore atteso")
                .isNotNull();
        assertThat(notificationError.getStatusCode().getReasonPhrase().toUpperCase())
                .as("Il tipo di errore non coincide con quanto atteso")
                .contains(errorType.toUpperCase());
    }

    private static String resolve(TokenResolver tokenResolver, String rawValue) {
        String resolvedValue = StringUtils.resolveValue(rawValue);
        return resolvedValue == null ? null : tokenResolver.resolve(resolvedValue);
    }

    private static Boolean parseBoolean(String rawValue) {
        String resolvedValue = StringUtils.resolveValue(rawValue);
        return resolvedValue == null ? null : Boolean.valueOf(resolvedValue);
    }

    private static Integer parseInteger(String rawValue) {
        String resolvedValue = StringUtils.resolveValue(rawValue);
        return resolvedValue == null ? null : Integer.valueOf(resolvedValue);
    }

    private static List<NotificationStatusV26> parseStatus(String rawValue) {
        String resolvedValue = StringUtils.resolveValue(rawValue);
        if (resolvedValue == null) {
            return null;
        }
        return Arrays.stream(resolvedValue.split(","))
                .map(String::trim)
                .map(NotificationStatusV26::fromValue)
                .collect(Collectors.toList());
    }

    private static OffsetDateTime toStartOfDayUtc(String rawDate) {
        String resolvedDate = DateUtils.resolveDate(rawDate);
        if (resolvedDate == null) {
            return null;
        }
        return LocalDate.parse(resolvedDate).atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
