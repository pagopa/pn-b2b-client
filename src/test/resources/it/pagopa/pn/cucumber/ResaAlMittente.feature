Feature: Resa al mittente di una notifica


  Scenario: [RETURN-TO-SENDER_1] Invio notifica 890 mono-destinatario verso PF dichiarato deceduto con controllo costo, retention dei documenti e stato RETURNED_TO_SENDER

  Scenario: [RETURN-TO-SENDER_2] Invio notifica AR mono-destinatario verso PF visualizzata precedentemente alla notifica di deceduto con controllo costo, retention dei documenti

  Scenario: [RETURN-TO-SENDER_3] Invio notifica AR mono-destinatario verso PF cancellata dopo la notifica di decesso con controllo costo, retention dei documenti e stato CANCELLED

  Scenario: [RETURN-TO-SENDER_4] Invio notifica 890 mono-destinatario verso PG deceduto e in seguito visualizzata con controllo costo, retention dei documenti stato finale RETURN_TO_SENDER

  Scenario: [RETURN-TO-SENDER_5] Invio notifica AR mono-destinatario verso PF cancellata e susccessivamente arrivo notifica di deceduto con controllo costo e stato finale CANCELLED


  Scenario: [RETURN-TO-SENDER_6] Invio notifica AR multi-destinatario aventi stati Inviata, Irraggiungibile, Deceduto e stato finale DELIVERED e controllo costo
  Scenario: [RETURN-TO-SENDER_7] Invio notifica AR multi-destinatario aventi stati Irraggiungibile, Deceduto e stato finale UNREACHABLE
  Scenario: [RETURN-TO-SENDER_8] Invio notifica 890 multi-destinatario entrambi deceduti e stato finale RETURNED_TO_SENDER
  Scenario: [RETURN-TO-SENDER_9] Invio notifica 890 multi-destinatario aventi stati Inviata e Deceduto e stato finale DELIVERED
  Scenario: [RETURN-TO-SENDER_10] Invio notifica AR multi-destinatario aventi stati Inviata e Irraggiungibile e stato finale DELIVERED
  Scenario: [RETURN-TO-SENDER_11] Invio notifica AR multi-destinatario aventi stati Visualizzata e Deceduto e stato finale VIEWED con controllo costo
  Scenario: [RETURN-TO-SENDER_12] Invio notifica AR multi-destinatario  aventi stati Inviata e Irraggiungibile e stato finale DELIVERED


  Scenario: [RETURN-TO-SENDER_13] Invio notifica AR multi-destinatario aventi stati Inviata e prima Visualizzata e poi Deceduto e stato finale DELIVERED con controllo costo
  Scenario: [RETURN-TO-SENDER_14] Invio notifica 890 multi-destinatario aventi stati Inviata, Irraggiungibile e Deceduto che poi Visualizza con stato finale DELIVERED e controllo costo
  Scenario: [RETURN-TO-SENDER_15] Invio notifica AR multi-destinatario aventi stati Irraggiungibile e Visualizzata successivamente Deceduto con  stato finale UNREACHABLE
  Scenario: [RETURN-TO-SENDER_16] Invio notifica 890 multi-destinatario aventi stati Irraggiungibile e Deceduto. A seguito della Cancellazione stato finale CANCELLED con controllo costo
  Scenario: [RETURN-TO-SENDER_17] Invio notifica 890 multi-destinatario aventi stati Inviata, Irraggiungibile e Visualizzata che poi sarà Deceduto con stato finale DELIVERED
  Scenario: [RETURN-TO-SENDER_18] Invio notifica AR multi-destinatario entrambi deceduti con in seguito una visualizzazione e stato finale RETURNED_TO_SENDER
  Scenario: [RETURN-TO-SENDER_19] Invio notifica 890 multi-destinatario aventi stati Deceduto e Visualizzato che poi sarà Deceduto e stato finale RETURNED_TO_SENDER

  Scenario: [RETURN-TO-SENDER_20] Invio notifica AR multi-destinatario aventi stati Inviata e prima Visualizzata e poi Deceduto e stato finale DELIVERED con controllo costo
  Scenario: [RETURN-TO-SENDER_21] Invio notifica AR multi-destinatario aventi stati Inviata, Irraggiungibile e Deceduto che poi Visualizza con stato finale DELIVERED e controllo costo
  Scenario: [RETURN-TO-SENDER_22] Invio notifica 890 multi-destinatario aventi stati Irraggiungibile e Visualizzata successivamente Deceduto con  stato finale UNREACHABLE
  Scenario: [RETURN-TO-SENDER_23] Invio notifica 890 multi-destinatario aventi stati Irraggiungibile e Deceduto. A seguito della Cancellazione stato finale CANCELLED con controllo costo


