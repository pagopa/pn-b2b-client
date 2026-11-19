package it.pagopa.pn.client.b2b.pa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SqsService {
    private static final int MAX_MESSAGES_PER_RECEIVE = 10;
    private static final int WAIT_TIME_SECONDS = 2;
    private static final int VISIBILITY_TIMEOUT_SECONDS = 30;
    private static final int MAX_RECEIVE_CALLS = 50;
    private static final int FULL_SCAN_MAX_RECEIVE_CALLS = 2000;

    private final SqsClient sqsClient;

    /**
     * Reads (without deleting) the messages currently available on the queue.
     * Each received message stays invisible for {@value #VISIBILITY_TIMEOUT_SECONDS} seconds, so consecutive
     * receive calls scan the queue; the messages become visible again once the visibility timeout expires.
     * The scan stops after {@value #MAX_RECEIVE_CALLS} receive calls even if the queue still has messages.
     */
    public List<Message> receiveMessages(String queueName) {
        return scan(queueName, VISIBILITY_TIMEOUT_SECONDS, MAX_RECEIVE_CALLS, false);
    }

    /**
     * Reads (without deleting) every visible message of the queue, up to the first empty receive call.
     * The messages stay invisible for {@code visibilityTimeoutSeconds} and are never deleted: use it only on queues
     * without consumers, such as DLQs.
     *
     * @throws IllegalStateException if the queue is not fully read within {@value #FULL_SCAN_MAX_RECEIVE_CALLS} calls
     */
    public List<Message> receiveAllMessages(String queueName, int visibilityTimeoutSeconds) {
        return scan(queueName, visibilityTimeoutSeconds, FULL_SCAN_MAX_RECEIVE_CALLS, true);
    }

    private List<Message> scan(String queueName, int visibilityTimeoutSeconds, int maxReceiveCalls, boolean failIfNotExhausted) {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_MESSAGES_PER_RECEIVE)
                .waitTimeSeconds(WAIT_TIME_SECONDS)
                .visibilityTimeout(visibilityTimeoutSeconds)
                .messageAttributeNames("All")
                .build();

        List<Message> messages = new ArrayList<>();
        Set<String> messageIds = new HashSet<>();
        for (int call = 0; call < maxReceiveCalls; call++) {
            List<Message> newMessages = sqsClient.receiveMessage(request).messages().stream()
                    .filter(message -> messageIds.add(message.messageId()))
                    .toList();
            if (newMessages.isEmpty()) {
                return messages;
            }
            messages.addAll(newMessages);
        }
        if (failIfNotExhausted) {
            throw new IllegalStateException("Queue " + queueName + " not fully read within " + maxReceiveCalls + " receive calls");
        }
        return messages;
    }
}
