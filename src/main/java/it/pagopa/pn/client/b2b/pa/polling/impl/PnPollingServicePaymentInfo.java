package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentInfoItem;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingPaymentInfo;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponsePaymentInfo;
import it.pagopa.pn.client.b2b.pa.service.IPnPaymentInfoClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey.ApiKeyType;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.PAYMENT_INFO)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServicePaymentInfo extends PnPollingTemplate<PnPollingResponsePaymentInfo> {

    private final IPnPaymentInfoClient paymentInfoClient;
    private final TimingForPolling timingForPolling;
    private List<BffPaymentInfoItem> lastPaymentInfoResponse;
    private Integer lastAmount;
    private long pollingStartedAt;
    private int pollAttempt;
    private Integer atMostMs;
    private Integer pollIntervalMs;

    public PnPollingServicePaymentInfo(IPnPaymentInfoClient paymentInfoClient, TimingForPolling timingForPolling) {
        this.paymentInfoClient = paymentInfoClient;
        this.timingForPolling = timingForPolling;
    }

    @Override
    protected Callable<PnPollingResponsePaymentInfo> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        if (pollingStartedAt == 0L) {
            pollingStartedAt = System.currentTimeMillis();
            pollAttempt = 0;
            TimingForPolling.TimingResult timing = timingForPolling.getTimingForPaymentInfo();
            pollIntervalMs = timing.waiting();
            atMostMs = timing.numCheck() * timing.waiting();
            PnPollingPaymentInfo paymentInfo = pnPollingParameter.getPnPollingPaymentInfo();
            String notices = paymentInfo == null || paymentInfo.getPaymentInfoRequestList() == null
                    ? "null"
                    : paymentInfo.getPaymentInfoRequestList().stream()
                    .map(r -> r.getNoticeCode())
                    .reduce((a, b) -> a + "," + b)
                    .orElse("empty");
            log.info("PAYMENT_INFO poll START atMostMs={}, pollIntervalMs={}, numCheck={}, previousAmount={}, expectedAmount={}, noticeCodes=[{}]",
                    atMostMs,
                    pollIntervalMs,
                    timing.numCheck(),
                    paymentInfo == null ? null : paymentInfo.getPreviousAmount(),
                    paymentInfo == null ? null : paymentInfo.getExpectedAmount(),
                    notices);
        }
        return () -> {
            pollAttempt++;
            PnPollingPaymentInfo paymentInfo = Objects.requireNonNull(
                    pnPollingParameter.getPnPollingPaymentInfo(),
                    "pnPollingPaymentInfo is required for PAYMENT_INFO polling");
            PnPollingResponsePaymentInfo response = new PnPollingResponsePaymentInfo();
            lastPaymentInfoResponse = paymentInfoClient.getPaymentInfoV21(paymentInfo.getPaymentInfoRequestList());
            response.setPaymentInfoResponse(lastPaymentInfoResponse);
            if (lastPaymentInfoResponse != null && !lastPaymentInfoResponse.isEmpty()) {
                lastAmount = lastPaymentInfoResponse.get(0).getAmount();
                response.setAmount(lastAmount);
            }
            long elapsedMs = System.currentTimeMillis() - pollingStartedAt;
            String notice = lastPaymentInfoResponse == null || lastPaymentInfoResponse.isEmpty()
                    ? "null"
                    : lastPaymentInfoResponse.get(0).getNoticeCode();
            log.info("PAYMENT_INFO poll attempt={} elapsedMs={}/{} noticeCode={} amount={} previousAmount={} expectedAmount={}",
                    pollAttempt,
                    elapsedMs,
                    atMostMs,
                    notice,
                    lastAmount,
                    paymentInfo.getPreviousAmount(),
                    paymentInfo.getExpectedAmount());
            return response;
        };
    }

    @Override
    protected Predicate<PnPollingResponsePaymentInfo> checkCondition(String id, PnPollingParameter pnPollingParameter) {
        return response -> {
            PnPollingPaymentInfo paymentInfo = pnPollingParameter.getPnPollingPaymentInfo();
            Integer previousAmount = paymentInfo.getPreviousAmount();
            Integer expectedAmount = paymentInfo.getExpectedAmount();
            Integer currentAmount = response.getAmount();
            long elapsedMs = System.currentTimeMillis() - pollingStartedAt;

            if (expectedAmount != null) {
                if (Objects.equals(expectedAmount, currentAmount)) {
                    log.info("GPD amount matched expected: elapsedMs={}, amount={}", elapsedMs, currentAmount);
                    response.setResult(true);
                    return true;
                }
                response.setResult(false);
                return false;
            }

            if (!Objects.equals(previousAmount, currentAmount)) {
                log.info("GPD amount changed: elapsedMs={}, {} -> {}", elapsedMs, previousAmount, currentAmount);
                response.setResult(true);
                return true;
            }
            response.setResult(false);
            return false;
        };
    }

    @Override
    protected PnPollingResponsePaymentInfo getException(Exception exception) {
        long elapsedMs = pollingStartedAt == 0L ? 0L : System.currentTimeMillis() - pollingStartedAt;
        log.warn("PAYMENT_INFO poll TIMEOUT/EXCEPTION elapsedMs={}/{} attempts={} lastAmount={} lastResponse={} cause={}",
                elapsedMs,
                atMostMs,
                pollAttempt,
                lastAmount,
                lastPaymentInfoResponse,
                exception.toString());
        PnPollingResponsePaymentInfo response = new PnPollingResponsePaymentInfo();
        response.setPaymentInfoResponse(lastPaymentInfoResponse);
        response.setAmount(lastAmount);
        response.setResult(false);
        return response;
    }

    @Override
    protected Integer getPollInterval(String value) {
        return timingForPolling.getTimingForPaymentInfo().waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForPaymentInfo();
        return timingResult.numCheck() * timingResult.waiting();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return false;
    }

    @Override
    public void setApiKey(String apiKey) {
        // payment-info client uses configured bearer token
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return null;
    }
}
