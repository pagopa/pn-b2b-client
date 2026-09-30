package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Una suite può impostare una sola versione dello stream: V24 e V26 sono eseguite come suite distinte.
 */
@Suite
@SelectClasses({WebhookV24Suite.class, WebhookV26Suite.class})
public class WebhookV24V26Test {
}
