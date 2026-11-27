package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({M2MV3ProducerKeychainTest.class, ProducerKeychainTest.class})
public class ProducerKeychainSuiteTest {
}

