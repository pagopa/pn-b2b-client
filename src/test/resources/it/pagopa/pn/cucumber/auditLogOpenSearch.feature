Feature: verifica audit-log su openSearch

  Scenario Outline: [AUDIT_OPEN_SEARCH_1] verifica solo presenza audit log 10y
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log              |
      | AUD_ACC_LOGIN          |
      #| AUD_ACC_LOGOUT         |
      | AUD_NT_PRELOAD         |
      #| AUD_NT_LOAD            |
      | AUD_NT_INSERT          |
      | AUD_NT_CHECK           |
      | AUD_NT_VALID           |
      | AUD_NT_AAR             |
      | AUD_NT_TIMELINE        |
      | AUD_NT_STATUS          |
      | AUD_NT_NEWLEGAL        |
      | AUD_NT_VIEW_RCP        |
      | AUD_NT_DOCOPEN_RCP     |
      | AUD_NT_PAYMENT         |
      | AUD_NT_REQCOST         |
      | AUD_NT_REQQR           |
      | AUD_NT_DOWNTIME        |
      | AUD_NT_SEARCH_DEL      |
      | AUD_NT_ATCHOPEN_DEL    |
      | AUD_NT_VIEW_DEL        |
      | AUD_NT_DOCOPEN_DEL     |
      | AUD_NT_LEGALOPEN_DEL   |
      | AUD_AB_DD_INSUP        |
      | AUD_AB_DD_DEL          |
      | AUD_AB_DA_INSUP        |
      | AUD_AB_DA_DEL          |
      #| AUD_AB_DA_IO_INSUP     |
      #| AUD_AB_DA_IO_DEL       |
      | AUD_AB_VALIDATE_PEC    |
      | AUD_AB_VALIDATE_CODE   |
      | AUD_AB_VERIFY_PEC      |
      | AUD_AB_VERIFY_MAIL     |
      | AUD_AB_VERIFY_SMS      |
      #| AUD_AB_DEL             |
      | AUD_UC_INSUP           |
      #| AUD_AN_SEND            |
      #| AUD_AN_RECEIVE         |
      | AUD_DD_SEND            |
      | AUD_DD_RECEIVE         |
      | AUD_DL_CREATE          |
      | AUD_DL_ACCEPT          |
      | AUD_DL_UPDATE          |
      | AUD_DL_REJECT          |
      | AUD_DL_REVOKE          |
      | AUD_DL_EXPIRE          |
      | AUD_AK_CREATE          |
      | AUD_AK_VIEW            |
      | AUD_AK_ROTATE          |
      | AUD_AK_BLOCK           |
      | AUD_AK_REACTIVATE      |
      | AUD_AK_DELETE          |
      | AUD_FD_RESOLVE_LOGIC   |
      | AUD_FD_RESOLVE_SERVICE |
      | AUD_FD_SEND            |
      | AUD_PD_PREPARE         |
      | AUD_FD_RECEIVE         |
      | AUD_FD_DISCARD         |
      | AUD_PD_PREPARE_RECEIVE |
      | AUD_PD_EXECUTE         |
      | AUD_PD_EXECUTE_RECEIVE |
      | AUD_NT_CANCELLED       |


  Scenario Outline: [AUDIT_OPEN_SEARCH_2] verifica presenza nel giusto indice e data di scrittura audit log 10y
    Then viene verificato che esiste un audit log "<audit-log>" in "10y" non più vecchio di 10 giorni
    And viene verificato che non esiste un audit log "<audit-log>" in "5y"
    Examples:
      | audit-log              |
      | AUD_ACC_LOGIN          |
      #| AUD_ACC_LOGOUT         |
      | AUD_NT_PRELOAD         |
      #| AUD_NT_LOAD            |
      | AUD_NT_INSERT          |
      | AUD_NT_CHECK           |
      | AUD_NT_VALID           |
      | AUD_NT_AAR             |
      | AUD_NT_TIMELINE        |
      | AUD_NT_STATUS          |
      | AUD_NT_NEWLEGAL        |
      | AUD_NT_VIEW_RCP        |
      | AUD_NT_DOCOPEN_RCP     |
      | AUD_NT_PAYMENT         |
      | AUD_NT_REQCOST         |
      | AUD_NT_REQQR           |
      | AUD_NT_DOWNTIME        |
      | AUD_NT_SEARCH_DEL      |
      | AUD_NT_ATCHOPEN_DEL    |
      | AUD_NT_VIEW_DEL        |
      | AUD_NT_DOCOPEN_DEL     |
      | AUD_NT_LEGALOPEN_DEL   |
      | AUD_AB_DD_INSUP        |
      | AUD_AB_DD_DEL          |
      | AUD_AB_DA_INSUP        |
      | AUD_AB_DA_DEL          |
      #| AUD_AB_DA_IO_INSUP     |
      #| AUD_AB_DA_IO_DEL       |
      | AUD_AB_VALIDATE_PEC    |
      | AUD_AB_VALIDATE_CODE   |
      | AUD_AB_VERIFY_PEC      |
      | AUD_AB_VERIFY_MAIL     |
      | AUD_AB_VERIFY_SMS      |
      #| AUD_AB_DEL             |
      | AUD_UC_INSUP           |
      #| AUD_AN_SEND            |
      #| AUD_AN_RECEIVE         |
      | AUD_DD_SEND            |
      | AUD_DD_RECEIVE         |
      | AUD_DL_CREATE          |
      | AUD_DL_ACCEPT          |
      | AUD_DL_UPDATE          |
      | AUD_DL_REJECT          |
      | AUD_DL_REVOKE          |
      | AUD_DL_EXPIRE          |
      | AUD_AK_CREATE          |
      | AUD_AK_VIEW            |
      | AUD_AK_ROTATE          |
      | AUD_AK_BLOCK           |
      | AUD_AK_REACTIVATE      |
      | AUD_AK_DELETE          |
      | AUD_FD_RESOLVE_LOGIC   |
      | AUD_FD_RESOLVE_SERVICE |
      | AUD_FD_SEND            |
      | AUD_PD_PREPARE         |
      | AUD_FD_RECEIVE         |
      | AUD_FD_DISCARD         |
      | AUD_PD_PREPARE_RECEIVE |
      | AUD_PD_EXECUTE         |
      | AUD_PD_EXECUTE_RECEIVE |
      | AUD_NT_CANCELLED       |



  Scenario Outline: [AUDIT_OPEN_SEARCH_3] verifica solo presenza audit log 5y
    Then viene verificato che esiste un audit log "<audit-log>" in "5y"
    Examples:
      | audit-log              |
      | AUD_NT_SEARCH_RCP      |
      | AUD_NT_ATCHOPEN_RCP    |
      | AUD_NT_LEGALOPEN_RCP   |
      | AUD_NT_SEARCH_SND      |
      | AUD_NT_VIEW_SND        |
      | AUD_NT_DOCOPEN_SND     |
      | AUD_NT_ATCHOPEN_SND    |
      | AUD_NT_LEGALOPEN_SND   |
      | AUD_DA_SEND_SMS        |
      | AUD_DA_SEND_EMAIL      |
      #| AUD_DA_SEND_IO         |
      | AUD_DT_CREATE          |
      | AUD_DT_UPDATE          |
      #| AUD_DT_DELETE          |
      | AUD_NT_CANCELLED       |





  Scenario Outline: [AUDIT_OPEN_SEARCH_4] verifica presenza nel giusto indice e data di scrittura audit log 5y
    Then viene verificato che esiste un audit log "<audit-log>" in "5y" non più vecchio di 10 giorni
    And viene verificato che non esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log              |
      | AUD_NT_SEARCH_RCP      |
      | AUD_NT_ATCHOPEN_RCP    |
      | AUD_NT_LEGALOPEN_RCP   |
      | AUD_NT_SEARCH_SND      |
      | AUD_NT_VIEW_SND        |
      | AUD_NT_DOCOPEN_SND     |
      | AUD_NT_ATCHOPEN_SND    |
      | AUD_NT_LEGALOPEN_SND   |
      | AUD_DA_SEND_SMS        |
      | AUD_DA_SEND_EMAIL      |
      #| AUD_DA_SEND_IO         |
      | AUD_DT_CREATE          |
      | AUD_DT_UPDATE          |
      #| AUD_DT_DELETE          |
      | AUD_NT_CANCELLED       |


