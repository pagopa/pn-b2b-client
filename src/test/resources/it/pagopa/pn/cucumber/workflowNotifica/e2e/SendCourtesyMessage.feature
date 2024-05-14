Feature: Invio messaggi cortesia e2e
    @e2e
    Scenario: [E2E-SEND_COURTESY_MESSAGE_1] invio messaggio di cortesia - invio per email
        Given si predispone addressbook per l'utente "Galileo Galilei"
        And viene inserito un recapito legale "example@pecSuccess.it"
        And viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Galileo Galilei |
            | taxId | GLLGLL64B15G702I |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
            | details_recIndex | 0 |

    @e2e
    Scenario: [E2E-SEND_COURTESY_MESSAGE_2] invio messaggio di cortesia - invio per SMS
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Louis Armstrong |
            | taxId | RMSLSO31M04Z404R |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_digitalAddress | {"address": "+393214210000", "type": "SMS"} |
            | details_recIndex | 0 |

    @e2e @ignore
    Scenario: [E2E-SEND_COURTESY_MESSAGE_3] invio messaggio di cortesia - invio per AppIO
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Cristoforo Colombo |
            | taxId | CLMCST42R12D969Z |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
        Then si verifica la corretta acquisizione della notifica
        And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
            | loadTimeline | true |
            | details | NOT_NULL |
            | details_digitalAddress | {"address": "...", "type": "APPIO"} |
            | details_recIndex | 0 |

    @e2e  @ignore
    Scenario: [E2E-SEND-COURTESY-MESSAGE-4] Invio notifica mono destinatario con messaggio di cortesia non configurato
        Given viene generata una nuova notifica
            | subject | invio notifica con cucumber |
        And destinatario
            | denomination | Dino Sauro |
            | taxId | DSRDNI00A01A225I |
            | digitalDomicile | NULL |
        When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
        Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" non esista
            | NULL | NULL |