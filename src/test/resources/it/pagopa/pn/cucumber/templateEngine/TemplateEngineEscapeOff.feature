Feature: Template Engine - HTML escaping disabilitato

  # Il feature flag dell'HTML escaping deve essere disabilitato nell'ambiente di testing.

  @templateEngineEscapeOff
  Scenario Outline: [TEMPLATE-ENGINE_HTML-ESCAPE_OFF_1_A] Verifica comportamento con HTML escaping disabilitato sugli endpoint PDF
    When eseguo il fuzzing HTML escaping sull'endpoint "<endpoint>" in lingua "italiana" sui campi "<fields>"
    Then verifico che tutti i valori fuzzed siano rappresentati con HTML escaping disabilitato nel template di tipo "pdf"

    Examples:
      | endpoint                                       | fields                                                                                                                                                                                  |
      | notification-received-legal-fact               | subject,notification.sender.paDenomination,notification.recipients.denomination,notification.recipients.physicalAddressAndDenomination,notification.recipients.digitalDomicile.address |
      | pec-delivery-workflow-legal-fact               | deliveries.denomination,deliveries.address                                                                                                                                                |
      | notification-viewed-legal-fact                  | recipient.denomination,delegate.denomination                                                                                                                                              |
      | notification-cancelled-legal-fact               | notification.sender.paDenomination,notification.recipients.denomination                                                                                                                   |
      | analog-delivery-workflow-failure-legal-fact     | recipient.denomination                                                                                                                                                                    |
      | analog-delivery-workflow-timeout-legal-fact     | recipient.denomination,recipient.physicalAddress                                                                                                                                          |
      | notification-aar-radd-alt                       | notification.subject,notification.sender.paDenomination,recipient.denomination                                                                                                            |
      | notification-aar                                | notification.subject,notification.sender.paDenomination                                                                                                                                   |
      | analog-feedback-availability-statement          | senderDenomination                                                                                                                                                                        |
      | informal/analog-communication                   | subject,sender.denomination,recipient.denomination                                                                                                                                        |


  @templateEngineEscapeOff
  Scenario Outline: [TEMPLATE-ENGINE_HTML-ESCAPE_OFF_1_B] Verifica comportamento con HTML escaping disabilitato sugli endpoint HTML
    When eseguo il fuzzing HTML escaping sull'endpoint "<endpoint>" in lingua "italiana" sui campi "<fields>"
    Then verifico che tutti i valori fuzzed siano rappresentati con HTML escaping disabilitato nel template di tipo "html"

    Examples:
      | endpoint                              | fields                                                           |
      | notification-aar-for-email            | notification.sender.paDenomination                               |
      | notification-aar-for-email-digital    | notification.sender.paDenomination                               |
      | notification-aar-for-pec              | notification.subject,notification.sender.paDenomination          |
      | notification-cce-for-email            | denomination                                                     |
      | informal/email-communication-body      | subject,sender.denomination,sender.service,recipient.denomination |
      | informal/pec-communication-body        | subject,sender.denomination,sender.service,recipient.denomination |


  @templateEngineEscapeOff
  Scenario Outline: [TEMPLATE-ENGINE_HTML-ESCAPE_OFF_1_C] Verifica comportamento con HTML escaping disabilitato sugli endpoint TEXT
    When eseguo il fuzzing HTML escaping sull'endpoint "<endpoint>" in lingua "italiana" sui campi "<fields>"
    Then verifico che tutti i valori fuzzed siano rappresentati con HTML escaping disabilitato nel template di tipo "text"

    Examples:
      | endpoint                  | fields                                    |
      | informal/io-communication | sender.denomination,recipient.denomination |