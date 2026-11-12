package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Una suite può impostare una sola versione dello stream: V23 e V25 sono eseguite come suite distinte.
 */
@Suite
@SelectClasses({WebhookV23Test.class, WebhookV25Suite.class})
public class WebhookV23V25Test {
}
