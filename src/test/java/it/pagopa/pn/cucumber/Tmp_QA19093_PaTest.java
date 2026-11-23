package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo A – PA senza webhook (scenari #41-42, #44-61 del resoconto; i webhook sono in
 * {@link Tmp_QA19093_WebhookTest}).
 * Da cancellare a chiusura analisi.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_PaTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualizzazioneNotifica/VisualizzazioneTimeStampTecniciSLA.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/baseFunction/RestApiValidation.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPFPagamento.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheB2bPF.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/b2b/AvanzamentoNotificheAsyncB2bValidation.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/download/pf/AvanzamentoNotifichePFLegalFact.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/baseFunction/contentVerify/LegalFactContentVerify.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/e2e/AnalogSend.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualizzazioneNotifica/destinatario/pf/RicezioneNotifichePFAppIO.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/ApikeyManager.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/AooUo/AooUoB2b.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/partnerTecnologici/PtEntiDeleganti.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/mandateb2bmassivi/B2bDelegatiMassivi.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-pa.json," +
        "html:target/cucumber-report-qa19093-pa.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = "^\\[(TIMESTAMP_TECNICI_SLA_READ_STREAM_KO"
                + "|TIMESTAMP_TECNICI_SLA_READ_STREAM_OK"
                + "|REST_VALIDATION_2"
                + "|B2B-PA-ANNULLAMENTO_11"
                + "|B2B-PA-ANNULLAMENTO_31"
                + "|B2B-PA-ANNULLAMENTO_8_2"
                + "|B2B-PA-ANNULLAMENTO_40"
                + "|B2B-PA-PAY_CHECKOUT_1"
                + "|B2B_TIMELINE_27"
                + "|B2B_ASYNC_2_VERSIONAMENTO"
                + "|B2B_PA_LEGALFACT_9"
                + "|B2B_LEGAL_FACT_RIMOSSO_DA_SS_BUG_19624"
                + "|B2B_ANALOG_SEND_6"
                + "|B2B-PA-APP-IO_5"
                + "|QR_CODE_1"
                + "|API-KEY_3"
                + "|API-KEY_9"
                + "|B2B-AOO-UO_1"
                + "|PT-ENTI-DELEGANTI_13"
                + "|B2B-REVERSE-MANDATE-FAILURE-1"
                + ")\\].*"
)
@ExcludeTags({"ignore"})
public class Tmp_QA19093_PaTest {
}
