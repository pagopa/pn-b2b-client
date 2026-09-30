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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
public class RestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";
    public static final String CUCUMBER_TEST_CASE_ID_MDC_ENTRY = "testCaseId";
    public static final String CUCUMBER_FEATURE_FILE_MDC_ENTRY = "featureFile";

    @Bean(name = "customRestTemplate")
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RestTemplate customRestTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new RequestAndTraceIdInterceptor());
        return restTemplate;
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager pooling = new PoolingHttpClientConnectionManager();
        pooling.setMaxTotal(400);
        pooling.setDefaultMaxPerRoute(50);
        return pooling;
    }

    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        return (exception, executionCount, context) -> {
            if (executionCount > 10) {
                return false;
            }
            if (exception instanceof ConnectionPoolTimeoutException ||
                    exception instanceof org.apache.http.NoHttpResponseException) {
                backoffSleep(executionCount);
                return true;
            }
            return false;
        };
    }

    private void backoffSleep(int executionCount) {
        long backoffTime = (long) Math.pow(2, executionCount) * 1000;
        try {
            Thread.sleep(backoffTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, HttpRequestRetryHandler httpRequestRetryHandler) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(120_000)
                .setConnectTimeout(10_000)
                .setSocketTimeout(20_000)
                .build();
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(httpRequestRetryHandler)
                .evictIdleConnections(1, TimeUnit.MINUTES)
                .evictExpiredConnections()
                .build();
    }

    public static class RequestAndTraceIdInterceptor implements ClientHttpRequestInterceptor {
        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";
        private final Logger log = LoggerFactory.getLogger(RequestAndTraceIdInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            doLog(request, response);
            return response;
        }

        private void doLog(HttpRequest request, ClientHttpResponse response) {
            String httpMethod = request.getMethodValue();
            String requestUrl = request.getURI().toString();
            String traceId = getTraceIdFromHttpResponse(response);

            String scenarioName = MDC.get(CUCUMBER_SCENARIO_NAME_MDC_ENTRY);
            log.info("Request TraceId, method, url, scenario: [{}, {}, {}, {}]", traceId, httpMethod, requestUrl, scenarioName);
        }

        private String getTraceIdFromHttpResponse(ClientHttpResponse response) {
            List<String> traceIdHeaderValues = response.getHeaders().get(TRACE_ID_RESPONSE_HEADER_NAME);
            return (traceIdHeaderValues != null && !traceIdHeaderValues.isEmpty()) ? traceIdHeaderValues.get(0) : null;
        }
    }
}
