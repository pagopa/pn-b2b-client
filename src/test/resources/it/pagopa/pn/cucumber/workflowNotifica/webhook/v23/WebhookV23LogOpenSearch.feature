Feature: lettura log stream da open search

  #--------------AUDIT LOG DI UNO STREAM------------
  #@webhookV23
  Scenario Outline: [B2B-STREAM_ES2.1] Impostare nuova tipologia di Audit Log
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log      |
      | AUD_WH_CREATE  |
      | AUD_WH_READ    |
      | AUD_WH_UPDATE  |
      | AUD_WH_DELETE  |
      | AUD_WH_DISABLE |
      | AUD_WH_CONSUME |

 # AUD_WH_CREATE(PnAuditLogMarker.AUDIT10Y),
 # AUD_WH_READ(PnAuditLogMarker.AUDIT10Y),
 # AUD_WH_UPDATE(PnAuditLogMarker.AUDIT10Y),
 # AUD_WH_DELETE(PnAuditLogMarker.AUDIT10Y),
 # AUD_WH_DISABLE(PnAuditLogMarker.AUDIT10Y),
 # AUD_WH_CONSUME(PnAuditLogMarker.AUDIT10Y),

  Scenario: [B2B-STREAM_ES2.2] Impostare nuova tipologia di Audit Log
    Then viene verificato che esiste un audit log "AUD_WH_CONSUME" in "10y"
    And viene verificato che esiste un audit log "AUD_WH_CONSUME" con messaggio "[AUD_WH_CONSUME] FAILURE - Error in reading stream"

  @webhookV23
  Scenario: [B2B-STREAM_AUDIT_ERROR_1] Viene verificata la presenza del audit log specifico in caso di errore durante la lettura di uno stream di eventi.
    And vengono letti gli eventi dello stream con id "ffffffff-ffff-ffff-ffff-ffffffffffff" e versione "v23"
    And verifico la presenza di un audit log su "/aws/ecs/pn-stream" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | tag     | AUD_WH_CONSUME                    |
      | message | FAILURE - Error in reading stream |
    Then viene verificato che esiste un audit log "AUD_WH_CONSUME" in "10y"
    And viene verificato che esiste un audit log "AUD_WH_CONSUME" con messaggio "[AUD_WH_CONSUME] FAILURE - Error in reading stream"