package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Esegue le suite V23 e V25, ciascuna con la propria versione dello stream.
 */
@Suite
@SelectClasses({WebhookV23Test.class, WebhookV25Test.class})
public class WebhookV23V25Test {
}
