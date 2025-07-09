package it.pagopa.interop.common.client;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.operation.IOperation;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Optional;

@Slf4j
public abstract class AbstractClient  {

    @Getter @Setter
    protected IHttpExecutor httpCallExecutor;

    public <E, R> Optional<R> performOperation(IOperation<E, R> operation) {
        // Esegue la chiamata HTTP
        httpCallExecutor.performCall(operation.getApiCaller());

        // Recupera la risposta e l'esito della chiamata (cast se necessario)
        @SuppressWarnings("unchecked")
        E rawResponse = (E) httpCallExecutor.getResponse();
        var response = httpCallExecutor.getClientResponse();

        // Se la risposta è positiva, estrae e restituisce il risultato
        if (response.is2xxSuccessful()) {
            return Optional.ofNullable(operation.getResultExtractor().apply(rawResponse));
        }

        // In caso di errore, loggare o gestire in altro modo se necessario
        log.warn("HTTP call failed with status: {}", response.value());
        return Optional.empty();
    }
}
