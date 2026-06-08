package it.pagopa.pn.cucumber.steps.templateEngine.strategies;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowFailureLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowFailureRecipient;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AnalogDeliveryWorkflowFailureLegalFactStrategy implements ITemplateEngineStrategy {

    private final ITemplateEngineClient templateEngineClient;

    public AnalogDeliveryWorkflowFailureLegalFactStrategy(ITemplateEngineClient templateEngineClient) {
        this.templateEngineClient = templateEngineClient;
    }

    @Override
    public TemplateEngineResult retrieveTemplate(String language, boolean body, TemplateRequestContext context) {
        AnalogDeliveryWorkflowFailureLegalFact legalFact = createRequest(body, context);
        Resource file = templateEngineClient.analogDeliveryWorkflowFailureLegalFact(selectLanguage(language), legalFact);
        return new TemplateEngineResult(file);
    }

    @Override
    public String getTextToCheckLanguage(String language, String recipientType) {
        return switch (language.toUpperCase()) {
            case  "ITALIANA" -> {
                yield "Deposito avviso di avvenuta ricezione Con riferimento alla notifica avente IUN , ai sensi dell’art. 26, comma 7, del D.L. 76/2020, essendo risultato il string destinatario string CF";
            }
            case "TEDESCA" -> {
                yield "Deposito avviso di avvenuta ricezione Im Sinne des Art. 26, Absatz 7, des G.D. 76/2020, betreffend die Zustellung";
            }
            case "SLOVENA" -> {
                yield "Deposito avviso di avvenuta ricezione V zvezi z obvestilom, ki vsebuje IUN , v skladu s členom 26, sedmi odstavek, zakonodajnega odloka št. 76/2020, je string prejemnik string DŠ";
            }
            case "FRANCESE" -> {
                yield "Deposito avviso di avvenuta ricezione En ce qui concerne la notification comportant IUN , conformément à l'article 26, alinéa 7, du décret législatif 76string /2020, le destinataire étant string Code fiscale";
            }
            case "INGLESE" -> {
                yield "Deposito avviso di avvenuta ricezione With reference to the notification with IUN , pursuant to Art. 26, paragraph 7 of D.L. 76/2020, as the recipient string string Fiscal Code";
            }
            default -> throw new IllegalArgumentException("NO VALID LANGUANGE");
        };
    }

    private AnalogDeliveryWorkflowFailureLegalFact createRequest(boolean body, TemplateRequestContext context) {
        if (!body)
            return null;

        return new AnalogDeliveryWorkflowFailureLegalFact()
                .iun(context.getIun())
                .recipient(createAnalogDeliveryWorkflowFailureRecipient(context))
                .endWorkflowDate(context.getEndWorkflowDate())
                .endWorkflowTime(context.getEndWorkflowTime());
    }

    private AnalogDeliveryWorkflowFailureRecipient createAnalogDeliveryWorkflowFailureRecipient(TemplateRequestContext context) {
        return Optional.ofNullable(context.getRecipient())
                .map(data -> new AnalogDeliveryWorkflowFailureRecipient()
                            .denomination(data.getDenomination())
                            .taxId(data.getTaxId()))
                .orElse(null);
    }
}
