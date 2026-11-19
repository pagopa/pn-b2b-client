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

    private final SqsClient sqsClient;

    /**
     * Reads (without deleting) the messages currently available on the queue.
     * Each received message stays invisible for {@value #VISIBILITY_TIMEOUT_SECONDS} seconds, so consecutive
     * receive calls scan the queue; the messages become visible again once the visibility timeout expires.
     */
    public List<Message> receiveMessages(String queueName) {
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();

        List<Message> messages = new ArrayList<>();
        Set<String> messageIds = new HashSet<>();
        for (int i = 0; i < MAX_RECEIVE_CALLS; i++) {
            List<Message> received = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(MAX_MESSAGES_PER_RECEIVE)
                    .waitTimeSeconds(WAIT_TIME_SECONDS)
                    .visibilityTimeout(VISIBILITY_TIMEOUT_SECONDS)
                    .messageAttributeNames("All")
                    .build()).messages();

            List<Message> newMessages = received.stream()
                    .filter(message -> messageIds.add(message.messageId()))
                    .toList();
            if (newMessages.isEmpty()) {
                break;
            }
            messages.addAll(newMessages);
        }
        return messages;
    }
}
