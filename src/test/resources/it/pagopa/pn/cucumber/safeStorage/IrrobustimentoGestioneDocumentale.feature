#SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2497151003/SRS+-+Specializzazione+timeout+presigned-url+per+client+e+per+operazione+download+upload
#PROGETTAZIONE SCENARI: https://pagopa.atlassian.net/wiki/spaces/QA/pages/2539979170/PST+PN-15371+Irrobustimento+Gestione+Documentale
Feature: PN-17452 Irrobustimento Gestione Documentale

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_OK_1]
    Given il client "pn-test" ha il campo "DurationMinutestUpload" valorizzato a 4 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 200

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_KO_1]
    Given il client "pn-test" ha il campo "DurationMinutestUpload" valorizzato a 4 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si aspetta che la presigned-url scada
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 403

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_OK_2]
    Given il client "pn-delivery" ha il campo "DurationMinutestUpload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 200

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestUpload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_UPLOAD_KO_2]
    Given il client "pn-delivery" ha il campo "DurationMinutestUpload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si aspetta che la presigned-url scada
    When si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    Then l'operazione di "upload" restituisce status code 403

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_OK_1]
    Given il client "pn-test" ha il campo "DurationMinutestDownload" valorizzato a 5 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 200

  @presignedUrlTimeout
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_KO_1]
    Given il client "pn-test" ha il campo "DurationMinutestDownload" valorizzato a 5 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    And si aspetta che la presigned-url scada
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 403

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_OK_2]
    Given il client "pn-delivery" ha il campo "DurationMinutestDownload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 200

  @presignedUrlTimeout
  #il client usato in questo test non ha DurationMinutestDownload impostato su pn-SsAnagraficaClient, ma ha il valore di default dell'application.properties di safe storage
  Scenario: [PRESIGNED_URL_TIMEOUT_DOWNLOAD_KO_2]
    Given il client "pn-delivery" ha il campo "DurationMinutestDownload" valorizzato a 0 minuti
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di upload
    And si effettua un "upload" tramite presignedUrl del documento precedentemente registrato
    And viene eseguita la chiamata a safeStorage per ottenere la presigned-url di download
    And si aspetta che la presigned-url scada
    When si effettua un "download" tramite presignedUrl del documento precedentemente caricato
    Then l'operazione di "download" restituisce status code 403