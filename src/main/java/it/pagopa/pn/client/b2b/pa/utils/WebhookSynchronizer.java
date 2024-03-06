package it.pagopa.pn.client.b2b.pa.utils;


import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


@Slf4j
//TODO: EXPERIMENTAL
public enum WebhookSynchronizer {
    WEBHOOKSYNCHRONIZER;

    private static final int MAX_NUMBER_OF_STREAM = 10;
    private static final Semaphore semaphoreForPa1 = new Semaphore(MAX_NUMBER_OF_STREAM,true);
    private static final Semaphore semaphoreForPa2 = new Semaphore(MAX_NUMBER_OF_STREAM,true);
    private static final Semaphore semaphoreForPa3 = new Semaphore(MAX_NUMBER_OF_STREAM,true);


    @Synchronized
    public Boolean acquireStreamCreationSlot(int numberOfStreamRequest,String pa) throws InterruptedException {
        if(numberOfStreamRequest > MAX_NUMBER_OF_STREAM)throw new IllegalArgumentException();
        Semaphore semaphoreToAcquire = switch(pa){
            case "Comune_1" -> semaphoreForPa1;
            case "Comune_2" -> semaphoreForPa2;
            case "Comune_Multi" -> semaphoreForPa3;
            default -> throw new IllegalArgumentException();
        };
        return semaphoreToAcquire.tryAcquire(numberOfStreamRequest,90, TimeUnit.MINUTES);
    }

    public void releaseStreamCreationSlot(int numberOfStreamRequest, String pa) {
        Semaphore semaphoreToRelease = switch(pa){
            case "Comune_1" -> semaphoreForPa1;
            case "Comune_2" -> semaphoreForPa2;
            case "Comune_Multi" -> semaphoreForPa3;
            default -> throw new IllegalArgumentException();
        };
        semaphoreToRelease.release(numberOfStreamRequest);
    }


}
