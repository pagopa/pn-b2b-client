package it.pagopa.interop.common.client;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.operation.IOperation;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@Slf4j
public abstract class AbstractClient {

    @Setter
    protected IHttpExecutor httpCallExecutor;

    public <E, R> Optional<R> performOperation(IOperation<E, R> operation) {
        httpCallExecutor.performCall(operation.getApiCaller());
        return mapIf2xx(operation.getResultExtractor());
    }

    public <R> Optional<R> performOperation(Supplier<ResponseEntity<R>> promise) {
        httpCallExecutor.performCall(promise, ResponseEntity::getStatusCode);
        return mapIf2xx((ResponseEntity<R> re) -> re != null ? re.getBody() : null);
    }

    private <E, R> Optional<R> mapIf2xx(Function<E, R> extractor) {
        HttpStatus status = httpCallExecutor.getResponseStatus();
        if (status == null || !status.is2xxSuccessful()) {
            log.warn("HTTP call failed with status: {}", status != null ? status.value() : "null");
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        E rawResponse = (E) httpCallExecutor.getResponse();

        return Optional.ofNullable(extractor.apply(rawResponse));
    }
}
