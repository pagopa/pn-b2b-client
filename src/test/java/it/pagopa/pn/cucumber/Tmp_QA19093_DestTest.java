package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo C – Destinatario / ServiceDesk / trasversali (scenari #18-36 del resoconto).
 * Da cancellare a chiusura analisi.
 * <p>
 * {@code USER-ATTR_10} (anche in UserAttributes.feature) e {@code TIMELINE_REWORK_2} (anche in
 * TimelineReworkFase2.feature) sono disambiguati con l'inizio del titolo.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_DestTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/serviceDesk/ApiServiceCruscottoAssistenza.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicercaNotifichePA.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/AooUo/AooUoB2bUserAttributes.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualizzazioneNotifica/deleghe/pf/RicezioneNotifichePFWebDeleghe.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/ApiB2bDestinatariStrutturati.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/DelegheTemporanee.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/correzioneTimeline/TimelineRework.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/correzioneTimeline/TimelineReworkFase2.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/IOConnector.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/costiNotificaFase5/robustezzaApi/CostiNotifica_robustezzaApi.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/UserAttributes.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/comunicazioniBonarie/ComunicazioniBonarie.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/externalChannel/RichiestaCartaceaRequestPaId.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/AooUo/AooUoB2b.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-dest.json," +
        "html:target/cucumber-report-qa19093-dest.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = "^\\[(API-SERVICE-CA_CE02\\.2"
                + "|API-SERVICE-CA_CE02\\.2_8"
                + "|SERVICEDESK_RICERCA_NOTIFICHE_4"
                + "|MITTENTE_RICERCA_NOTIFICHE_BONARIE_2\\.B"
                + "|B2B-PF-ANNULLAMENTO_16"
                + "|WEB-PF-MANDATE_8"
                + "|Scenario-8"
                + "|MANDATE_TEMP_HAPPY_PATH_1"
                + "|MANDATE_TEMP_CREATION_FAILED_INVALID_QR"
                + "|TIMELINE_REWORK_9_1"
                + "|IO_CONNECTOR_3\\.1\\.1"
                + "|CNF5_ROBUSTEZZA_API_RECUPERO_COSTI"
                + "|CNF5_ROBUSTEZZA_API_INSERIMENTO_COSTI"
                + "|B2B-PF-TOS_1"
                + "|PREPARE-TC2A-KO"
                + "|EC-PAPER-REQUESTPAID-1"
                + "|B2B-AOO-UO_FILTER_1"
                + ")\\].*"
                + "|^\\[USER-ATTR_10\\] inserimento email di cortesia per ente figlio.*"
                + "|^\\[TIMELINE_REWORK_2\\] Viene invocata con errori.*"
)
@ExcludeTags({"ignore"})
public class Tmp_QA19093_DestTest {
}
