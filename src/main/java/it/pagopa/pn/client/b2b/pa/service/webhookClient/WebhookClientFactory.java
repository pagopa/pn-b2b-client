package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Classe per futuro possibile refactor (al momento inutilizzata)
 * Al fine di non avere un unico client per il webhook con i metodi relativi a tutte le versioni, si è optato per il frazionamento
 * di questo in N classi (una per versione).
 * Tuttavia, non si poteva eseguire l'autowiring delle WebhookClientVx dentro alla classe WebhookStepsVx corrispondente, in quanto le
 * classi WebhookStepsVx hanno bisogno che gli si passi un AvanzamentoNotificheWebhookSteps nel costruttore
 * (il che creerebbe una dipendenza circolare).
 * La soluzione trovata è stata quella di creare questa classe, dotata di tutti i client, e iniettarla dentro ad AvanzamentoNotificheWebhookSteps.
 * Ogni WebhookStepsVx potrà poi recuperare il proprio webhook client tramite il metodo getWebhookClientByVersion
 * TODO: si dovrà aggiungere alla spring Integration anche
 * <p>
 *         WebhookClientFactory.class,
 *         WebhookClientV10.class,
 *         WebhookClientV23.class,
 *         WebhookClientV24.class,
 *         WebhookClientV25.class,
 *         WebhookClientV26.class,
 *         WebhookClientV27.class
 */
@Component
@Getter
public class WebhookClientFactory {

    private final Map<Integer, AbstractWebhookClient> clientsMap;

    @Autowired
    public WebhookClientFactory(
            WebhookClientV10 webhookClientV10,
            WebhookClientV23 webhookClientV23,
            WebhookClientV24 webhookClientV24,
            WebhookClientV25 webhookClientV25,
            WebhookClientV26 webhookClientV26,
            WebhookClientV27 webhookClientV27
    ) {
        clientsMap = Map.of(
                10, webhookClientV10,
                23, webhookClientV23,
                24, webhookClientV24,
                25, webhookClientV25,
                26, webhookClientV26,
                27, webhookClientV27
        );
    }

    public AbstractWebhookClient getWebhookClientByVersion(int version) {
        return clientsMap.get(version);
    }
}
