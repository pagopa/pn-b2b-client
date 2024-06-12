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