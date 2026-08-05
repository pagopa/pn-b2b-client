package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/* Aggregatore di alcune piccole suite verticali necessarie per la versione 2.22 Interop. Esiste solo per comodità,
* potrà essere rimosso in futuro. */
@Suite
@SelectClasses({UrlDescriptionTest.class, PIN10457Test.class, DocumentTypeCheckTest.class})
public class Verticali2_22Test {
}

