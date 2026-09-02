@ioMock @router
Feature: Routing Trasparente e Pass-through verso App IO Reale
  Come connettore IO di SEND
  Voglio instradare le richieste con ID messaggio standard verso l'ambiente reale di App IO
  Per garantire la trasparenza del routing quando non è richiesto l'utilizzo del Mock

  @MOCK_IO_ROUTER_GET_04_1_A
  Scenario: [MOCK_IO_ROUTER_GET_04_1_A] Routing trasparente a IO reale per richiesta stato con ID standard privo di prefisso mock
    Given una richiesta di stato messaggio con identificativo standard privo di prefisso mock "01ARZ3NDEKTSV4RRFFQ69G5FAV"
    When viene richiesto lo stato del messaggio per il destinatario "RSSMRA80A01H5010"
    Then la richiesta viene instradata con successo verso l'ambiente reale di IO
