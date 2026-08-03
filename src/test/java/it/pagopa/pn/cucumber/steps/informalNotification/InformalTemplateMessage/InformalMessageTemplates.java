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

    SORICAL_DE(
            new InformalMessageContent(
                    "DE",
                    "Für Sie wurde eine neue Rechnung erstellt",
                    """
                    Sorical S.p.A. informiert Sie darüber, dass für den Vertrag Nr. 182140 eine neue Rechnung erstellt wurde.
                    """,
                    """
                    SEND informiert Sie darüber, dass Sie eine Mitteilung von Sorical S.p.A. erhalten haben.
                    """
            )
    ),

    SORICAL_SL(
            new InformalMessageContent(
                    "SL",
                    "Za vas je bil izdan nov račun",
                    """
                    Družba Sorical S.p.A. vas obvešča, da je bil za pogodbo št. 182140 izdan nov račun.
                    """,
                    """
                    SEND vas obvešča, da ste prejeli sporočilo družbe Sorical S.p.A.
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