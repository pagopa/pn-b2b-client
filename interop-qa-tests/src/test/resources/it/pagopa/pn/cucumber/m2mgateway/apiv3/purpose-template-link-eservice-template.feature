@eServiceTemplateLink_m2mv3
Feature: finalità agevolata M2M, link e-service template a un template finalità

  ## Macro scenario: Recupero lista risorse collegabili suggerite per un template finalità

  Scenario: [LINK_TEMPLATE_ESERVICE_2_1] Recupero lista dei soli e-service template collegati per suggerire il template finalità
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |

    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Then recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |

  Scenario: [LINK_TEMPLATE_ESERVICE_2_2] Recupero lista dei soli e-service template collegati per suggerire il template finalità con paginazione
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvati gli e-service template in una lista di risorse di riferimento

    # Paginazione con offset
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita | $DA_CONTESTO(purposeTemplateId) |
      | offset               | 1  |
      | limit                | 10 |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento ignorando il primo risultato

    # Paginazione con limit
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita | $DA_CONTESTO(purposeTemplateId) |
      | offset               | 0  |
      | limit                | 1  |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento solo per il primo risultato

  Scenario: [LINK_TEMPLATE_ESERVICE_2_3] Recupero lista dei soli e-service template collegati per suggerire il template finalità con filtri
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA1"
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvati gli e-service template in una lista di risorse di riferimento
    And viene salvato 1 nome di riferimento dagli e-service template collegati
    And vengono salvati 2 ID creatore di riferimento dagli e-service template collegati

    # Filtro nome e-service template
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita           | $DA_CONTESTO(purposeTemplateId) |
      | filtro_e_service_template_name | $DA_CONTESTO(referenceEServiceTemplateName_1) |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento aventi:
      | nome_risorsa | $DA_CONTESTO(referenceEServiceTemplateName_1) |

    # Filtro con molteplici ID del pubblicatore di una risorsa
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId) |
      | filtro_id_pubblicatore | $DA_CONTESTO(referencePublisherId_1),$DA_CONTESTO(referencePublisherId_2) |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(referencePublisherId_1),$DA_CONTESTO(referencePublisherId_2) |

    # Filtro con singolo ID del pubblicatore di una risorsa
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)      |
      | filtro_id_pubblicatore | $DA_CONTESTO(referencePublisherId_1) |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(referencePublisherId_1) |
    And viene salvato 1 nome di riferimento dagli e-service template collegati

    # Filtro doppio con ID del pubblicatore e nome di una risorsa
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita           | $DA_CONTESTO(purposeTemplateId) |
      | filtro_e_service_template_name | $DA_CONTESTO(referenceEServiceTemplateName_1) |
      | filtro_id_pubblicatore         | $DA_CONTESTO(referencePublisherId_1) |
    Then gli e-service template collegati corrispondono alla lista di risorse di riferimento aventi:
      | nome_risorsa    | $DA_CONTESTO(referenceEServiceTemplateName_1) |
      | id_pubblicatore | $DA_CONTESTO(referencePublisherId_1) |

    # Filtro con risultato lista vuota
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)      |
      | filtro_id_pubblicatore | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
    Then gli e-service template collegati corrispondono ad una lista vuota

  Scenario: [LINK_TEMPLATE_ESERVICE_2_4] Recupero lista e-service template collegati ad un template finalità non esistente
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
    Then la richiesta restituisce errore di template finalità non trovato

  ## Macro scenario: Associazione di una risorsa collegabile ad un template finalità

  Scenario: [LINK_TEMPLATE_ESERVICE_4_1] Associazione di un singolo e-service template ad un template finalità con successo
    Given l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    # Stato in bozza (DRAFT) per il template finalità
    When viene creato un nuovo purpose template

    ## Associazione con un e-service template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa un e-service template a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |

    # Stato attivo (PUBLISHED) per il template finalità
    When il purpose template viene gradualmente spostato in stato PUBLISHED

    ## Associazione con un e-service template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa un e-service template a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |

  Scenario: [LINK_TEMPLATE_ESERVICE_4_2] Errore di conflitto durante l’associazione di un singolo e-service template ad un template finalità
    Il test verifica che venga generato un errore di conflitto quando si associa un e-service template già collegato
    al template finalità.

    Given l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene creato un nuovo purpose template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa un e-service template a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    When prova ad associare un e-service template a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then la richiesta di associazione fallisce per errore di conflitto

  Scenario: [LINK_TEMPLATE_ESERVICE_4_3] Template finalità non trovato durante l'associazione di un singolo e-service template ad un template finalità
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When prova ad associare un e-service template a un template finalità
      | id_template_finalita  | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | id_e_service_template | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta restituisce errore di template finalità non trovato

  ## Macro scenario: Disassociazione di una risorsa collegata da un template finalità

  Scenario: [LINK_TEMPLATE_ESERVICE_6_1] Disassociazione di un singolo e-service template da un template finalità con successo
    Given l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin

    # Stato in bozza (DRAFT) per il template finalità
    And viene creato un nuovo purpose template

    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera gli e-service template collegati per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvati gli e-service template in una lista di risorse di riferimento
    And vengono salvati 2 ID e-service template di riferimento dagli e-service template collegati

    ## Disassociazione di un e-service template
    When disassocia un e-service template da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service_template | $DA_CONTESTO(referenceEServiceTemplateId_1) |

    # Stato attivo (PUBLISHED) per il template finalità
    And il purpose template viene gradualmente spostato in stato PUBLISHED

    ## Disassociazione di un e-service template
    And disassocia un e-service template da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service_template | $DA_CONTESTO(referenceEServiceTemplateId_2) |

    Then gli e-service template collegati al template finalità sono una lista vuota
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |

  Scenario: [LINK_TEMPLATE_ESERVICE_6_2] Errore di conflitto durante la disassociazione di un singolo e-service template già scollegato da un template finalità
    Given l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    Given viene creato un nuovo purpose template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa un e-service template a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And disassocia un e-service template da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |

    When prova a disassociare un e-service template da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then la richiesta di disassociazione fallisce per errore di conflitto

  Scenario: [LINK_TEMPLATE_ESERVICE_6_3] Template finalità non trovato durante la disassociazione di un singolo e-service template da un template finalità
    Given l'utente è un "admin" di "PA1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When prova a disassociare una risorsa da un template finalità
      | id_template_finalita  | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | id_e_service_template | eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab |
    Then la richiesta restituisce errore di template finalità non trovato
