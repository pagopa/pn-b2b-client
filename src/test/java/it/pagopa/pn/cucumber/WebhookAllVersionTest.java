package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Mantiene la selezione storica di questo runner, limitata alla V27.
 */
@Suite
@SelectClasses({WebhookV27Test.class})
public class WebhookAllVersionTest {
}
