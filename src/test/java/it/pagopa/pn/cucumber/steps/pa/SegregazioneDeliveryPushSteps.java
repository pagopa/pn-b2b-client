package it.pagopa.pn.cucumber.steps.pa;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationSearchRow;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class SegregazioneDeliveryPushSteps {

    private final SharedSteps sharedSteps;
    private final IPnWebPaClient webPaClient;
    private final Map<String, String> failedIUN = new HashMap<>();
    private final String deliveryPushBaseUrl;
    private BffNotificationsResponse searchResponse;
    private QueryParamsPojo queryParamsPojo;
    private static final String NEW_TIMELINE_URL = "/delivery-push-private/test/new-impl/timeline/";

    private static final String OLD_TIMELINE_URL = "/delivery-push-private/test/old-impl/timeline/";

    private static final String NEW_STATUS_HISTORY_URL = "/delivery-push-private/test/new-impl/history/";

    private static final String OLD_STATUS_HISTORY_URL = "/delivery-push-private/test/old-impl/history/";


    @Autowired
    public SegregazioneDeliveryPushSteps(
            SharedSteps sharedSteps,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBaseUrl) {
        this.sharedSteps = sharedSteps;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.deliveryPushBaseUrl = deliveryPushBaseUrl;
    }

    @Data
    private static class QueryParamsPojo {
        int notificationSampleSize;
        OffsetDateTime dateStart;
        OffsetDateTime dateEnd;
        String nextPageKey;
    }


    @Given("vengono recuperate dal sistema {int} notifiche inviate tra {string} e {string} da {string}")
    public void recoverMultipleNotifications(int quantity, String dateStart, String dateEnd, String paName) {
        sharedSteps.setPA(paName);

        queryParamsPojo = new QueryParamsPojo();
        queryParamsPojo.notificationSampleSize = quantity;
        queryParamsPojo.dateStart = B2bUtils.convertStringToOffsetDateTime(dateStart);
        queryParamsPojo.dateEnd = B2bUtils.convertStringToOffsetDateTime(dateEnd);

        searchResponse = webPaClient.searchSentNotification(
                queryParamsPojo.dateStart,
                queryParamsPojo.dateEnd,
                null, null, null, null, 50, null);
    }

    @Then("confronto le timeline ottenute chiamando la nuova API e la vecchia impostando confidentialInfoRequired a {string}")
    public void compareTimelineResults(String confidentialInfoRequired) {
        List<String> iunList = new ArrayList<>();
        assertThat(searchResponse).as("Il risultato della ricerca non dev'essere null").isNotNull();
        assertThat(searchResponse.getResultsPage()).asList().as("Il risultato della ricerca non dev'essere vuoto").isNotEmpty();

        int counter = 0;
        while (counter < queryParamsPojo.notificationSampleSize) {
            for (NotificationSearchRow x : searchResponse.getResultsPage()) {
                String iun = x.getIun();
                String queryParams = Boolean.parseBoolean(confidentialInfoRequired) ? "?confidentialInfoRequired=true" : "";
                iunList.add(iun);
                String timelineOld = null;
                String timelineNew = null;
                String oldUrl = "";
                String newUrl = "";
                try {
                    oldUrl = getApiUrl(OLD_TIMELINE_URL, iun, queryParams);
                    newUrl = getApiUrl(NEW_TIMELINE_URL, iun, queryParams);
                    timelineOld = callGetApi(oldUrl);
                    timelineNew = callGetApi(newUrl);
                    JsonElement oldApiJsonElement = JsonParser.parseString(timelineOld);
                    JsonElement newApiJsonElement = JsonParser.parseString(timelineNew);
                    assertThat(oldApiJsonElement).isEqualTo(newApiJsonElement);
                } catch (AssertionError ae) {
                    log.warn("NOT MATCHING DATA FOR IUN : " + iun + " OLD TIMELINE BODY " + timelineOld);
                    log.warn("NOT MATCHING DATA FOR IUN : " + iun + " NEW TIMELINE BODY " + timelineNew);
                    failedIUN.put(iun, oldUrl + "\n" + newUrl);
                } catch (Exception e) {
                    failedIUN.put(iun, "ERROR FOR IUN : " + iun + " " + e.getMessage());
                } finally {
                    counter += 1;
                }
            }
            if (counter < queryParamsPojo.notificationSampleSize && !searchResponse.getNextPagesKey().isEmpty()) {
                String nextPageKey = searchResponse.getNextPagesKey().get(0);
                searchResponse = webPaClient.searchSentNotification(
                        queryParamsPojo.dateStart,
                        queryParamsPojo.dateEnd,
                        null, null, null, null, 50, nextPageKey);
            } else {
                counter = queryParamsPojo.notificationSampleSize;
            }
        }
        for (String iun : iunList) {
            log.info(iun);
        }
        assertThat(failedIUN.entrySet().isEmpty()).as(logAssertionError(true, failedIUN)).isTrue();
    }

    @Then("confronto gli status history ottenuti chiamando la nuova API e la vecchia")
    public void compareStatusHistoryResults() {
        List<String> iunList = new ArrayList<>();
        assertThat(searchResponse).as("Il risultato della ricerca non dev'essere null").isNotNull();
        assertThat(searchResponse.getResultsPage()).asList().as("Il risultato della ricerca non dev'essere vuoto").isNotEmpty();

        int counter = 0;
        while (counter < queryParamsPojo.notificationSampleSize) {
            for (NotificationSearchRow x : searchResponse.getResultsPage()) {
                String iun = x.getIun();
                int recipientNum = x.getRecipients().size();
                OffsetDateTime createdAt = x.getSentAt();
                String queryParams = "?numberOfRecipients=" + recipientNum + "&createdAt=" + createdAt;
                iunList.add(iun);
                String statusHistoryOld = null;
                String statusHistoryNew = null;
                String oldUrl = "";
                String newUrl = "";
                try {
                    oldUrl = getApiUrl(OLD_STATUS_HISTORY_URL, iun, queryParams);
                    newUrl = getApiUrl(NEW_STATUS_HISTORY_URL, iun, queryParams);
                    statusHistoryOld = callGetApi(oldUrl);
                    statusHistoryNew = callGetApi(newUrl);
                    JsonElement oldApiJsonElement = JsonParser.parseString(statusHistoryOld);
                    JsonElement newApiJsonElement = JsonParser.parseString(statusHistoryNew);
                    assertThat(oldApiJsonElement).isEqualTo(newApiJsonElement);
                } catch (AssertionError ae) {
                    log.warn("NOT MATCHING DATA FOR IUN : " + iun + " OLD HISTORY BODY " + statusHistoryOld);
                    log.warn("NOT MATCHING DATA FOR IUN : " + iun + " NEW HISTORY BODY " + statusHistoryNew);
                    failedIUN.put(iun, oldUrl + "\n" + newUrl);
                } catch (Exception e) {
                    failedIUN.put(iun, "ERROR FOR IUN : " + iun + " " + e.getMessage());
                } finally {
                    counter += 1;
                }
            }
            if (counter < queryParamsPojo.notificationSampleSize && !searchResponse.getNextPagesKey().isEmpty()) {
                String nextPageKey = searchResponse.getNextPagesKey().get(0);
                searchResponse = webPaClient.searchSentNotification(
                        queryParamsPojo.dateStart,
                        queryParamsPojo.dateEnd,
                        null, null, null, null, 20, nextPageKey);
            } else {
                counter = queryParamsPojo.notificationSampleSize;
            }
        }
        for (String iun : iunList) {
            log.info(iun);
        }
        assertThat(failedIUN.entrySet().isEmpty()).as(logAssertionError(false, failedIUN)).isTrue();
    }

    private String getApiUrl(String apiUrl, String iun, String queryParams) {
        return deliveryPushBaseUrl + apiUrl + iun + queryParams;
    }

    private String callGetApi(String url) {
        try {
            log.info("Calling url: {}", url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Status Code: {}", response.statusCode());
            return response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error calling " + url + ": " + e.getMessage();
        }
    }

    private String logAssertionError(boolean isTimeline, Map<String, String> errorMap) {
        StringBuilder sb = new StringBuilder();
        String check = isTimeline ? "TIMELINE" : "STATUS HISTORY";
        String intro = "Trovate " + errorMap.entrySet().size() + " discrepanze a livello di " + check + ":\n";
        sb.append(intro);
        errorMap.forEach((key, value) -> {
            sb.append(key);
            sb.append(" ");
            sb.append(value);
            sb.append("\n");
        });
        return sb.toString();
    }
}
