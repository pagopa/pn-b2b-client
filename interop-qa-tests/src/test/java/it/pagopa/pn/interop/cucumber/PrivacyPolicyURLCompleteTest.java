package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({PrivacyPolicyURLTest.class, M2MV3PrivacyPolicyURLTest.class})
public class PrivacyPolicyURLCompleteTest {
}

