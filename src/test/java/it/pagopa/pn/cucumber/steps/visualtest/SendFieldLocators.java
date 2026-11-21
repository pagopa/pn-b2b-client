package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.FieldLocator;
import it.pagopa.common.pdf.visualtest.FieldLocators;

import java.util.regex.Pattern;

/** Locators of the SEND dynamic values: each value is a single word fragment, found wherever it is in the page. */
public final class SendFieldLocators {

    private static final Pattern IUN = Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-[A-Z0-9]");
    private static final Pattern DATE = Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b");
    private static final Pattern TIME = Pattern.compile("\\b(?:[01]\\d|2[0-3]):[0-5]\\d\\b");
    private static final Pattern CF_PF = Pattern.compile("\\b[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]\\b");
    private static final Pattern CF_PG = Pattern.compile("\\b\\d{11}\\b");

    private SendFieldLocators() {
    }

    public static FieldLocator iun() {
        return FieldLocators.regex(IUN);
    }

    public static FieldLocator date() {
        return FieldLocators.regex(DATE);
    }

    public static FieldLocator time() {
        return FieldLocators.regex(TIME);
    }

    public static FieldLocator codiceFiscalePf() {
        return FieldLocators.regex(CF_PF);
    }

    public static FieldLocator codiceFiscalePg() {
        return FieldLocators.regex(CF_PG);
    }
}
