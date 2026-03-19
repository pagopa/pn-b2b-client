package it.pagopa.pn.client.b2b.pa.config.springconfig;


import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class RestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

        @Bean
        public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
            PoolingHttpClientConnectionManager pooling = new PoolingHttpClientConnectionManager();
            pooling.setMaxTotal(500);
            pooling.setDefaultMaxPerRoute(100);
            return pooling;
        }

        @Bean
        public HttpRequestRetryHandler httpRequestRetryHandler() {
            return (exception, executionCount, context) -> {
                if (exception instanceof ConnectionPoolTimeoutException) {
                    return false;
                }

                if (executionCount > 2) {
                    return false;
                }

                if (exception instanceof NoHttpResponseException ||
                        exception instanceof ConnectTimeoutException) {

                    try {
                        Thread.sleep(200L * executionCount);
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
                    .setConnectionRequestTimeout(10000)
                    .setConnectTimeout(5000)
                    .setSocketTimeout(60000)
                    .build();

            return HttpClients.custom()
                    .setConnectionManager(pooling)
                    .setDefaultRequestConfig(requestConfig)
//                    .setRetryHandler(retryHandler)
                    .disableAutomaticRetries()
//                    .evictIdleConnections(30, TimeUnit.SECONDS)
                    .build();
        }

        @Bean(name = "customRestTemplate")
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        @Primary
        public RestTemplate customRestTemplate(CloseableHttpClient httpClient) {

            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);

            RestTemplate restTemplate = new RestTemplate(factory);

            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new RequestAndTraceIdInterceptor());
            restTemplate.setInterceptors(interceptors);

            return restTemplate;
        }


    public static class RequestAndTraceIdInterceptor implements ClientHttpRequestInterceptor {

        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";

        public final Logger log = LoggerFactory.getLogger(RequestAndTraceIdInterceptor.class);

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
            HttpHeaders responseHeaders = response.getHeaders();
            List<String> traceIdHeaderValues = responseHeaders.get(TRACE_ID_RESPONSE_HEADER_NAME);
            return getFirstOrNull(traceIdHeaderValues);
        }

        private String getFirstOrNull(List<String> list) {
            String result;
            if (list != null && !list.isEmpty()) {
                result = list.get(0);
            } else {
                result = null;
            }
            return result;
        }
    }
}
