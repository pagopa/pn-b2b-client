package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;
import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;

public class DestinatarioRegistry {

    public final Destinatario DESTINATARIO_MARIO_GHERKIN;
    public final Destinatario DESTINATARIO_MARIO_CUCUMBER;
    public final Destinatario DESTINATARIO_SIGNOR_CASUALE;
    public final Destinatario DESTINATARIO_CRISTOFORO_COLOMBO;
    public final Destinatario DESTINATARIO_ETTORE_FIERAMOSCA;
    public final Destinatario DESTINATARIO_LEONARDO_DA_VINCI;
    public final Destinatario DESTINATARIO_GALILEO_GALILEI;
    public final Destinatario DESTINATARIO_GHERKIN_SPA;
    public final Destinatario DESTINATARIO_CUCUMBER_SPA;
    public final Destinatario DESTINATARIO_GHERKIN_SRL;
    public final Destinatario DESTINATARIO_CUCUMBER_SRL;
    public final Destinatario DESTINATARIO_GHERKIN_ANALOGIC;
    public final Destinatario DESTINATARIO_CUCUMBER_ANALOGIC;
    public final Destinatario DESTINATARIO_GHERKIN_IRREPERIBILE;
    public final Destinatario DESTINATARIO_CUCUMBER_SOCIETY;
    public final Destinatario DESTINATARIO_SIGNOR_GENERATO;
    public final Destinatario DESTINATARIO_NESSUNO;
    public final Destinatario DESTINATARIO_ERRORE_D01;
    public final Destinatario DESTINATARIO_INDIRIZZO_VALIDO_ANPR;

    private final List<Destinatario> all;
    private final TaxIdConfig taxIds;

    @Autowired
    public DestinatarioRegistry(TaxIdConfig taxIds) {
        this.taxIds = taxIds;

        DESTINATARIO_MARIO_GHERKIN = new Destinatario(MARIO_GHERKIN, taxIds.getMarioGherkin(), PF, PEC);
        DESTINATARIO_MARIO_CUCUMBER = new Destinatario(MARIO_CUCUMBER, taxIds.getMarioCucumber(), PF, PEC);
        DESTINATARIO_SIGNOR_CASUALE = new Destinatario(SIGNOR_CASUALE, null, PF, PEC);
        DESTINATARIO_CRISTOFORO_COLOMBO = new Destinatario(CRISTOFORO_COLOMBO, taxIds.getMarioGherkin(), PF, PEC);
        DESTINATARIO_ETTORE_FIERAMOSCA = new Destinatario(ETTORE_FIERAMOSCA, taxIds.getMarioCucumber(), PF, PEC);
        DESTINATARIO_LEONARDO_DA_VINCI = new Destinatario(LEONARDO_DA_VINCI, taxIds.getLeonardoDaVinci(), PF, PEC);
        DESTINATARIO_GALILEO_GALILEI = new Destinatario(GALILEO_GALILEI, taxIds.getGalileoGalilei(), PF, PEC);
        DESTINATARIO_GHERKIN_SPA = new Destinatario(GHERKIN_SPA, taxIds.getGherkinSpa(), PG, PEC);
        DESTINATARIO_CUCUMBER_SPA = new Destinatario(CUCUMBER_SPA, taxIds.getCucumberSpa(), PG, PEC);
        DESTINATARIO_GHERKIN_SRL = new Destinatario(GHERKIN_SRL, taxIds.getGherkinSrl(), PG, PEC);
        DESTINATARIO_CUCUMBER_SRL = new Destinatario(CUCUMBER_SRL, taxIds.getCucumberSpa(), PG, PEC);
        DESTINATARIO_GHERKIN_ANALOGIC = new Destinatario(GHERKIN_ANALOGIC, taxIds.getGherkinAnalogic(), PG, PEC);
        DESTINATARIO_CUCUMBER_ANALOGIC = new Destinatario(CUCUMBER_ANALOGIC, taxIds.getCucumberAnalogic(), PF, PEC);
        DESTINATARIO_GHERKIN_IRREPERIBILE = new Destinatario(GHERKIN_IRREPERIBILE, taxIds.getGherkinIrreperibile(), PG, PEC);
        DESTINATARIO_CUCUMBER_SOCIETY = new Destinatario(CUCUMBER_SOCIETY, taxIds.getCucumberSociety(), PG, PEC);
        DESTINATARIO_SIGNOR_GENERATO = new Destinatario(SIGNOR_GENERATO, FiscalCodeGenerator.generateCF(System.nanoTime()), PF, PEC);
        DESTINATARIO_NESSUNO = new Destinatario(NESSUNO, null, null, null);
        DESTINATARIO_ERRORE_D01 = new Destinatario(UTENZA_CON_INDIRIZZO_NON_VALIDO, taxIds.getUserIndirizzoNonValidoD01(), PF, PEC);
        DESTINATARIO_INDIRIZZO_VALIDO_ANPR = new Destinatario(UTENZA_CON_INDIRIZZO_VALIDO_ANPR, taxIds.getUserIndirizzoValidoAnpr(), PF, PEC);

        all = List.of(
                DESTINATARIO_MARIO_GHERKIN, DESTINATARIO_MARIO_CUCUMBER, DESTINATARIO_SIGNOR_CASUALE,
                DESTINATARIO_CRISTOFORO_COLOMBO, DESTINATARIO_ETTORE_FIERAMOSCA, DESTINATARIO_LEONARDO_DA_VINCI,
                DESTINATARIO_GALILEO_GALILEI, DESTINATARIO_GHERKIN_SPA, DESTINATARIO_CUCUMBER_SPA,
                DESTINATARIO_GHERKIN_SRL, DESTINATARIO_CUCUMBER_SRL, DESTINATARIO_GHERKIN_ANALOGIC,
                DESTINATARIO_CUCUMBER_ANALOGIC, DESTINATARIO_GHERKIN_IRREPERIBILE, DESTINATARIO_CUCUMBER_SOCIETY,
                DESTINATARIO_SIGNOR_GENERATO, DESTINATARIO_NESSUNO, DESTINATARIO_ERRORE_D01, DESTINATARIO_INDIRIZZO_VALIDO_ANPR
        );
    }

    public String getSenderTaxIdFromProperties(String paName) {
        return switch (paName) {
            case COMUNE_1 -> taxIds.getComune1();
            case COMUNE_2 -> taxIds.getComune2();
            case COMUNE_MULTI -> taxIds.getComuneMulti();
            case COMUNE_SON -> taxIds.getComuneSon();
            case COMUNE_ROOT -> taxIds.getComuneRoot();
            default -> throw new IllegalArgumentException("PA non riconosciuta: " + paName);
        };
    }

    @ParameterType(MARIO_GHERKIN + "|" +
            MARIO_CUCUMBER + "|" +
            SIGNOR_CASUALE + "|" +
            ETTORE_FIERAMOSCA + "|" +
            CRISTOFORO_COLOMBO + "|" +
            LEONARDO_DA_VINCI + "|" +
            GHERKIN_SPA + "|" +
            CUCUMBER_SPA + "|" +
            GHERKIN_SRL + "|" +
            CUCUMBER_SRL + "|" +
            GHERKIN_ANALOGIC + "|" +
            CUCUMBER_ANALOGIC + "|" +
            GHERKIN_IRREPERIBILE + "|" +
            CUCUMBER_SOCIETY + "|" +
            SIGNOR_GENERATO + "|" +
            GALILEO_GALILEI + "|" +
            NESSUNO + "|" +
            UTENZA_CON_INDIRIZZO_NON_VALIDO + "|" +
            UTENZA_CON_INDIRIZZO_VALIDO_ANPR
    )
    public Destinatario destinatario(String name) {
        return all.stream()
                .filter(d -> d.getDenomination() != null && d.getDenomination().trim().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
