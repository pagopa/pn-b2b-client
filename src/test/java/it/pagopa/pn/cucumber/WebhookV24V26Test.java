package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Esegue le suite V24 e V26, ciascuna con la propria versione dello stream.
 */
@Suite
@SelectClasses({WebhookV24Test.class, WebhookV26Test.class})
public class WebhookV24V26Test {
}
