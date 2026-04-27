package it.pagopa.pn.cucumber.steps.delayer.service;

import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClientV2;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerCountersPrintItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DelayerSevice {
    private final DelayerLambdaClientV2 delayerLambdaClient;

    public DelayerCountersPrintItem getPrintCapacityCounter(String deliveryDate) {
        try {

            var counters =  delayerLambdaClient.getCountersPrint(deliveryDate);
            return counters.getItems().get(0);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante GET_PRINT_CAPACITY_COUNTER per deliveryDate %s"
                            .formatted(deliveryDate),
                    e
            );
        }
    }
}
