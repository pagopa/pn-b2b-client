package it.pagopa.interop.config.springconfig.springconfig;


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
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


@Configuration
public class InteropRestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate customRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(990_000);
        requestFactory.setReadTimeout(990_000);
        requestFactory.setConnectionRequestTimeout(990_000);
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new RequestResponseLoggingInterceptor());

        return restTemplate;
    }

    private static class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";

        private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class.getName());

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // Logs HTTP request
            logRequest(request, body);
            // Performs request and get the response
            ClientHttpResponse response = execution.execute(request, body);
            // Logs HTTP response
            //logResponse(response);
            return response;
        }

        private void logRequest(HttpRequest request, byte[] body) throws IOException {
            logger.info("Request Method: " + request.getMethod());
            logger.info("Request URI: " + request.getURI());
            // Logs header request
            request.getHeaders().forEach((key, value) -> logger.info("Request Header: " + key + " = " + value));
            // Logs request body
            if (body.length > 0) {
                logger.info("Request Body: " + new String(body));
            }
        }

        private void logResponse(ClientHttpResponse response) throws IOException {
            logger.info("Response Status Code: " + response.getStatusCode());
            logger.info("Response Status Text: " + response.getStatusText());
            // Logs header response
            response.getHeaders().forEach((key, value) -> logger.info("Response Header: " + key + " = " + value));
            // Logs response body
            String responseBody = new String(response.getBody().readAllBytes());
            logger.info("Response Body: " + responseBody);
        }
    }

}
