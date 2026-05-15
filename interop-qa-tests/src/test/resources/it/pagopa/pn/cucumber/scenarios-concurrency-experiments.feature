# Test utili solo per verificare i meccanismi di gestione della concorrenza, non hanno alcun legame coi test di dominio

@concurrency-exp
Feature: File feature unicamente per sperimentazione tecnica; non implementa alcuna logica di testing reale

  @concurrency-exp_vincolato2
  @concurrency-exp_vincolato
  Scenario: scenario vincolato entrambi gruppi
    And gira scenario vincolato 2

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 1 gruppo 2
    And gira scenario vincolato 2

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 2 gruppo 2
    And gira scenario vincolato 2

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 3 gruppo 2
    And gira scenario vincolato 2

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 4 gruppo 2
    And gira scenario vincolato 2

  Scenario: scenario libero 1
    And gira scenario libero

  Scenario: scenario libero 2
    And gira scenario libero

  Scenario: scenario libero 3
    And gira scenario libero

  Scenario: scenario libero 4
    And gira scenario libero

  Scenario: scenario libero 5
    And gira scenario libero

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 5 gruppo 2
    And gira scenario vincolato 2

  @concurrency-exp_vincolato2
  Scenario: scenario vincolato 6 gruppo 2
    And gira scenario vincolato 2

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 1
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 2
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 3
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 4
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 5
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 6
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 7
    And gira scenario vincolato

  @concurrency-exp_vincolato
  Scenario: scenario vincolato 8
    And gira scenario vincolato
