Feature: Service Desk Refinement

@ServiceDeskRefinement
Scenario: [SERVICE_DESK_TIMELINE_REFINEMENT_1] verifica presenza elemento REFINEMENT nella response di service desk
  Given viene generata una nuova notifica
    | subject | invio notifica con cucumber |
    | senderDenomination | Comune di milano |
  And destinatario Mario Cucumber
  And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
  Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
  And viene chiamato service desk e si controlla la presenza dell'elemento "REFINEMENT" nella response