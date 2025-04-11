package it.pagopa.pn.cucumber.steps.utilitySteps.checkFullSentNotification;

/**
 * Enum di tutte le tipologie di check che si possono effettuare su una fullSentNotification nelle classi
 * che implementano l'interfaccia {@link it.pagopa.pn.cucumber.steps.pa.b2bVersions.B2bStepsInterface}.
 * Se si necessita di creare una nuova tipologia di check con parametri, sarà sufficiente aggiungere
 * i parametri necessari all'interno di FullSentNotificationCheckFilters
 * e inserire qui la nuova tipologia di check
 */
public enum FullSentNotificationCheck {
    SIZE;
}
