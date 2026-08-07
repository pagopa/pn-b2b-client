package it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage;

import it.pagopa.pn.cucumber.steps.informalNotification.InformalDto.InformalMessageContent;
import lombok.Getter;

@Getter
public enum InformalMessageTemplates {

    RISCUOTI_IT(
            new InformalMessageContent(
                    "IT",
                    "È stata emessa una nuova fattura per te",
                    """
                    Riscuoti S.p.a. ti informa che è stata emessa una fattura per l'utenza n.1812121.
                    """,
                    """
                    SEND, ti informa che hai ricevuto
                    una comunicazione da Riscuoti  S.p.A.
                    """
            )
    ),

    RISCUOTI_DE(
            new InformalMessageContent(
                    "DE",
                    "Für Sie wurde eine neue Rechnung erstellt",
                    """
                    Riscuoti S.p.A. informiert Sie: Für Anschluss Nr.1812121 wurde eine Rechnung erstellt.
                    """,
                    """
                    SEND informiert Sie über eine Mitteilung von Riscuoti S.p.A.
                    """
            )
    ),

    RISCUOTI_EN(
            new InformalMessageContent(
                    "EN",
                    "A new invoice has been issued for you",
                    """
                    Riscuoti S.p.A. informs you that a new invoice has been issued for customer account no. 1812121.
                    """,
                    """
                    SEND informs you that you have received
                    a communication from Riscuoti S.p.A.
                    """
            )
    ),

    RISCUOTI_SL(
            new InformalMessageContent(
                    "SL",
                    "Za vas je bil izdan nov račun",
                    """
                    Družba Riscuoti S.p.A. vas obvešča, da je bil za pogodbo št. 1812121 izdan nov račun.
                    """,
                    """
                    SEND vas obvešča, da ste prejeli sporočilo družbe Riscuoti S.p.A.
                    """
            )
    ),

    RISCUOTI_FR(
            new InformalMessageContent(
                    "FR",
                    "Une nouvelle facture a ete emise pour vous",
                    """
                    Riscuoti S.p.A. vous informe qu une facture a ete emise pour le contrat n°1812121.
                    """,
                    """
                    SEND vous informe que vous avez reçu une communication de Riscuoti S.p.A.
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