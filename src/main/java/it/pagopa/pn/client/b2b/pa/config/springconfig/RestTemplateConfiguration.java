package it.pagopa.pn.client.b2b.pa.config.springconfig;

import org.apache.http.client.config.RequestConfig;
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
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

    // =========================
    // CONNECTION MANAGER
    // =========================
    @Bean(destroyMethod = "shutdown")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager pooling = new PoolingHttpClientConnectionManager();
        pooling.setMaxTotal(200);
        pooling.setDefaultMaxPerRoute(100);
        pooling.setValidateAfterInactivity(5000);
        return pooling;
    }

    // =========================
    // HTTP CLIENT
    // =========================
    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager pooling) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .build();

        return HttpClients.custom()
                .setConnectionManager(pooling)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler((exception, executionCount, context) -> false)
                .setConnectionManagerShared(false)
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .evictExpiredConnections()
                .build();
    }

    // =========================
    // REST TEMPLATE
    // =========================
    @Bean(name = "customRestTemplate")
    @Primary
    public RestTemplate customRestTemplate(CloseableHttpClient httpClient) {

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        factory.setBufferRequestBody(false); // ✅ evita uso RAM inutile

        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.setInterceptors(
                Collections.singletonList(new RequestAndTraceIdInterceptor())
        );

        return restTemplate;
    }

    // =========================
    // INTERCEPTOR
    // =========================
    public static class RequestAndTraceIdInterceptor implements ClientHttpRequestInterceptor {

        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";
        private static final Logger log = LoggerFactory.getLogger(RequestAndTraceIdInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

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
                    request.getMethod(),
                    statusCode,
                    duration,
                    traceId,
                    request.getURI(),
                    scenarioName
            );
        }
    }
}