package it.pagopa.pn.client.b2b.pa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SqsService {
    private static final int MAX_MESSAGES_PER_RECEIVE = 10;
    private static final int LONG_POLLING_WAIT_SECONDS = 1;
    private static final int MAX_RECEIVE_CALLS = 2000;

    private final SqsClient sqsClient;

    /**
     * Legge tutti i messaggi visibili della coda senza consumarli. Ogni lettura nasconde i messaggi restituiti per
     * visibilityTimeoutSeconds, così le letture successive restituiscono quelli non ancora letti; la scansione finisce
     * alla prima lettura vuota (con long polling, che interroga tutti i server SQS). I messaggi non vengono cancellati
     * e tornano visibili allo scadere del timeout: da usare solo su code senza consumer, come le DLQ.
     */
    public List<String> readAllMessageBodies(String queueName, int visibilityTimeoutSeconds) {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_MESSAGES_PER_RECEIVE)
                .visibilityTimeout(visibilityTimeoutSeconds)
                .waitTimeSeconds(LONG_POLLING_WAIT_SECONDS)
                .build();

        List<String> bodies = new ArrayList<>();
        for (int call = 0; call < MAX_RECEIVE_CALLS; call++) {
            List<Message> messages = sqsClient.receiveMessage(request).messages();
            if (messages.isEmpty()) {
                return bodies;
            }
            messages.stream().map(Message::body).forEach(bodies::add);
        }
        throw new IllegalStateException("Coda " + queueName + " non letta per intero entro " + MAX_RECEIVE_CALLS + " letture");
    }
}
