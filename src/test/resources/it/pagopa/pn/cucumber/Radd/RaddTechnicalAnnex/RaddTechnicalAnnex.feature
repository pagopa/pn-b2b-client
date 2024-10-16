Feature: Adeguamento RADD alle modifiche dell’allegato tecnico - Stampa degli atti

  #PRINT ACT

#  #capire come configurare/riprendere caso in cui MAX-Print request ha valore definito == 0
#  scenario non coperto da AT
#
# @raddAlt @zip
#  Scenario: [ADEG-RADD-PRINT_ACTS-1] PF - Stampa illimitata di documenti disponibili associati a QR code esistente con CF corretto
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative  |
#      | senderDenomination | Comune di Palermo           |
#    And destinatario Mario Cucumber
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
#    When L'operatore scansiona il qrCode e stampa gli atti per 2 volte senza errori
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "DELIVERED"
#    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
#    And lato desinatario "Mario Cucumber" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  #RADD-ALT_ACT-75
  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-PRINT_ACTS-2] PF - Stampa limitata di documenti disponibili associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansiona il qrCode e stampa gli atti per il numero di volte consentito
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    And lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "Mario Cucumber" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-PRINT_ACTS-3] PF - Restituzione errore - Stampa limitata di documenti disponibili associati con raggiungimento limite raggiunto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansiona il qrCode e stampa gli atti per il numero di volte consentito
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then Viene restituito un messaggio di errore "Limite di 10 stampe superato" con codice di errore 3 su radd alternative
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"

#OPERATORE UPLOADER

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_AOR-1] Operatore RADD_UPLOADER - Start di una AOR transaction con fileKey presente - ricezione OK
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisicagiuridica su radd alternative da operatore radd "UPLOADER"
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_AOR-2] Operatore RADD_UPLOADER - Start di una AOR transaction con fileKey null o empty - ricezione Errore
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    When tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd "UPLOADER" senza successo con file key "<fileKey>"
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo fileKey obbligatorio mancante"
    Examples:
    | fileKey |
    | null    |
    |         |

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_ACT-1] PF - Operatore RADD_UPLOADER - Start di una ACT transaction con fileKey presente - ricezione OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "UPLOADER" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative per operatore "UPLOADER"
    And l'operazione di download degli atti si conclude correttamente su radd alternative

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_ACT-2] Operatore RADD_UPLOADER - Start di una ACT transaction senza fileKey presente - ricezione Errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "UPLOADER" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    When tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore "UPLOADER" senza successo con file key "<fileKey>"
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo fileKey obbligatorio mancante"
    Examples:
      | fileKey |
      | null    |
      |         |

  # OPERATORE STANDARD / Senza ruolo

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_AOR-3] Operatore RADD_STANDARD / senza Ruolo - Start di una AOR transaction senza fileKey presente - ricezione OK
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "<operatorType>" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisicagiuridica su radd alternative da operatore radd "<operatorType>" con file key "<fileKey>"
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    Examples:
      | operatorType | fileKey |
      | STANDARD     | null    |
      | WITHOUT_ROLE |         |

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_AOR-4] Operatore RADD_STANDARD / senza ruolo - Start di una AOR transaction con fileKey presente - ricezione Errore
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "<operatorType>" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative per errore
    When tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd "<operatorType>" senza successo
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo fileKey inaspettato"
    Examples:
      | operatorType |
      | STANDARD     |
      | WITHOUT_ROLE |

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_ACT-3] Operatore RADD_STANDARD / senza ruolo - Start di una ACT transaction senza fileKey presente - ricezione OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "<operatorType>" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative per operatore "<operatorType>" con fileKey "<fileKey>"
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Examples:
      | operatorType | fileKey |
      | STANDARD     |  null   |
      | WITHOUT_ROLE |         |

  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_ACT-4] Operatore RADD_STANDARD - Start di una ACT transaction con fileKey presente - ricezione Errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "<operatorType>" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative per errore
    When tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore "<operatorType>" senza successo
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo fileKey inaspettato"
    Examples:
      | operatorType |
      | STANDARD     |
      | WITHOUT_ROLE |

  #UPLOAD ERROR
  @raddTechnicalAnnex
  Scenario Outline: [ADEG-RADD-TRANS_ACT-5] Operatore senza ruolo / standard - Tentativo di eseguire documentUpload - ricezione Errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "<operatorType>" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And l'operatore "<operatorType>" tenta di caricare i documento di identità del cittadino su radd alternative senza successo
    And il caricamente ha prodotto une errore http 403 su radd alternative
    Examples:
    | operatorType |
    | WITHOUT_ROLE |
    | STANDARD     |

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_AOR-5] Operatore RADD_STANDARD - Start di una AOR transaction con versionToken presente - ricezione Error
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "STANDARD" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    Then tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd "STANDARD" con versionToken errato
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo versionToken inaspettato"

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_AOR-6] Operatore RADD_UPLOADER - Start di una AOR transaction senza versionToken presente - ricezione Error
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    Then tentativo di recuperare gli aar delle notifiche in stato irreperibile da operatore radd "UPLOADER" con versionToken errato
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo versionToken obbligatorio mancante"

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_ACT-6] PF - Operatore RADD_UPLOADER - Start di una ACT transaction senza version token presente - ricezione OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "UPLOADER" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative dall'operatore RADD "UPLOADER"
    When tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore "UPLOADER" con versionToken errato
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo versionToken obbligatorio mancante"

  @raddTechnicalAnnex
  Scenario: [ADEG-RADD-TRANS_ACT-7] PF - Operatore RADD_STANDARD - Start di una ACT transaction senza version token presente - ricezione OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore "STANDARD" scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    When tentativo di recuperare gli atti delle notifiche associata all'AAR da radd alternative per operatore "STANDARD" con versionToken errato
    And il tentativo genera un errore 400 "Bad Request" con il messaggio "Campo versionToken inaspettato"
