#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2497151003/SRS+-+Specializzazione+timeout+presigned-url+per+client+e+per+operazione+download+upload
#PROGETTAZIONE SCENARI: https://pagopa.atlassian.net/wiki/spaces/QA/pages/2539979170/PST+PN-15371+Irrobustimento+Gestione+Documentale
Feature: PN-17452 Irrobustimento Gestione Documentale

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_OK_1] Upload di un documento tramite presignedUrl valida da parte di un client che non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient
    Given il client "pn-test" ha il campo "DurationMinutestUpload" valorizzato a 4 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 200

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_KO_1] Upload di un documento tramite presignedUrl scaduta da parte di un client che ha DurationMinutestUpload impostato su pn-SsAnagraficaClient
    Given il client "pn-test" ha il campo "DurationMinutestUpload" valorizzato a 4 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si aspetta che la presigned-url scada
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 403

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_OK_2] Upload di un documento tramite presignedUrl valida da parte di un client che non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient
    Given il client "pn-delivery" ha il campo "DurationMinutestUpload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 200

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_KO_2] Upload di un documento tramite presignedUrl scaduta da parte di un client che non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient
    Given il client "pn-delivery" ha il campo "DurationMinutestUpload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si aspetta che la presigned-url scada
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 403

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_OK_1] Download di un documento tramite presignedUrl valida da parte di un client che ha DurationMinutestDownload impostato su pn-SsAnagraficaClient
    Given il client "pn-test" ha il campo "DurationMinutestDownload" valorizzato a 5 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 200

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_KO_1] Download di un documento tramite presignedUrl scaduta da parte di un client che ha DurationMinutestDownload impostato su pn-SsAnagraficaClient
    Given il client "pn-test" ha il campo "DurationMinutestDownload" valorizzato a 5 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    And si aspetta che la presigned-url scada
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 403

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_OK_2] Download di un documento tramite presignedUrl valida da parte di un client che non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient
    Given il client "pn-delivery" ha il campo "DurationMinutestDownload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 200

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_KO_2] Download di un documento tramite presignedUrl scaduta da parte di un client che non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient
    Given il client "pn-delivery" ha il campo "DurationMinutestDownload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    And si aspetta che la presigned-url scada
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 403

  Scenario Outline: [TEST_MANUALI_CATENA_TRASFORMAZIONI] Step da lanciare per caricare i documenti su cui poi effettuare i check lato manuale per Catena di Trasformazioni (https://pagopa.atlassian.net/wiki/spaces/PN/pages/2555641910/PST+PN-1749+Irrobustimento+Gestione+Documentale+-+Catena+di+trasformazioni)
    Given Viene caricato un nuovo documento "<documentName>" di tipo "<documentType>"
    Examples:
      | documentName                 | documentType            |
      #CASI POSITIVI (devono essere presenti sul bucket pn-safestorage e non comparire sul bucket pn-safestorage-staging)
      | multa.pdf                    | PN_CHAIN_TRANSFORMATION |
      | multa.pdf                    | PN_AAR                  |
      | multa.pdf                    | PN_LEGAL_FACTS          |
      #CASI NEGATIVI (devono essere presenti sul bucket pn-safestorage-staging e non comparire sul bucket pn-safestorage)
      | documento_errato.cleaned.pdf | PN_CHAIN_TRANSFORMATION |
      | corrupted-document.pdf       | PN_CHAIN_TRANSFORMATION |
