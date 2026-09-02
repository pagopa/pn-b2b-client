Feature: Ricerca dei recapiti digitali per una notifica bonaria.


# ***********************************************
# **** 1 - Invio di una notifica bonaria tramite canali digitali PEC
# ***********************************************


 #pec piattaforma con e senza speciale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_1_A] Come ente mittente invio una notifica bonaria verso PF specificando una pec speciale e il servizio utilizzerà quella di piattaforma

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_1_A] Come ente mittente invio una notifica bonaria verso PG specificando una pec speciale e il servizio utilizzerà quella di piattaforma

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_1_B] Come ente mittente invio una notifica bonaria verso PF NON specificando una pec speciale e il servizio utilizzerà quella di piattaforma

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_1_B] Come ente mittente invio una notifica bonaria verso PG NON specificando una pec speciale e il servizio utilizzerà quella di piattaforma


#ok registri naz

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_2] Come ente mittente invio una notifica bonaria verso PF senza pec speciale ne di piattaforma, il serizio utilizzerà quella generale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_2] Come ente mittente invio una notifica bonaria verso PG senza pec speciale ne di piattaforma, il serizio utilizzerà quella generale

#no registri

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_3] Come ente mittente invio una notifica bonaria verso PF senza pec speciale ne di piattaforma, il serizio cerchera la generale con esito negativo, il canale pec sarà saltato

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_3] Come ente mittente invio una notifica bonaria verso PG senza pec speciale ne di piattaforma, il serizio cerchera la generale con esito negativo, il canale pec sarà saltato


#sercQ

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_4] Come ente mittente invio una notifica bonaria verso PF con pec speciale e il servizio utilizzerà sercQ

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_4] Come ente mittente invio una notifica bonaria verso PG con pec speciale e il servizio utilizzerà sercQ



#solo speciale


  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_5] Come ente mittente invio una notifica bonaria verso PF con solo pec speciale e il servizio utilizzerà la pec speciale


  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_5] Come ente mittente invio una notifica bonaria verso PG con solo pec speciale e il servizio utilizzerà la pec speciale




# ***********************************************
# **** 2 - Invio del messaggio di cortesia a seguito di utilizzo del canale digitale SercQ
# ***********************************************

 #SMS ON

  @informalNotificationsSearchDigitalAddress @informalNotificSmsON #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_1_A] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia email ed SMS

  @informalNotificationsSearchDigitalAddress @informalNotificSmsON #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_1_A] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia email ed SMS

  @informalNotificationsSearchDigitalAddress @informalNotificSmsON
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_1_B] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email

  @informalNotificationsSearchDigitalAddress @informalNotificSmsON
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_1_B] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email


 #SMS OFF

  @informalNotificationsSearchDigitalAddress @informalNotificSmsOFF #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_2_A] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia solo su email

  @informalNotificationsSearchDigitalAddress @informalNotificSmsOFF #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_2_A] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia solo su email

  @informalNotificationsSearchDigitalAddress @informalNotificSmsOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_2_B] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email

  @informalNotificationsSearchDigitalAddress @informalNotificSmsOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_2_B] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email



# ***********************************************
# **** 5 - Nuova versione dei TOS per attivazione ricerca recapito digitale per le notifiche bonarie
# ***********************************************

 #speciale+piattaforma=speciale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_A] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF con pec speciale e pec di piattaforma, il servizio utilizzerà la pec speciale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_A] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG con pec speciale e pec di piattaforma, il servizio utilizzerà la pec speciale


#!speciale-piattaforma=skip

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_B] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF SENZA pec speciale e pec di piattaforma, il servizio skippa il canale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_B] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG SENZA pec speciale e pec di piattaforma, il servizio skippa il canale


#!speciale+sercQ=skip

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_C] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF SENZA pec speciale e sercq attivo, il servizio skippa il canale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_C] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG SENZA pec speciale e sercq attivo, il servizio skippa il canale


 #speciale+sercq=speciale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_D] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF con sercq attivo e pec di speciale, il servizio utilizzerà la pec speciale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_D] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG con sercq attivo e pec di speciale, il servizio utilizzerà la pec speciale


 #!speciale+!piatt=skip

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_E] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF senza pec speciale ne recapiti di piattaforma, il servizio skippa il canale

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_E] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG senza pec speciale ne recapiti di piattaforma, il servizio skippa il canale





# ***********************************************
# **** 6 - Invio di una notifica bonaria tramite canale Email
# ***********************************************



  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_A] Come ente mittente invio una notifica bonaria verso PF SENZA email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_A] Come ente mittente invio una notifica bonaria verso PG SENZA email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.


  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA email speciale e SENZA email di piattaforma. Il servizio skippa il canale email.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA email speciale e SENZA email di piattaforma. Il servizio skippa il canale email.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_C] Come ente mittente invio una notifica bonaria verso PF CON email speciale e SENZA email di piattaforma. Il servizio utilizza l'email speciale.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_C] Come ente mittente invio una notifica bonaria verso PG CON email speciale e SENZA email di piattaforma. Il servizio utilizza l'email speciale.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_D] Come ente mittente invio una notifica bonaria verso PF CON email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_D] Come ente mittente invio una notifica bonaria verso PG CON email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.




# ***********************************************
# **** 7 - Invio di una notifica bonaria tramite canale SMS
# ***********************************************

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_A] Come ente mittente invio una notifica bonaria verso PF SENZA n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_A] Come ente mittente invio una notifica bonaria verso PG SENZA n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio skippa il canale sms.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio skippa il canale sms.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_C] Come ente mittente invio una notifica bonaria verso PF CON n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio utilizza il nimero speciale.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_C] Come ente mittente invio una notifica bonaria verso PG CON n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio utilizza il numero speciale.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_D] Come ente mittente invio una notifica bonaria verso PF CON n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_D] Come ente mittente invio una notifica bonaria verso PG CON n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.


# ***********************************************
# **** 8 - Workflow di una notifica bonaria inviata tramite SercQ
# ***********************************************

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_08_1_A] Come ente mittente invio una notifica bonaria verso PF con sercQ attivo. A seguito di un successo di invio si controlla la correttezza della timeline.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_08_1_A] Come ente mittente invio una notifica bonaria verso PG con sercQ attivo. A seguito di un successo di invio si controlla la correttezza della timeline.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_08_1_B] Come ente mittente invio una notifica bonaria verso PF con sercQ attivo. A seguito di un ko sull'invio si controlla la correttezza della timeline.

  @informalNotificationsSearchDigitalAddress
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_08_1_B] Come ente mittente invio una notifica bonaria verso PG con sercQ attivo. A seguito di un ko sull'invio si controlla la correttezza della timeline.







# ***********************************************
# **** Verifica del Comportamento del servizio con Feature-flag: OFF
# ***********************************************

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_A] Come ente mittente invio una notifica bonaria verso PF CON PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio utilizza la pec speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_A] Come ente mittente invio una notifica bonaria verso PG CON PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio utilizza la pec speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio non validerà la notifica.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio non validerà la notifica.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_C] Come ente mittente invio una notifica bonaria verso PF SENZA PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_C] Come ente mittente invio una notifica bonaria verso PG SENZA PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_D] Come ente mittente invio una notifica bonaria verso PF CON PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_D] Come ente mittente invio una notifica bonaria verso PG CON PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.






  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_A] Come ente mittente invio una notifica bonaria verso PF SENZA EMAIL speciale e CON EMAIL di piattaforma. Il servizio skippa il canale email

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_A] Come ente mittente invio una notifica bonaria verso PG SENZA EMAIL speciale e CON EMAIL di piattaforma. Il servizio skippa il canale email

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_B] Come ente mittente invio una notifica bonaria verso PF SENZA EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio skippa il canale email

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_B] Come ente mittente invio una notifica bonaria verso PG SENZA EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio skippa il canale email

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_C] Come ente mittente invio una notifica bonaria verso PF CON EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio utilizza l'email speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_C] Come ente mittente invio una notifica bonaria verso PG CON EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio utilizza l'email speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_C] Come ente mittente invio una notifica bonaria verso PF CON EMAIL speciale e CON EMAIL di piattaforma. Il servizio utilizza l'email speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_C] Come ente mittente invio una notifica bonaria verso PG CON EMAIL speciale e CON EMAIL di piattaforma. Il servizio utilizza l'email speciale.



  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_A] Come ente mittente invio una notifica bonaria verso PF SENZA SMS speciale e CON SMS di piattaforma. Il servizio skippa il canale SMS

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_A] Come ente mittente invio una notifica bonaria verso PG SENZA SMS speciale e CON SMS di piattaforma. Il servizio skippa il canale SMS

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_B] Come ente mittente invio una notifica bonaria verso PF SENZA SMS speciale e SENZA SMS di piattaforma. Il servizio skippa il canale SMS

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_B] Come ente mittente invio una notifica bonaria verso PG SENZA SMS speciale e SENZA SMS di piattaforma. Il servizio skippa il canale SMS

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_C] Come ente mittente invio una notifica bonaria verso PF CON SMS speciale e SENZA SMS di piattaforma. Il servizio utilizza l'SMS speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_C] Come ente mittente invio una notifica bonaria verso PG CON SMS speciale e SENZA SMS di piattaforma. Il servizio utilizza l'SMS speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_C] Come ente mittente invio una notifica bonaria verso PF CON SMS speciale e CON SMS di piattaforma. Il servizio utilizza l'SMS speciale.

  @informalNotificationsSearchDigitalAddress @
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_C] Come ente mittente invio una notifica bonaria verso PG CON SMS speciale e CON SMS di piattaforma. Il servizio utilizza l'SMS speciale.