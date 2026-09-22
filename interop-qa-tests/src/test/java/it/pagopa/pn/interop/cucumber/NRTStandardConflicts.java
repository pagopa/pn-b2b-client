package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({NrtTestConflicts.class, M2MV3TestConflicts.class, AdeguamentoAnalisiRischioTestConflicts.class})
public class NRTStandardConflicts {
}

