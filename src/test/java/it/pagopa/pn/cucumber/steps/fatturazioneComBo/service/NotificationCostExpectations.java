package it.pagopa.pn.cucumber.steps.fatturazioneComBo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Costi attesi, configurati sui microservizi di ciascun ambiente e non leggibili dai test.
 * <p>
 * Ogni valore è opzionale: se non viene passato (-D o variabile d'ambiente, es. {@code PN_COMBO_INVOICING_INFORMAL_BASE_COST})
 * i test verificano solo che il costo sia valorizzato, positivo e coerente fra timeline e fatturazione. Se viene passato,
 * verificano anche l'importo. In questo modo la suite gira in autonomia su ogni ambiente senza valori fissi.
 * <ul>
 *     <li>{@code pn.combo-invoicing.informal-base-cost}: costo della comunicazione bonaria. È configurato due volte e i
 *     due valori devono coincidere: PN_WORKFLOWMANAGER_PAGOPAINFORMALNOTIFICATIONBASECOST (pn-workflow-manager, costo
 *     sulle chiusure) e PN_DELIVERYPUSHVALIDATOR_PAGOPAINFORMALNOTIFICATIONBASECOST (pn-delivery-push-validator, rifiuti)</li>
 *     <li>{@code pn.combo-invoicing.informal-technical-refusal-cost}: costo di un rifiuto tecnico bonario con un
 *     destinatario, cioè il valore dopo il ";" di PN_DELIVERYPUSHVALIDATOR_INFORMALTECHNICALREFUSALCOSTMODE
 *     (es. {@code RECIPIENT_BASED;50} → 50)</li>
 *     <li>{@code pn.combo-invoicing.legal-base-cost}: PN_DELIVERYPUSHVALIDATOR_PAGOPANOTIFICATIONBASECOST</li>
 *     <li>{@code pn.combo-invoicing.legal-technical-refusal-cost}: costo di un rifiuto tecnico legale con un
 *     destinatario, cioè il valore dopo il ";" di PN_DELIVERYPUSHVALIDATOR_TECHNICALREFUSALCOSTMODE</li>
 *     <li>{@code pn.combo-invoicing.invoicing-ttl-days}: INVOICING_TTL_DAYS della lambda invoicingManager
 *     (pn-progression-sensor, default 365)</li>
 * </ul>
 * Il rifiuto è tecnico solo se la ricerca dell'indirizzo sui registri va in errore ({@code ADDRESS_SEARCH_FAILED});
 * negli altri casi il costo è quello base per il numero di destinatari.
 */
@Slf4j
@Component
public class NotificationCostExpectations {

    private final OptionalInt informalBaseCost;
    private final OptionalInt informalTechnicalRefusalCost;
    private final OptionalInt legalBaseCost;
    private final OptionalInt legalTechnicalRefusalCost;
    private final OptionalInt invoicingTtlDays;

    public NotificationCostExpectations(
            @Value("${pn.combo-invoicing.informal-base-cost:}") String informalBaseCost,
            @Value("${pn.combo-invoicing.informal-technical-refusal-cost:}") String informalTechnicalRefusalCost,
            @Value("${pn.combo-invoicing.legal-base-cost:}") String legalBaseCost,
            @Value("${pn.combo-invoicing.legal-technical-refusal-cost:}") String legalTechnicalRefusalCost,
            @Value("${pn.combo-invoicing.invoicing-ttl-days:}") String invoicingTtlDays) {
        this.informalBaseCost = parse(informalBaseCost);
        this.informalTechnicalRefusalCost = parse(informalTechnicalRefusalCost);
        this.legalBaseCost = parse(legalBaseCost);
        this.legalTechnicalRefusalCost = parse(legalTechnicalRefusalCost);
        this.invoicingTtlDays = parse(invoicingTtlDays);
        log.info("Costi attesi configurati: bonaria={}, rifiuto tecnico bonaria={}, legale={}, rifiuto tecnico legale={}, ttl={}",
                this.informalBaseCost, this.informalTechnicalRefusalCost, this.legalBaseCost, this.legalTechnicalRefusalCost, this.invoicingTtlDays);
    }

    public OptionalInt informalBaseCost() {
        return informalBaseCost;
    }

    public OptionalInt informalTechnicalRefusalCost() {
        return informalTechnicalRefusalCost;
    }

    public OptionalInt legalBaseCost() {
        return legalBaseCost;
    }

    public OptionalInt legalTechnicalRefusalCost() {
        return legalTechnicalRefusalCost;
    }

    public OptionalInt invoicingTtlDays() {
        return invoicingTtlDays;
    }

    private static OptionalInt parse(String value) {
        return Optional.ofNullable(value)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(v -> OptionalInt.of(Integer.parseInt(v)))
                .orElse(OptionalInt.empty());
    }
}
