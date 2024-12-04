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
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


@Configuration
public class InteropRestTemplateConfiguration {

    public static final String CUCUMBER_SCENARIO_NAME_MDC_ENTRY = "cucumber_scenario_name";

    @Bean(name = "customRestTemplate")
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
        interceptors.add(new RequestAndTraceIdInterceptor());

        return restTemplate;
    }

    @Bean(name = "defaultRestTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate defaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RequestAndTraceIdInterceptor());

        return restTemplate;
    }

    private static class RequestAndTraceIdInterceptor implements ClientHttpRequestInterceptor {

        public static final String TRACE_ID_RESPONSE_HEADER_NAME = "x-amzn-trace-Id";

        public final Logger log = LoggerFactory.getLogger( RequestAndTraceIdInterceptor.class );

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

            ClientHttpResponse response = execution.execute( request, body );

            doLog( request, response );

            return response;
        }

        private void doLog(HttpRequest request, ClientHttpResponse response) {
            String httpMethod = request.getMethodValue();
            String requestUrl = request.getURI().toString();
            String traceId = getTraceIdFromHttpResponse( response );

            String scenarioName = MDC.get( CUCUMBER_SCENARIO_NAME_MDC_ENTRY );
            log.info("Request TraceId, method, url, scenario: [{}, {}, {}, {}]", traceId, httpMethod, requestUrl, scenarioName);
        }

        private String getTraceIdFromHttpResponse(ClientHttpResponse response) {
            HttpHeaders responseHeaders = response.getHeaders();
            List<String> traceIdHeaderValues = responseHeaders.get(TRACE_ID_RESPONSE_HEADER_NAME);
            return getFirstOrNull( traceIdHeaderValues );
        }

        private String getFirstOrNull( List<String> list ) {
            String result;
            if( list != null && !list.isEmpty() ) {
                result = list.get( 0 );
            }
            else {
                result = null;
            }
            return result;
        }
    }
}
