package it.pagopa.interop.config.springconfig;


import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.authorization.domain.dpop.AgidJwtProperties;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.common.interceptor.dpop.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@Configuration
@EnableRetry
public class InteropRestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate customRestTemplate(M2MVersionsMapper mapperV2) {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(990_000);
        requestFactory.setReadTimeout(990_000);
        requestFactory.setConnectionRequestTimeout(990_000);
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new RequestResponseLoggingInterceptor());

        FileHttpMessageConverter fileMessageConverter = new FileHttpMessageConverter();
        FileDownloadMultipartConverter multipartConverterV2 = new FileDownloadMultipartConverter();
        FileDownloadMultipartConverterV3 multipartConverterV3 = new FileDownloadMultipartConverterV3(
                multipartConverterV2,
                mapperV2);
        restTemplate.getMessageConverters().addAll(of(
                fileMessageConverter,
                multipartConverterV2,
                multipartConverterV3
        ));
        return restTemplate;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DPoPAccessTokenSupplier dpopAccessTokenSupplier(DPoPTokenService tokenService) {
        return new DPoPAccessTokenSupplier(tokenService);
    }

    @Bean
    public AgidJwtProperties agidJwtProperties() {
        return new AgidJwtProperties();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DpopRestTemplate dpopRestTemplate(
            DPoPTokenService dpoPTokenService,
            DPoPAccessTokenSupplier dpopAccessTokenSupplier,
            RestTemplate customRestTemplate, // prende il @Primary
            AgidJwtProperties agidJwtProperties
    ) {
        // RequestFactory "raw" per evitare chain annidata
        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
        rf.setConnectTimeout(990_000);
        rf.setReadTimeout(990_000);
        rf.setConnectionRequestTimeout(990_000);
        rf.setBufferRequestBody(false);

        RestTemplate rt = new RestTemplate(rf);

        // copia i converter (file + multipart ecc)
        rt.setMessageConverters(customRestTemplate.getMessageConverters());
        rt.setErrorHandler(customRestTemplate.getErrorHandler());
        rt.setUriTemplateHandler(customRestTemplate.getUriTemplateHandler());

        // SOLO il logging interceptor dal base:
        List<ClientHttpRequestInterceptor> base = customRestTemplate.getInterceptors().stream()
                .filter(i -> i.getClass().getName().contains("RequestResponseLoggingInterceptor"))
                .toList();

        // initial keyPair null: verrà settata da setAuth()
        return new DpopRestTemplate(
                rt,
                dpoPTokenService,
                dpopAccessTokenSupplier,
                new ArrayList<>(base),
                null,
                agidJwtProperties
        );
    }

    public static class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";

        private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class.getName());

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // Logs HTTP request
            logRequest(request, body);
            // Esegui la richiesta
            return logResponse(request, body, execution);
        }

        private void logRequest(HttpRequest request, byte[] body) {
            logger.info("Request Method: {}", request.getMethod());
            logger.info("Request URI: {}", request.getURI());
            // Logs header request
            request.getHeaders().forEach((key, value) -> logger.info("Request Header: {} = {}", key, value));
            // Logs request body
            if (body.length > 0) {
                logger.info("Request Body: {}", new String(body));
            }
        }

        private ClientHttpResponse logResponse(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            try (ClientHttpResponse response = execution.execute(request, body)) {

                logger.info("Response Status Code: {}", response.getStatusCode());
                logger.info("Response Status Text: {}", response.getStatusText());

                // Logs header response
                response.getHeaders().forEach((key, value) -> logger.info("Response Header: {} = {}", key, value));

                InputStream inputStream = response.getBody();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }

                String responseBody = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
                if (StringUtils.isNotBlank(responseBody)) logger.info("Response Body: {}", new String(responseBody));

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
                            logger.error("Error while closing the response body input stream", e);
                        }
                    }
                };
            }
        }
    }
}
