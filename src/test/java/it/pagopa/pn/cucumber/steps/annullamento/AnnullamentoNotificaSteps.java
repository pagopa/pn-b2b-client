package it.pagopa.pn.cucumber.steps.annullamento;

import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class AnnullamentoNotificaSteps {
    private final AnnullamentoNotificaPojo annullamentoNotificaPojo;

    public AnnullamentoNotificaSteps() {
        this.annullamentoNotificaPojo = new AnnullamentoNotificaPojo();
    }

    @Then("è presente il documento che ne attesta l'annullamento")
    public void recuperaDocumentoAnnullamento() {
        String iun = "TODO";

    }
}
