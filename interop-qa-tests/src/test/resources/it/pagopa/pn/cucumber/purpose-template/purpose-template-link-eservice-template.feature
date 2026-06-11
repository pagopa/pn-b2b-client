Feature: finalità agevolata BFF, link e-service template a un template finalità

  Background:
    Given l'utente è un "admin" di "PA1"

  ## Macro scenario: Recupero lista risorse collegate per suggerire il template finalità

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_1_1] Recupero lista combinata e-service concreti ed e-service template collegati per suggerire il template finalità
    Given viene creato un nuovo purpose template
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |

    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    Then le risorse recuperate presentano un e-service concreto
    And le risorse recuperate presentano un e-service template

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_1_2] Recupero lista combinata e-service concreti e template collegati con paginazione
    Given viene creato un nuovo purpose template
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvate le risorse collegate in una lista di risorse di riferimento

    # Paginazione con offset
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita | $DA_CONTESTO(purposeTemplateId) |
      | offset               | 1  |
      | limit                | 10 |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento ignorando il primo risultato

    # Paginazione con limit
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita | $DA_CONTESTO(purposeTemplateId) |
      | offset               | 0  |
      | limit                | 2  |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento solo per i primi 2 risultati

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_1_3] Recupero lista combinata e-service concreti e template collegati con filtri
    Given viene creato un nuovo purpose template
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And "PA2" ha già creato e pubblicato 1 e-service
    And l'utente è un "admin" di "PA1"
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA1"
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvate le risorse collegate in una lista di risorse di riferimento
    And viene salvato 1 nome e-service template di riferimento dalle risorse collegate
    And viene salvato 1 nome e-service concreto di riferimento dalle risorse collegate
    And vengono salvati 2 ID pubblicatore di riferimento dalle risorse collegate

    # Filtro nome e-service template
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)               |
      | filtro_nome_e_service | $DA_CONTESTO(referenceEServiceTemplateName_1) |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | nome_risorsa          | $DA_CONTESTO(referenceEServiceTemplateName_1) |

    # Filtro nome e-service concreto
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)       |
      | filtro_nome_e_service | $DA_CONTESTO(referenceEServiceName_1) |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | nome_risorsa          | $DA_CONTESTO(referenceEServiceName_1) |

    # Filtro parte del nome di una risorsa
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)       |
      | filtro_nome_e_service | eservice |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | parte_del_nome        | eservice |

    # Filtro con molteplici ID del pubblicatore di una risorsa
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)      |
      | filtro_id_pubblicatore | $DA_CONTESTO(referencePublisherId_1),$DA_CONTESTO(referencePublisherId_2) |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(referencePublisherId_1),$DA_CONTESTO(referencePublisherId_2) |

    # Filtro con singolo ID del pubblicatore di una risorsa
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)       |
      | filtro_id_pubblicatore | $DA_CONTESTO(referencePublisherId_1)  |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | id_pubblicatore        | $DA_CONTESTO(referencePublisherId_1)  |
    And viene salvato 1 nome risorsa di riferimento dalle risorse collegate

    # Filtro doppio con ID del pubblicatore e nome di una risorsa
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)       |
      | filtro_nome_e_service  | $DA_CONTESTO(referenceResourceName_1) |
      | filtro_id_pubblicatore | $DA_CONTESTO(referencePublisherId_1)  |
    Then le risorse collegate corrispondono alla lista di risorse di riferimento aventi:
      | nome_risorsa           | $DA_CONTESTO(referenceResourceName_1) |
      | id_pubblicatore        | $DA_CONTESTO(referencePublisherId_1)  |

    # Filtro con risultato lista vuota
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita   | $DA_CONTESTO(purposeTemplateId)      |
      | filtro_id_pubblicatore | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
    Then le risorse collegate corrispondono ad una lista vuota

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_1_4] Recupero lista combinata e-service concreti e template collegati ad un template finalità non esistente
    When recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
    Then la richiesta restituisce errore di template finalità non trovato

  ## Macro scenario: Associazione di una risorsa collegabile ad un template finalità

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_3_1] Associazione di un singolo e-service template ad un template finalità con successo

    # Stato in bozza (DRAFT) per il template finalità
    When viene creato un nuovo purpose template

    ## Associazione con un e-service template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |

    ## Associazione con e-service concreto
    When "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    Then recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceId)         |

    # Stato attivo (PUBLISHED) per il template finalità
    When il purpose template viene gradualmente spostato in stato PUBLISHED

    ## Associazione con un e-service template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |

    ## Associazione con e-service concreto
    When "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
    Then recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceId)         |

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_3_2] Errore di conflitto durante l’associazione di una risorsa già collegata ad un template finalità
    Given viene creato un nuovo purpose template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    And recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    When prova ad associare una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then la richiesta di associazione fallisce per errore di conflitto

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_3_3] Template finalità non trovato durante l’associazione di una singola risorsa ad un template finalità
    When prova ad associare una risorsa a un template finalità
      | id_template_finalita  | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | id_e_service_template | cbcb6d06-2222-4481-8df2-f3fc590933a8 |
    Then la richiesta restituisce errore di template finalità non trovato

  ## Macro scenario: Disassociazione di una risorsa collegata da un template finalità

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_5_1] Disassociazione di una singola risorsa da un template finalità con successo

    # Stato in bozza (DRAFT) per il template finalità
    Given viene creato un nuovo purpose template

    And "PA1" ha già creato e pubblicato 1 e-service
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service          | $DA_CONTESTO(eServiceId)         |
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
    And recupera le risorse collegate per suggerire il template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_risorsa_attesa     | $DA_CONTESTO(eServiceTemplateId) |
    And vengono salvate le risorse collegate in una lista di risorse di riferimento
    And vengono salvati 2 ID e-service template di riferimento dalle risorse collegate
    And vengono salvati 2 ID e-service concreto di riferimento dalle risorse collegate

    ## Disassociazione di un e-service template
    When disassocia una risorsa da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service_template | $DA_CONTESTO(referenceEServiceTemplateId_1) |

    ## Disassociazione di un e-service concreto
    And disassocia una risorsa da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service          | $DA_CONTESTO(referenceEServiceId_1) |

    # Stato attivo (PUBLISHED) per il template finalità
    When il purpose template viene gradualmente spostato in stato PUBLISHED

    ## Disassociazione di un e-service template
    When disassocia una risorsa da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service_template | $DA_CONTESTO(referenceEServiceTemplateId_2) |

    ## Disassociazione di un e-service concreto
    And disassocia una risorsa da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |
      | id_e_service          | $DA_CONTESTO(referenceEServiceId_2) |

    Then le risorse collegate al template finalità sono una lista vuota
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId) |

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_5_2] Errore di conflitto durante la disassociazione di una singola risorsa già disassociata da un template finalità
    Given viene creato un nuovo purpose template
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And associa una risorsa a un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |

    When prova a disassociare una risorsa da un template finalità
      | id_template_finalita  | $DA_CONTESTO(purposeTemplateId)  |
      | id_e_service_template | $DA_CONTESTO(eServiceTemplateId) |
    Then la richiesta di disassociazione fallisce per errore di conflitto

  @purposeTemplate @eServiceTemplateLink_bff
  Scenario: [LINK_TEMPLATE_ESERVICE_5_3] Template finalità non trovato durante la disassociazione di una singola risorsa da un template finalità
    When prova a disassociare una risorsa da un template finalità
      | id_template_finalita  | aaaabbbb-cccc-1111-2222-ddddeeee3333 |
      | id_e_service_template | eb8b9e5a-8f6d-455b-92ae-35b7ffccfaab |
    Then la richiesta restituisce errore di template finalità non trovato
