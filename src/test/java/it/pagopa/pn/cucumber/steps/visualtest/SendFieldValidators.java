package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.FieldValidator;
import it.pagopa.common.pdf.visualtest.FieldValidators;

import java.util.regex.Pattern;

/**
 * Factory di {@link FieldValidator} specifici del dominio SEND/PagoPA.
 *
 * <p>Estende i validatori generici di {@link FieldValidators} con formati propri
 * del sistema SEND (IUN, codici fiscali, ecc.).</p>
 *
 * <p>Questa classe appartiene al modulo {@code pn-b2bclient} e <strong>non</strong>
 * deve essere spostata nel modulo {@code common}.</p>
 */
public final class SendFieldValidators {

    /**
     * Pattern IUN: formato {@code XXXX-XXXX-XXXX-NNNNNN-N-N}
     * (es. {@code YDKG-AHTX-KRKA-202502-N-1}).
     */
    private static final Pattern IUN_PATTERN = Pattern.compile(
            "\\b[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d\\b");

    /**
     * Pattern Codice Fiscale persona fisica (16 caratteri alfanumerici standard).
     */
    private static final Pattern CF_PF_PATTERN = Pattern.compile(
            "\\b[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]\\b");

    /**
     * Pattern Codice Fiscale / P.IVA persona giuridica (11 cifre).
     */
    private static final Pattern CF_PG_PATTERN = Pattern.compile(
            "\\b\\d{11}\\b");

    /**
     * Pattern indirizzo PEC: email standard con dominio.
     */
    private static final Pattern PEC_PATTERN = Pattern.compile(
            "\\b[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}\\b");

    private SendFieldValidators() {
    }

    /**
     * Validatore IUN: il campo deve contenere un codice IUN nel formato SEND standard.
     */
    public static FieldValidator iun() {
        return FieldValidators.regex(IUN_PATTERN,
                "IUN SEND (es. YDKG-AHTX-KRKA-202502-N-1)");
    }

    /**
     * Validatore Codice Fiscale persona fisica.
     */
    public static FieldValidator codiceFiscalePf() {
        return FieldValidators.regex(CF_PF_PATTERN,
                "Codice Fiscale PF (16 caratteri, es. RSSMRA80A01H501U)");
    }

    /**
     * Validatore Codice Fiscale / P.IVA persona giuridica.
     */
    public static FieldValidator codiceFiscalePg() {
        return FieldValidators.regex(CF_PG_PATTERN,
                "Codice Fiscale/P.IVA PG (11 cifre)");
    }

    /**
     * Validatore indirizzo PEC.
     */
    public static FieldValidator indirizzoEmail() {
        return FieldValidators.regex(PEC_PATTERN, "indirizzo email/PEC valido");
    }

    /**
     * Validatore data in formato italiano leggibile (es. {@code 31/01/2024}).
     */
    public static FieldValidator dataItaliana() {
        return FieldValidators.regex(
                Pattern.compile("\\b(?:0?[1-9]|[12]\\d|3[01])/(?:0?[1-9]|1[0-2])/\\d{4}\\b"),
                "data italiana (dd/MM/yyyy)");
    }

    /**
     * Validatore ora in formato HH:mm (es. {@code 10:30}).
     */
    public static FieldValidator oraHHmm() {
        return FieldValidators.regex(
                Pattern.compile("\\b(?:[01]\\d|2[0-3]):[0-5]\\d\\b"),
                "ora HH:mm");
    }
}
