Feature: Radd Alternative integrazione con Poste



#  /radd-net/api/v1/download/{operationType}/{operationId}:
And L'operatore esegue il download del frontespizio del operazione "act"
And L'operatore esegue il download del frontespizio del operazione "aor"


#  /radd-net/api/v1/act/inquiry: description:
When L'operatore scansione il qrCode per recuperare gli atti di <CITIZEN>
When L'operatore usa lo IUN "corretto" per recuperare gli atti di Mario Cucumber


#  /radd-net/api/v1/act/transaction/start:
And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative


#  /radd-net/api/v1/act/transaction/complete:
And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative


#  /radd-net/api/v1/act/transaction/abort:
And la transazione viene abortita per gli "act"


#  /radd-net/api/v1/aor/inquiry: description:  ***chiede di verificare la presenza di notifiche
And la persona giuridica Gherkin Irreperibile chiede di verificare la presenza di notifiche
And la persona fisica Gherkin Irreperibile chiede di verificare la presenza di notifiche
And la persona fisica Signor Casuale chiede di verificare ad operatore radd "UPLOADER" la presenza di notifiche


#  /radd-net/api/v1/aor/transaction/start:
Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd alternative


#  /radd-net/api/v1/aor/transaction/complete: description:
And viene chiusa la transazione per il recupero degli aar su radd alternative


#  /radd-net/api/v1/aor/transaction/abort: description:
And la transazione viene abortita per gli "aor"
