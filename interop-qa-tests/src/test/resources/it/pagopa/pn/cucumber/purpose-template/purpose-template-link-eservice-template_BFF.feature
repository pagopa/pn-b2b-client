Feature: finalità agevolata BFF, link e-service template a un template finalità

  Background:
    Given l'utente è un "admin" di "PA1"
#    And viene creato un nuovo purpose template
#    And il purpose template viene gradualmente spostato in stato PUBLISHED
    # Altri step di preparazione oppure:
    # probabilmente conviene avere un purpose template dedicato da utilizzare

  ## Macro scenario: Recupero lista risorse collegabili suggerite per un template finalità

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_1_1] Recupero lista combinata e-service concreti ed e-service template suggeriti per un template finalità
    # Given: fb61e42b-6a72-4146-bcc2-2ae6d5d2e1b0 presenta sia e-service che e-service template collegati
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | fb61e42b-6a72-4146-bcc2-2ae6d5d2e1b0 |
      | offset              | 0  |
      | limit               | 50 |
    Then le risorse collegabili presentano un e-service concreto
    And le risorse collegabili presentano un e-service template

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_1_2] Recupero lista combinata e-service concreti e template suggeriti per un template finalità con paginazione
    Given recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset              | 0  |
      | limit               | 50 |
    And vengono salvate le risorse collegabili in una lista di risorse di riferimento

    # Paginazione con offset
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset              | 1  |
      | limit               | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento ignorando il primo risultato

    # Paginazione con limit
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset              | 0  |
      | limit               | 2  |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento solo per i primi 2 risultati

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_1_3] Recupero lista combinata e-service concreti e template suggeriti per un template finalità con filtri
    Given recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset              | 0  |
      | limit               | 50 |
    And vengono salvate le risorse collegabili in una lista di risorse di riferimento
    And viene salvato 1 nome e-service template di riferimento dalle risorse collegabili
    And viene salvato 1 nome e-service concreto di riferimento dalle risorse collegabili
    And vengono salvati 2 ID pubblicatore di riferimento dalle risorse collegabili

    # Filtro nome e-service template
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | fb346d06-16e6-4481-8df2-f3fc590933a8             |
      | filtro_nome_eservice | $DA_CONTESTO(nome_eservice_template_riferimento) |
      | offset               | 0  |
      | limit                | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | nome_eservice        | $DA_CONTESTO(nome_eservice_template_riferimento) |

    # Filtro nome e-service concreto
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | fb346d06-16e6-4481-8df2-f3fc590933a8             |
      | filtro_nome_eservice | $DA_CONTESTO(nome_eservice_concreto_riferimento) |
      | offset               | 0  |
      | limit                | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | nome_eservice        | $DA_CONTESTO(nome_eservice_concreto_riferimento) |

    # Filtro parte del nome di una risorsa
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | filtro_nome_eservice | e-service |
      | offset               | 0  |
      | limit                | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | parte_del_nome       | e-service |

    # Filtro con molteplici ID del pubblicatore di una risorsa
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id    | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | filtro_id_pubblicatore | $DA_CONTESTO(id_pubblicatore_riferimento_1),$DA_CONTESTO(id_pubblicatore_riferimento_2) |
      | offset                 | 0  |
      | limit                  | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(id_pubblicatore_riferimento_1),$DA_CONTESTO(id_pubblicatore_riferimento_2) |

    # Filtro con singolo ID del pubblicatore di una risorsa
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id    | fb346d06-16e6-4481-8df2-f3fc590933a8    |
      | filtro_id_pubblicatore | $DA_CONTESTO(id_pubblicatore_riferimento)   |
      | offset                 | 0  |
      | limit                  | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(id_pubblicatore_riferimento) |
    And viene salvato 1 nome risorsa di riferimento dalle risorse collegabili

    # Filtro doppio con ID del pubblicatore e nome di una risorsa
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id    | fb346d06-16e6-4481-8df2-f3fc590933a8   |
      | filtro_nome_eservice   | $DA_CONTESTO(nome_risorsa_riferimento) |
      | filtro_id_pubblicatore | $DA_CONTESTO(id_pubblicatore_riferimento)  |
      | offset                 | 0  |
      | limit                  | 50 |
    Then le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:
      | nome_eservice          | $DA_CONTESTO(nome_risorsa_riferimento) |
      | id_pubblicatore        | $DA_CONTESTO(id_pubblicatore_riferimento)  |

    # Filtro con risultato lista vuota
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id    | fb346d06-16e6-4481-8df2-f3fc590933a8    |
      | filtro_id_pubblicatore | aaaabbbb-cccc-1111-2222-ddddeeee3333    |
      | offset                 | 0  |
      | limit                  | 50 |
    Then le risorse collegabili corrispondono ad una lista vuota

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_1_4] Recupero lista combinata e-service concreti e template suggeriti da un template finalità non esistente
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | offset              | 0  |
      | limit               | 50 |
    Then la richiesta di risorse collegabili restituisce errore di template finalità non trovato

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_1_5] Errori formali nella richiesta della lista combinata e-service concreti e template suggeriti per un template finalità
    # Formato non valido per purpose template ID
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | xyz |
      | offset               | 0   |
      | limit                | 2   |
    Then le risorse collegabili non vengono fornite causa richiesta non valida

    # Parametro limit assente
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset               | 0   |
    Then le risorse collegabili non vengono fornite causa richiesta non valida

    # Parametro offset assente
    When recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id  | fb346d06-16e6-4481-8df2-f3fc590933a8 |
      | limit                | 2   |
    Then le risorse collegabili non vengono fornite causa richiesta non valida

  ## Macro scenario: Associazione di una risorsa collegabile ad un template finalità

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_3_1] Associazione di un singolo e-service template ad un template finalità con successo
    # Given: serve partire con un nuovo purpose template
    Given recupera le risorse collegabili suggerite per un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | offset              | 0  |
      | limit               | 50 |
    And viene salvato 1 ID e-service template di riferimento dalle risorse collegabili
    And viene salvato 1 ID e-service concreto di riferimento dalle risorse collegabili

    # Associazione e-service template suggerito
    When associa una risorsa ad un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | $DA_CONTESTO(id_eservice_template_riferimento) |
    And recupera le risorse collegate ad un template finalità
    Then le risorse collegate includono:
      | risorsa_id |
      | $DA_CONTESTO(id_eservice_template_riferimento) |

    # Associazione e-service concreto suggerito
    When associa una risorsa ad un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | $DA_CONTESTO(id_eservice_concreto_riferimento) |
    And recupera le risorse collegate ad un template finalità
    Then le risorse collegate includono:
      | risorsa_id |
      | $DA_CONTESTO(id_eservice_concreto_riferimento) |

    # Associazione risorsa non suggerita
    When associa una risorsa ad un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | ee556d06-16e6-4481-8df2-f3fc590977bb |
    And recupera le risorse collegate ad un template finalità
    Then le risorse collegate includono:
      | risorsa_id |
      | ee556d06-16e6-4481-8df2-f3fc590977bb |

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_3_2] Errore di conflitto durante l'associazione di un singolo e-service template ad un template finalità
    # Associazione e-service concreto che sia istanza di un e-service template
    When associa una risorsa ad un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta di associazione restituisce errore di conflitto

    # Associazione con e-service non esistente
    When associa una risorsa ad un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
    Then la richiesta di associazione restituisce errore di conflitto

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_3_3] Template finalità non trovato durante l'associazione di un singolo e-service template ad un template finalità
    When associa una risorsa ad un template finalità
      | purpose_template_id | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | risorsa_id          | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta di associazione restituisce errore di template finalità non trovato

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_3_4] Errori formali nell'associazione di un singolo e-service template ad un template finalità
    # Formato ID template finalità non valido
    When associa una risorsa ad un template finalità
      | purpose_template_id | xyz |
      | risorsa_id          | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta di associazione restituisce errore di richiesta non valida

    # Campo ID risorsa errato
    When associa una risorsa ad un template finalità con nome campo della risorsa errato
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
    Then la richiesta di associazione restituisce errore di richiesta non valida

  ## Macro scenario: Disassociazione di una risorsa collegata da un template finalità

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_5_1] Disassociazione di un singolo e-service template da un template finalità con successo
    #Given Preparare un template finalità con risorse associate da poter disassociare
    #Given Potrebbero esserci delle risorse note sempre associabile:
    # b01961b9-01ac-4e68-9fc1-b8071562fe55 (e-service)
    # eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab (e-service template)
    # 2bb6048c-dbdf-4ec3-b313-41f01c14e894 (altro e-service)

    # Disassociazione e-service template suggerito
    When disassocia una risorsa da un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab |
    And recupera le risorse collegate ad un template finalità
    Then le risorse collegate includono:
      | risorsa_id |
      | b01961b9-01ac-4e68-9fc1-b8071562fe55 |
      | 2bb6048c-dbdf-4ec3-b313-41f01c14e894 |
    And le risorse collegate non includono:
      | risorsa_id |
      | eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab |

    # Disassociazione e-service concreto suggerito
    When disassocia una risorsa da un template finalità
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
      | risorsa_id          | b01961b9-01ac-4e68-9fc1-b8071562fe55 |
    And recupera le risorse collegate ad un template finalità
    Then le risorse collegate includono:
      | risorsa_id |
      | 2bb6048c-dbdf-4ec3-b313-41f01c14e894 |
    And le risorse collegate non includono:
      | risorsa_id |
      | b01961b9-01ac-4e68-9fc1-b8071562fe55 |

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_5_2] Template finalità non trovato durante la disassociazione di un singolo e-service template da un template finalità
    When disassocia una risorsa da un template finalità
      | purpose_template_id | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | risorsa_id          | eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab |
    Then la richiesta di disassociazione restituisce errore di template finalità non trovato

  @purposeTemplate @eServiceTemplateLink
  Scenario: [LINK_TEMPLATE_ESERVICE_5_3] Errori formali nella disassociazione di un singolo e-service template da un template finalità
    # Formato ID template finalità non valido
    When disassocia una risorsa da un template finalità
      | purpose_template_id | xyz |
      | risorsa_id          | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta di disassociazione restituisce errore di richiesta non valida

    # Campo ID risorsa errato
    When disassocia una risorsa da un template finalità con nome campo della risorsa errato
      | purpose_template_id | bb346d06-16e6-4481-8df2-f3fc590933a8 |
    Then la richiesta di disassociazione restituisce errore di richiesta non valida
