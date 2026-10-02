package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Nonostante il nome, questo runner ha sempre selezionato solo la V27: la selezione è mantenuta.
 */
@Suite
@SelectClasses({WebhookV27Test.class})
public class WebhookAllVersionTest {
}
