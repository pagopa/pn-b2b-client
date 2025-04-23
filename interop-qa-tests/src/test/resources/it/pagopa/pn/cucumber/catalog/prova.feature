@descriptor_activation
Feature: Attivazione di un descrittore
  Tutti gli utenti autorizzati di enti erogatori possono attivare un descrittore in stato SUSPENDED

  @prova_parallel
  Scenario: Per un e-service che ha un solo descrittore, il quale è in stato SUSPENDED, all'attivazione del descrittore, torna allo stato PUBLISHED
    Given il pacchetto risulta correttamente formattato
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "CIAO"

  @prova_parallel
  Scenario: Per un e-service che ha un solo descrittore, il quale non si trova in stato SUSPENDED, alla riattivazione del descrittore, si ottiene un errore
    Given il pacchetto risulta correttamente formattato
    Given l'utente ha già un pacchetto correttamente strutturato con un eservice in mode "CIAO"

