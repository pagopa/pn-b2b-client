Feature: File feature unicamente per sperimentazione tecnica; non implementa alcuna logica di testing reale
  Scenario: Check status code variabile per set di api
    And si ottengono i seguenti status codes: 200 per BFF V1
    And si ottengono i seguenti status codes: 200 per BFF V1, 300 per M2M V2, 400 per M2M V3