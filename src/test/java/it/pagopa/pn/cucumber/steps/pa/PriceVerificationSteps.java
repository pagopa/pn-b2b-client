package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PriceVerificationSteps {

    private final SharedSteps sharedSteps;

    @Value("${pn.external.costo_base_notifica}")
    private Integer costoBaseNotifica;

    @Autowired
    public PriceVerificationSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
    }

    @Then("viene verificato il costo {string} di una notifica {string} del utente {string}")
    public void notificationPriceVerificationIvaIncluded(String tipoCosto, String tipoNotifica, String user) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        Assertions.assertNotNull(fullSentNotification);

        Integer pricePartial;
        Integer priceTotal;

        if (fullSentNotification.getNotificationFeePolicy().equals(NotificationFeePolicy.DELIVERY_MODE)) {
            pricePartial = calcoloPrezzo(tipoNotifica, tipoCosto, user, fullSentNotification);
            priceTotal = calcoloPrezzo(tipoNotifica, tipoCosto, user, fullSentNotification);
        } else if (fullSentNotification.getNotificationFeePolicy().equals(NotificationFeePolicy.FLAT_RATE)) {
            pricePartial = 0;
            priceTotal = 0;
        } else {
            throw new IllegalArgumentException();
        }

        switch (tipoCosto.toLowerCase()) {
            case "parziale" -> {
                priceVerificationV1(pricePartial, null, Integer.parseInt(user));
                priceVerificationV23(pricePartial, null, Integer.parseInt(user), tipoCosto);
            }
            case "totale" -> priceVerificationV23(priceTotal, null, Integer.parseInt(user), tipoCosto);
            default -> throw new IllegalArgumentException();
        }
    }

    private Integer calcoloPrezzo(String tipoNotifica, String tipoCosto, String user, FullSentNotificationV29 notifica) {

        List<TimelineElementV28> listaNotifica = notifica.getTimeline().stream().filter(value -> value.getDetails() != null && value.getDetails().getAnalogCost() != null).toList();

        int pricePartial;
        int priceTotal;

        Integer paFee = notifica.getPaFee();
        Integer vat = notifica.getVat();


        switch (tipoNotifica.toLowerCase()) {
            case "890", "ar", "rir" -> {
                TimelineElementV28 analogFirstAttempt = listaNotifica.stream().filter(value -> value.getElementId().contains("ATTEMPT_0") && value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                TimelineElementV28 analogSecondAttempt = listaNotifica.stream().filter(value -> value.getElementId().contains("ATTEMPT_1") && value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                Integer analogCostFirstAttempt = analogFirstAttempt.getDetails().getAnalogCost();
                Integer analogCostSecondAttempt = analogSecondAttempt != null && analogSecondAttempt.getDetails() != null ? analogSecondAttempt.getDetails().getAnalogCost() : 0;
                pricePartial = costoBaseNotifica + analogCostFirstAttempt + analogCostSecondAttempt;
                priceTotal = Math.round(paFee + costoBaseNotifica + (analogCostFirstAttempt + analogCostSecondAttempt) + (float) ((analogCostFirstAttempt + analogCostSecondAttempt) * vat) / 100);
            }
            case "rs", "ris" -> {
                TimelineElementV28 analogNotification = listaNotifica.stream().filter(value -> value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                Integer analogCost = analogNotification.getDetails().getAnalogCost();
                pricePartial = costoBaseNotifica + analogCost;
                priceTotal = paFee + costoBaseNotifica + analogCost + Math.round(((float) (analogCost) * vat / 100));
            }
            default -> throw new IllegalArgumentException();
        }

        return switch (tipoCosto.toLowerCase()) {
            case "parziale" -> pricePartial;
            case "totale" -> priceTotal;
            default -> null;
        };
    }

    private void priceVerificationV1(Integer price, String date, Integer recipientIndex) {
        List<String> datiPagamento = sharedSteps.getDatiPagamentoVersionamento(recipientIndex, 0);
        NotificationPriceResponse notificationPrice = sharedSteps.getB2bClient().getNotificationPrice(datiPagamento.get(0), datiPagamento.get(1));
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), recipientIndex);
                Assertions.assertEquals(price, notificationPrice.getAmount());
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private void priceVerificationV2(Integer price, String date, Integer destinatario) {
        String iun = sharedSteps.getNotificationIun();
        FullSentNotificationV20 fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV2(iun);
        NotificationPriceResponse notificationPrice = sharedSteps.getB2bClient().getNotificationPrice(
                fullSentNotification.getRecipients().get(destinatario).getPayment().getCreditorTaxId(),
                fullSentNotification.getRecipients().get(destinatario).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), fullSentNotification.getIun());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(price, notificationPrice.getAmount());
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    public List<NotificationPriceResponseV23> priceVerificationV23(Integer price, String date, Integer destinatario, String tipologiaCosto) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        if (fullSentNotification != null) {
            List<NotificationPaymentItem> listNotificationPaymentItem = fullSentNotification.getRecipients().get(destinatario).getPayments();
            List<NotificationPriceResponseV23> listNotificationPriceV23 = new ArrayList<>();


            if (listNotificationPaymentItem != null) {
                for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                    NotificationPriceResponseV23 notificationPriceV23 = sharedSteps.getB2bClient().getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());

                    try {
                        Assertions.assertEquals(notificationPriceV23.getIun(), sharedSteps.getNotificationIun());
                        if (price != null) {
                            log.info("notificationPriceV23: {} destinatario: {}", notificationPriceV23, destinatario);
                            switch (tipologiaCosto.toLowerCase()) {
                                case "parziale" ->
                                        Assertions.assertEquals(price, notificationPriceV23.getPartialPrice());
                                case "totale" -> Assertions.assertEquals(price, notificationPriceV23.getTotalPrice());
                                default ->
                                        throw new IllegalArgumentException("Valore non valido per tipologiaCosto: " + tipologiaCosto);
                            }
                        }
                        if (date != null) {
                            Assertions.assertNotNull(notificationPriceV23.getRefinementDate());
                            listNotificationPriceV23.add(notificationPriceV23);
                        }

                    } catch (AssertionFailedError assertionFailedError) {
                        sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
                    }
                }
                return listNotificationPriceV23;
            }
        }
        return null;
    }
}
