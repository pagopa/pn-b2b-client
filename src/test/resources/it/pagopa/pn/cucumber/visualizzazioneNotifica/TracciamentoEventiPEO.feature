Feature: irrobustimento tracciamento eventi PEO PostaElettronicaOrdinaria (SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2660007975/SRS+pn-ec+tracciamento+eventi+PEO+mail+non+legale)

  @TracciamentoEventiPEO
  Scenario Outline: [CHECK_EVENT_STATUS]
    Given viene impostato il client "<client>"
    When viene inviata una mail tramite PEO all'indirizzo "<emailAddress>" con allegato "<attachment>"
    Then la request recuperata da gestore-repository deve avere un'eventsList con i seguenti eventi "<events>"
    Examples:
      | client      | emailAddress                            | attachment | events                   |
      #Client con eventi PEO censiti su pn-EcAnagrafica (confinfo)
      | pn-test     | matteo.sperati@dgsspa.com               | null       | M003-sent;M004-delivered |
      | pn-test     | bounce@simulator.amazonses.com          | null       | M003-sent;M005-bounced   |
      | pn-test     | suppressionlist@simulator.amazonses.com | null       | M003-sent;M005-bounced   |
      | pn-test     | complaint@simulator.amazonses.com       | null       | M003-sent;M006-spam      |
      | pn-test     | matteo.sperati@dgsspa.com               | virus      | M003-sent;M009-rejected  |
      #Client con eventi PEO non censiti su pn-EcAnagrafica (tranne M003, che è di default)
      | pn-delivery | matteo.sperati@dgsspa.com               | null       | M003-sent                |
      | pn-delivery | bounce@simulator.amazonses.com          | null       | M003-sent                |
      | pn-delivery | suppressionlist@simulator.amazonses.com | null       | M003-sent                |
      | pn-delivery | complaint@simulator.amazonses.com       | null       | M003-sent                |
      | pn-delivery | matteo.sperati@dgsspa.com               | virus      | M003-sent                |

  @TracciamentoEventiPEO
  Scenario: [CHECK_DUPLICATE_REQUEST] La seconda chiamata con stessi parametri e requestId restituisce 204
    Given viene impostato il client "pn-test"
    When viene inviata una mail tramite PEO all'indirizzo "matteo.sperati@dgsspa.com" con allegato "null"
    And viene inviata nuovamente la stessa mail tramite PEO e si ottiene status code 204