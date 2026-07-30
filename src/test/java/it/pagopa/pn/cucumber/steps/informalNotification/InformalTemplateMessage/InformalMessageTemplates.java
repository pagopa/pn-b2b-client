package it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage;

import it.pagopa.pn.cucumber.steps.informalNotification.InformalDto.InformalMessageContent;
import lombok.Getter;

@Getter
public enum InformalMessageTemplates {

    SORICAL_IT(
            new InformalMessageContent(
                    "IT",
                    "È stata emessa una nuova fattura per te",
                    """
                    Sorical S.p.a. ti informa che è stata emessa una fattura per l'utenza n.182140.
                    """,
                    """
                    SEND, ti informa che hai ricevuto
                    una comunicazione da Sorical S.p.A.
                    """
            )
    ),

    SORICAL_FR(
            new InformalMessageContent(
                    "FR",
                    "Une nouvelle facture a ete emise pour vous",
                    """
                    Sorical S.p.A. vous informe qu une facture a ete emise pour le contrat n°182140.
                    """,
                    """
                    SEND vous informe que vous avez reçu une communication de Sorical S.p.A.
                    """
            )
    );

    private final InformalMessageContent content;

    InformalMessageTemplates(InformalMessageContent content) {
        this.content = content;
    }

    public InformalMessageContent getContent() {
        return content;
    }

}