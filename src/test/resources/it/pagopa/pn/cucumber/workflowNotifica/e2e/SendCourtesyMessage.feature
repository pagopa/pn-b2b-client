Feature: Invio messaggi cortesia e2e
    @e2e @ignore
    Scenario: [E2E-SEND_COURTESY_MESSAGE_1] invio messaggio di cortesia - invio per email
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista e che abbia details
            | digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
            | recIndex | 0 |

    @e2e @ignore
    Scenario: [E2E-SEND_COURTESY_MESSAGE_2] invio messaggio di cortesia - invio per SMS
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Louis Amstrong |
            | taxId | RMSLSO31M04Z404R |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista e che abbia details
             | digitalAddress | {"address": "+393214210000", "type": "SMS"} |
             | recIndex | 0 |

    @e2e @ignore
    Scenario: [E2E-SEND_COURTESY_MESSAGE_3] invio messaggio di cortesia - invio per AppIO
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario Mario Cucumber
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista e che abbia details
             | digitalAddress | {"address": "...", "type": "APPIO"} |
             | recIndex | 0 |

    @e2e @ignore
      Scenario: [E2E-SEND-COURTESY-MESSAGE-4] Invio notifica mono destinatario con messaggio di cortesia non configurato
        Given viene generata una nuova notifica
          | subject | invio notifica con cucumber |
        And destinatario Dino De Sauro
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
        Then vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_COURTESY_MESSAGE"