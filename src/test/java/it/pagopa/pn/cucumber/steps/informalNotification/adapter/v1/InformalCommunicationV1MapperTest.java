package it.pagopa.pn.cucumber.steps.informalNotification.adapter.v1;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRecipientV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalCommunicationV1Mapper;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InformalCommunicationV1MapperTest {

    private final InformalCommunicationV1Mapper mapper = Mappers.getMapper(InformalCommunicationV1Mapper.class);

    @Test
    public void testMapInformalCommunicationToV1() {
        UUID messageId = UUID.randomUUID();
        InformalPhysicalAddress address = InformalPhysicalAddress.builder()
                .address("Via Roma 1")
                .municipality("Roma")
                .province("RM")
                .zip("00100")
                .foreignState("ITALIA")
                .build();

        InformalPayment payment = InformalPayment.builder()
                .amount(100)
                .noticeCode("123456789012345678")
                .creditorTaxId("12345678901")
                .dueDate(OffsetDateTime.now())
                .attachmentKey("classpath:/payment.pdf")
                .attachmentContentType("application/pdf")
                .build();

        InformalRecipient recipient = InformalRecipient.builder()
                .recipientType(RecipientType.PF)
                .denomination("Mario Rossi")
                .taxId("RSSMRA80A01H501U")
                .email("mario.rossi@example.com")
                .digitalDomicile("mario.rossi@pec.it")
                .messageId(messageId)
                .physicalAddress(address)
                .payments(List.of(payment))
                .additionalLanguages(List.of("FR"))
                .build();

        InformalDocument doc = InformalDocument.builder()
                .key("classpath:/doc.pdf")
                .contentType("application/pdf")
                .sha256("dummy-sha256")
                .build();

        InformalCommunication domain = InformalCommunication.builder()
                .campaignId("Campagna1")
                .senderDenomination("Comune di Test")
                .paProtocolNumber("PROT-123")
                .subject("Oggetto Test")
                .group("Gruppo1")
                .recipients(List.of(recipient))
                .documents(List.of(doc))
                .build();

        InformalNotificationRequestV1 v1 = mapper.toV1(domain);

        assertNotNull(v1);
        assertEquals("Campagna1", v1.getCampaignId());
        assertEquals("Comune di Test", v1.getSenderDenomination());
        assertEquals("PROT-123", v1.getPaProtocolNumber());
        assertEquals("Oggetto Test", v1.getSubject());
        assertEquals("Gruppo1", v1.getGroup());

        assertEquals(1, v1.getRecipients().size());
        InformalNotificationRecipientV1 recV1 = v1.getRecipients().get(0);
        assertEquals(InformalNotificationRecipientV1.RecipientTypeEnum.PF, recV1.getRecipientType());
        assertEquals("Mario Rossi", recV1.getDenomination());
        assertEquals("RSSMRA80A01H501U", recV1.getTaxId());
        assertEquals("mario.rossi@example.com", recV1.getEmail());
        assertNotNull(recV1.getDigitalDomicile());
        assertEquals("mario.rossi@pec.it", recV1.getDigitalDomicile().getAddress());
        assertEquals(messageId, recV1.getMessageId());
        assertEquals(List.of("FR"), recV1.getAdditionalLanguages());

        assertNotNull(recV1.getPhysicalAddress());
        assertEquals("Via Roma 1", recV1.getPhysicalAddress().getAddress());

        assertEquals(1, recV1.getPayments().size());
        assertNotNull(recV1.getPayments().get(0).getPagoPa());
        assertEquals(100, recV1.getPayments().get(0).getPagoPa().getAmount());

        assertEquals(1, v1.getDocuments().size());
        assertEquals("classpath:/doc.pdf", v1.getDocuments().get(0).getRef().getKey());
        assertEquals("dummy-sha256", v1.getDocuments().get(0).getDigests().getSha256());
    }
}
