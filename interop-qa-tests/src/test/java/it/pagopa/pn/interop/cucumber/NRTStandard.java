package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({NrtTest.class, M2MV3Test.class})
public class NRTStandard {
}

