package it.pagopa.interop.config.springconfig.springconfig;


import org.apache.commons.lang3.StringUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.*;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
            // Esegui la richiesta
            return logResponse(request, body, execution);
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

        private ClientHttpResponse logResponse(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            logger.info("Response Status Code: " + response.getStatusCode());
            logger.info("Response Status Text: " + response.getStatusText());
            // Logs header response
            response.getHeaders().forEach((key, value) -> logger.info("Response Header: " + key + " = " + value));

            InputStream inputStream = response.getBody();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            String responseBody = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
            if (StringUtils.isNotBlank(responseBody)) logger.info("Response Body: " + new String(responseBody));

            byte[] responseData = byteArrayOutputStream.toByteArray();
            InputStream newInputStream = new ByteArrayInputStream(responseData);
            return new ClientHttpResponse() {
                @Override
                public InputStream getBody() throws IOException {
                    return newInputStream;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return response.getHeaders();
                }

                @Override
                public HttpStatus getStatusCode() throws IOException {
                    return response.getStatusCode();
                }

                @Override
                public int getRawStatusCode() throws IOException {
                    return response.getRawStatusCode();
                }

                @Override
                public String getStatusText() throws IOException {
                    return response.getStatusText();
                }

                @Override
                public void close() {
                    try {
                        newInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }
}
