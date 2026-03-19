package it.pagopa.pn.client.b2b.pa.config.springconfig;


import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
public class RestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

    @Bean(destroyMethod = "shutdown")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager pooling = new PoolingHttpClientConnectionManager();
        pooling.setMaxTotal(500);
        pooling.setDefaultMaxPerRoute(500);
        return pooling;
    }

    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        return (exception, executionCount, context) -> {
            if (executionCount > 10) {
                return false;
            }
            if (exception instanceof ConnectionPoolTimeoutException) {
                long backoffTime = (long) Math.pow(2, executionCount) * 1000;
                try {
                    Thread.sleep(backoffTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return true;
            }
            return false;
        };
    }

    @Bean
    public CloseableHttpClient httpClient(
            PoolingHttpClientConnectionManager pooling,
            HttpRequestRetryHandler retryHandler) {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(60000)
                .setConnectTimeout(10000)
                .setSocketTimeout(60000)
                .build();

        return HttpClients.custom()
                .setConnectionManager(pooling)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                // CRUCIALE: Libera RAM chiudendo connessioni inattive e gestendo il pool in esclusiva
                .setConnectionManagerShared(false)
                .evictIdleConnections(10, TimeUnit.SECONDS)
                .build();
    }

    @Bean(name = "customRestTemplate")
    @Primary
    public RestTemplate customRestTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestAndTraceIdInterceptor()));

        return restTemplate;
    }

    public static class RequestAndTraceIdInterceptor implements ClientHttpRequestInterceptor {
        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";
        private static final Logger log = LoggerFactory.getLogger(RequestAndTraceIdInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            long startTime = System.currentTimeMillis();
            ClientHttpResponse response = null;
            try {
                response = execution.execute(request, body);
                return response;
            } finally {
                doLog(request, response, System.currentTimeMillis() - startTime);
            }
        }

        private void doLog(HttpRequest request, ClientHttpResponse response, long duration) {
            String scenarioName = MDC.get(CUCUMBER_SCENARIO_NAME_MDC_ENTRY);
            String traceId = "N/A";
            String statusCode = "UNKNOWN/ERROR";

            if (response != null) {
                try {
                    statusCode = response.getStatusCode().toString();
                    List<String> traceIds = response.getHeaders().get(TRACE_ID_RESPONSE_HEADER_NAME);
                    if (traceIds != null && !traceIds.isEmpty()) {
                        traceId = traceIds.get(0);
                    }
                } catch (Exception e) {
                    statusCode = "CONNECTION_LOST";
                }
            }

            log.info("HTTP {} | Status: {} | Time: {}ms | TraceId: {} | URL: {} | Scenario: {}",
                    request.getMethod(), statusCode, duration, traceId, request.getURI(), scenarioName);
        }
    }
}
