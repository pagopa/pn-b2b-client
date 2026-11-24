package it.pagopa.pn.cucumber.steps.templateEngine.strategies.comunicazioniBonarie;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalCommunication;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalCommunicationBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalCommunicationSender;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalEmailCommunicationSubject;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalSmsCommunication;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.RecipientTypeEnum;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.SharedInformalCommunicationRecipient;

import java.util.Map;

/**
 * Costruisce i payload delle comunicazioni bonarie popolando tutti i campi obbligatori degli
 * schemi OpenAPI e applicando solo gli override richiesti dal DataTable dello scenario Cucumber.
 * Serve a evitare che un test sul singolo campo "X" fallisca in realtà perché anche altri campi
 * obbligatori non testati risultano assenti dal payload.
 * <p>
 * I campi non testati (assenti dal DataTable) vengono valorizzati con il nome del campo stesso,
 * cosi' il valore e' sempre lo stesso e prevedibile per le asserzioni finali sul contenuto del
 * template generato.
 */
final class InformalCommunicationRequestFactory {

    private static final String DEFAULT_IUN = "UTGP-ZRHR-XDNQ-202505-Q-1";
    private static final RecipientTypeEnum DEFAULT_RECIPIENT_TYPE = RecipientTypeEnum.PF;

    private InformalCommunicationRequestFactory() {
    }

    static InformalCommunication buildInformalCommunication(Map<String, String> parameters) {
        InformalCommunicationBody body = new InformalCommunicationBody()
                .primaryContent(requiredString(parameters, "body_primaryContent"))
                .secondaryContent(optionalString(parameters, "body_secondaryContent"));

        InformalCommunicationSender sender = new InformalCommunicationSender()
                .denomination(requiredString(parameters, "sender_denomination"))
                .id(requiredString(parameters, "sender_id"))
                .service(requiredString(parameters, "sender_service"));

        SharedInformalCommunicationRecipient recipient = new SharedInformalCommunicationRecipient()
                .denomination(requiredString(parameters, "recipient_denomination"))
                .taxId(requiredString(parameters, "recipient_taxId"));
        recipient.setRecipientType(requiredRecipientType(parameters, "recipient_recipientType"));

        return new InformalCommunication()
                .iun(requiredIun(parameters, "iun"))
                .subject(requiredString(parameters, "subject"))
                .body(body)
                .sender(sender)
                .recipient(recipient)
                .hasAttachment(requiredBoolean(parameters, "hasAttachment", true))
                .hasPayment(requiredBoolean(parameters, "hasPayment", false))
                .checkoutUrl(optionalString(parameters, "checkoutUrl"));
    }

    static InformalEmailCommunicationSubject buildInformalEmailCommunicationSubject(Map<String, String> parameters) {
        return new InformalEmailCommunicationSubject()
                .senderDenomination(requiredString(parameters, "senderDenomination"))
                .recipientDenomination(requiredString(parameters, "recipientDenomination"))
                .subject(requiredString(parameters, "subject"));
    }

    static InformalSmsCommunication buildInformalSmsCommunication(Map<String, String> parameters) {
        return new InformalSmsCommunication()
                .recipientType(requiredRecipientType(parameters, "recipientType"))
                .senderPaDenomination(requiredString(parameters, "senderPaDenomination"));
    }

    /**
     * Se il campo non e' esplicitamente indicato nel DataTable, viene valorizzato con il nome
     * del campo stesso (es. "sender_denomination"), cosi' da avere un valore fisso e prevedibile
     * su cui poter scrivere le asserzioni di contenuto.
     */
    private static String requiredString(Map<String, String> parameters, String key) {
        if (!parameters.containsKey(key)) return key;
        String value = parameters.get(key);
        return "null".equals(value) ? null : value;
    }

    private static String optionalString(Map<String, String> parameters, String key) {
        if (!parameters.containsKey(key)) return null;
        String value = parameters.get(key);
        return "null".equals(value) ? null : value;
    }

    private static Boolean requiredBoolean(Map<String, String> parameters, String key, boolean defaultValue) {
        if (!parameters.containsKey(key)) return defaultValue;
        String value = parameters.get(key);
        return "null".equals(value) ? null : Boolean.valueOf(value);
    }

    private static RecipientTypeEnum requiredRecipientType(Map<String, String> parameters, String key) {
        if (!parameters.containsKey(key)) return DEFAULT_RECIPIENT_TYPE;
        String value = parameters.get(key);
        return "null".equals(value) ? null :RecipientTypeEnum.fromValue(value);
    }

    /**
     * Se non specificato, restituisce sempre lo stesso IUN sintatticamente valido nel formato
     * XXXX-XXXX-XXXX-YYYYMM-X-N (es. UTGP-ZRHR-XDNQ-202505-Q-1).
     */
    private static String requiredIun(Map<String, String> parameters, String key) {
        if (!parameters.containsKey(key)) return DEFAULT_IUN;
        String value = parameters.get(key);
        return "null".equals(value) ? null : value;
    }
}
