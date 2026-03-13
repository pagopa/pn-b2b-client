Feature: irrobustimento tracciamento eventi PEO PostaElettronicaOrdinaria (SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2660007975/SRS+pn-ec+tracciamento+eventi+PEO+mail+non+legale)

  @TracciamentoEventiPEO
  Scenario Outline: [CHECK_EVENT_STATUS]
    When viene inviata una mail tramite PEO all'indirizzo "<emailAddress>" con allegato "<attachment>"
    Then recuperando la request da gestore-repository, verifico che il record contenga un evento con statusCode "<statusCode>" e status "<status>"
    Examples:
      | emailAddress                            | attachment | statusCode | status    |
      | matteo.sperati@dgsspa.com               | null       | M004       | delivered |
      | bounce@simulator.amazonses.com          | null       | M005       | bounced   |
      | suppressionlist@simulator.amazonses.com | null       | M005       | bounced   |
      | complaint@simulator.amazonses.com       | null       | M006       | spam      |
#      | matteo.sperati@dgsspa.com | virus      | M009       | rejected |