package it.pagopa.pn.cucumber.steps.informalNotification.data;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStats;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum CampaignCounter {

    NOTIFICHE_TOTALI(CampaignStats::getTotalCount),
    NOTIFICHE_RIFIUTATE(CampaignStats::getTotalRefusedCount),
    NOTIFICHE_INVIATE_SU_CANALE(CampaignStats::getSentOnChannelCount),
    NOTIFICHE_CONSEGNATE(CampaignStats::getDeliveredCount),
    NOTIFICHE_NON_CONSEGNABILI(CampaignStats::getUndeliverableCount),
    WORKFLOW_COMPLETATI(CampaignStats::getWorkflowDoneCount),
    NOTIFICHE_VISUALIZZATE(CampaignStats::getViewedCount),
    NOTIFICHE_PAGATE(CampaignStats::getPaidCount),

    NOTIFICHE_INVIATE_IO(stats -> stats.getSentOnChannel().getDigital().getIO()),
    NOTIFICHE_INVIATE_EMAIL(stats -> stats.getSentOnChannel().getDigital().getEMAIL()),
    NOTIFICHE_INVIATE_PEC(stats -> stats.getSentOnChannel().getDigital().getPEC()),
    NOTIFICHE_INVIATE_SMS(stats -> stats.getSentOnChannel().getDigital().getSMS()),
    NOTIFICHE_INVIATE_RS(stats -> stats.getSentOnChannel().getAnalog().getRS()),
    NOTIFICHE_CONSEGNATE_IO(stats -> stats.getDelivered().getIO()),
    NOTIFICHE_CONSEGNATE_EMAIL(stats -> stats.getDelivered().getEMAIL()),
    NOTIFICHE_CONSEGNATE_PEC(stats -> stats.getDelivered().getPEC()),
    NOTIFICHE_CONSEGNATE_RS(stats -> stats.getDelivered().getANALOG()),
    NOTIFICHE_VISUALIZZATE_IO(stats -> stats.getViewed().getIO()),
    NOTIFICHE_VISUALIZZATE_SEND(stats -> stats.getViewed().getSEND()),
    PRIMA_VISUALIZZAZIONE(stats -> stats.getViewed().getFirstViewedCount());

    private final Function<CampaignStats, Integer> extractor;

    public Integer extract(CampaignStats stats) {
        return extractor.apply(stats);
    }
}



