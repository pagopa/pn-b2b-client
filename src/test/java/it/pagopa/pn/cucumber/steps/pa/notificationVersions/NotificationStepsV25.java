package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
//TODO MATTEO RIMUOVERE ABSTRACR
public abstract class NotificationStepsV25 implements NotificationStepsInterface {

//    private NewNotificationRequestV25 notificationRequest;
//    private NewNotificationResponse notificationResponse;
//    private final NotificationVersion version;
//    private final SharedSteps sharedSteps;
//
//    public NotificationStepsV25(SharedSteps sharedSteps) {
//        version = NotificationVersion.V25;
//        this.sharedSteps = sharedSteps;
//    }
//
//    @Override
//    public String getVersionString() {
//        return version.toString();
//    }
//
//    @Override
//    public void prepareNotificationRequest(Map<String, String> data) {
//        notificationRequest = convertNotificationRequest(data);
//        sharedSteps.setVersionUsed(version);
//    }
//
//    @Override
//    public void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken) {
//        NewNotificationRequestV25 newNotificationRequest = convertNotificationRequest(new HashMap<>());
//        NotificationRecipientV24 newRecipient = convertNotificationRecipient(new HashMap<>());
//
//        NotificationRecipientV24 oldRecipient = notificationRequest.getRecipients().get(0);
//        newRecipient.setDenomination(oldRecipient.getDenomination());
//        newRecipient.setTaxId(oldRecipient.getTaxId());
//        newRecipient.setRecipientType(oldRecipient.getRecipientType());
//
//        if (isCreditorTaxIdUguale) {
//            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayments());
//            String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayments()).get(0).getPagoPa()).getCreditorTaxId();
//            newRecipient.getPayments().get(0).getPagoPa().setCreditorTaxId(creditorTaxId);
//        }
//        if (isCodiceAvvisoUguale) {
//            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayments());
//            String noticeCode = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayments()).get(0).getPagoPa()).getNoticeCode();
//            newRecipient.getPayments().get(0).getPagoPa().setNoticeCode(noticeCode);
//        }
//        if (isPaProtocolNumberUguale) {
//            newNotificationRequest.setPaProtocolNumber(notificationRequest.getPaProtocolNumber());
//        }
//        if (idempotenceToken != null) {
//            newNotificationRequest.setIdempotenceToken(idempotenceToken);
//        }
//
//        newNotificationRequest.setSubject(notificationRequest.getSubject());
//        newNotificationRequest.setSenderDenomination(notificationRequest.getSenderDenomination());
//        newNotificationRequest.addRecipientsItem(newRecipient);
//
//        notificationRequest = newNotificationRequest;
//    }
//
//    @Override
//    public void resetNotificationRequest() {
//        notificationRequest.setRecipients(new ArrayList<>());
//        NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef()
//                .key("classpath:/sample.pdf");
//        NotificationDocument document = new NotificationDocument()
//                .contentType("application/pdf")
//                .ref(ref);
//        notificationRequest.setDocuments(List.of(document));
//    }
//
//    @Override
//    public void addRecipientToNotification(Destinatario destinatario, Map<String, String> data) {
//        if (destinatario != null && destinatario.equals(DESTINATARIO_NESSUNO)) return;
//        NotificationRecipientV24 notificationRecipient = convertNotificationRecipient(data);
//        if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
//                && NotificationValue.getValue(data, PAYMENT.key) != null) {
//            String pagopaFormValue = getValue(data, PAYMENT_PAGOPA_FORM.key);
//            if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
//                for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
//                    Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
//                }
//            }
//        }
//        if (destinatario != null) {
//            notificationRecipient.setDenomination(destinatario.getDenomination());
//            notificationRecipient.setTaxId(destinatario.equals(DESTINATARIO_SIGNOR_CASUALE) ?
//                    FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId());
//            notificationRecipient.setRecipientType(NotificationRecipientV24.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
//            /** Nei vecchi metodi @And("Destinatario xxx") denomination e taxId venivano sempre settati
//             * (recipientType veniva spesso passato null, ma in quei casi subentrava il valore di default PG)
//             * e data veniva passata sempre come mappa vuota.
//             * Al contrario nei vecchi metodi @And("Destinatario xxx e:"), data veniva passata come mappa con valori
//             * e al contempo digitalDomicile era sempre null, in modo da non sovrascrivere eventuali valori passati.
//             * Pertanto il seguente codice segue il vecchio comportamento, ma in maniera più chiara e coincisa */
//            if (data.isEmpty()) {
//                notificationRecipient.setDigitalDomicile(
//                        new NotificationDigitalAddress()
//                                .type(NotificationDigitalAddress.TypeEnum.valueOf(destinatario.getDigitalDomicileType()))
//                                .address(Costanti.getDigitalAddressValue()));
//            }
//        }
//        notificationRequest.addRecipientsItem(notificationRecipient);
//    }
//
//    @Override
//    public void addRecipientToNotificationSpecialCondition(Destinatario destinatario, Map<String, String> data, String condition, Integer otherRecipientIndex) {
//        switch (condition.toUpperCase()) {
//            case "SAME_IUV_AS_RECIPIENT_INDEX" -> {
//                Assertions.assertDoesNotThrow(() -> Objects.requireNonNull(notificationRequest.getRecipients().get(otherRecipientIndex - 1).getPayments()).get(0));
//                String previousIUV = notificationRequest.getRecipients().get(otherRecipientIndex).getPayments().get(0).getPagoPa().getNoticeCode();
//                int currentRecipientNumber = notificationRequest.getRecipients().size();
//                addRecipientToNotification(destinatario, data);
//                NotificationRecipientV24 recipientAdded = notificationRequest.getRecipients().get(currentRecipientNumber + 1);
//                recipientAdded.getPayments().get(0).getPagoPa().setNoticeCode(previousIUV);
//            }
//        }
//    }
//
//    @Override
//    public void setSenderTaxId(String senderTaxId) {
//        notificationRequest.setSenderTaxId(senderTaxId);
//    }
//
//
//    @Override
//    public String getNotificationRequestGroup() {
//        return notificationRequest.getGroup();
//    }
//
//    @Override
//    public void setNotificationRequestGroup(String group) {
//        notificationRequest.setGroup(group);
//    }
//
//    @Override
//    public String sendNotification(int wait, String status, String pollingStrategy) {
//        AtomicReference<String> newNotificationIun = new AtomicReference<>(null);
//        try {
//            Assertions.assertDoesNotThrow(() -> {
//                notificationResponse = (NewNotificationResponse) uploadNotification();
//                if (status.equalsIgnoreCase(NOTIFICATION_STATUS_ACCEPTED)) {
//                    threadWait(wait);
//                    FullSentNotificationV27 fullSentNotification = waitForRequestAccepted(notificationResponse, pollingStrategy);
//                    threadWait(wait);
//                    Assertions.assertNotNull(fullSentNotification);
//                    newNotificationIun.set(fullSentNotification.getIun());
//                    sharedSteps.setNotificationIun(newNotificationIun.get());
//                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
//                    String errorCode = waitForRequestRefused(notificationResponse, pollingStrategy);
//                    sharedSteps.setErrorCode(errorCode);
//                    threadWait(wait);
//                    Assertions.assertFalse(errorCode.isEmpty());
//                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_NOT_REFUSED)) {
//                    RequestStatus response = sharedSteps.getB2bUtils().getClient().notificationCancellation(
//                            new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId())));
//                    Assertions.assertNotNull(response);
//                    Assertions.assertNotNull(response.getDetails());
//                    Assertions.assertFalse(response.getDetails().isEmpty());
//                    Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
//                    boolean refused = waitForRequestNotRefused(notificationResponse, pollingStrategy);
//                    threadWait(wait);
//                    Assertions.assertFalse(refused);
//                }
//            });
//            return newNotificationIun.get();
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }
//
//    @Override
//    public Object uploadNotification() throws IOException {
//        sharedSteps.setNotificationCreationDate(OffsetDateTime.now());
//        //PRELOAD DOCUMENTI NOTIFICA
//        List<NotificationDocument> documents = new ArrayList<>();
//        for (NotificationDocument doc : notificationRequest.getDocuments()) {
//            try {
//                Thread.sleep(sharedSteps.getB2bUtils().getRandom().nextInt(350));
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new PnB2bException(e.getMessage());
//            }
//            if (doc != null) {
//                documents.add(this.preloadDocument(doc));
//            }
//        }
//        notificationRequest.setDocuments(documents);
//        //PRELOAD DOCUMENTI DI PAGAMENTO
//        preloadPayDocument(notificationRequest);
//        return getAndCheckSendNewNotification(notificationRequest);
//    }
//
//    @Override
//    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
//        Objects.requireNonNull(Objects.requireNonNull(
//                this.notificationRequest.getRecipients().get(0).getPayments()).get(posizione).getPagoPa()).setNoticeCode(iuvGPD);
//    }
//
//    @Override
//    public void addDocumentItems(int numAllegati) {
//        int i = 0;
//        while (i < numAllegati) {
//            notificationRequest.addDocumentsItem(
//                    new NotificationDocument()
//                            .contentType(APPLICATION_PDF)
//                            .ref(new NotificationAttachmentBodyRef().key(getDefaultValue(DOCUMENT.key))));
//            i++;
//        }
//    }
//
//    @DataTableType
//    public synchronized NewNotificationRequestV25 convertNotificationRequest(Map<String, String> data) {
//        NewNotificationRequestV25 notificationRequest = (new NewNotificationRequestV25()
//                .subject(getValue(data, SUBJECT.key))
//                .cancelledIun(getValue(data, CANCELLED_IUN.key))
//                .group(getValue(data, GROUP.key))
//                .idempotenceToken(getValue(data, IDEMPOTENCE_TOKEN.key))
//                ._abstract(getValue(data, ABSTRACT.key))
//                .senderDenomination(getValue(data, SENDER_DENOMINATION.key))
//                .senderTaxId(getValue(data, SENDER_TAX_ID.key))
//                .paProtocolNumber(getValue(data, PA_PROTOCOL_NUMBER.key))
//                .taxonomyCode(getValue(data, TAXONOMY_CODE.key))
//                .amount(getValue(data, AMOUNT.key) == null ? null : Integer.parseInt(getValue(data, AMOUNT.key)))
//                .paymentExpirationDate(getValue(data, PAYMENT_EXPIRATION_DATE.key) == null ?
//                        null : getValue(data, PAYMENT_EXPIRATION_DATE.key))
//                .notificationFeePolicy((getValue(data, NOTIFICATION_FEE_POLICY.key) == null ?
//                        null : (getValue(data, NOTIFICATION_FEE_POLICY.key).equalsIgnoreCase("FLAT_RATE") ?
//                        NotificationFeePolicy.FLAT_RATE :
//                        NotificationFeePolicy.DELIVERY_MODE)))
//                .physicalCommunicationType((getValue(data, PHYSICAL_COMMUNICATION_TYPE.key) == null ?
//                        null : (getValue(data, PHYSICAL_COMMUNICATION_TYPE.key).equalsIgnoreCase("REGISTERED_LETTER_890") ?
//                        NewNotificationRequestV25.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890 :
//                        NewNotificationRequestV25.PhysicalCommunicationTypeEnum.AR_REGISTERED_LETTER)))
//                .paFee(getValue(data, PA_FEE.key) == null ? null : Integer.parseInt(getValue(data, PA_FEE.key)))
//                .vat(getValue(data, VAT.key) == null ? null : Integer.parseInt(getValue(data, VAT.key)))
//                .pagoPaIntMode((getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("SYNC") ?
//                        NewNotificationRequestV25.PagoPaIntModeEnum.SYNC :
//                        (getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("ASYNC") ?
//                                NewNotificationRequestV25.PagoPaIntModeEnum.ASYNC :
//                                getValue(data, PAGOPAINTMODE.key).equalsIgnoreCase("NONE") ?
//                                        NewNotificationRequestV25.PagoPaIntModeEnum.NONE : null))));
//
//        notificationRequest = addDocument(notificationRequest, data);
//        try {
//            Thread.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return notificationRequest;
//    }
//
//    @DataTableType
//    public synchronized NotificationRecipientV24 convertNotificationRecipient(Map<String, String> data) {
//        List<NotificationPaymentItem> listPayment;
//
//        NotificationRecipientV24 notificationRecipient = (new NotificationRecipientV24()
//                .denomination(getValue(data, DENOMINATION.key))
//                .taxId(getValue(data, TAX_ID.key))
//                //.internalId(getValue(data,INTERNAL_ID.key))
//                .digitalDomicile(getValue(data, DIGITAL_DOMICILE.key) == null ? null : (new NotificationDigitalAddress()
//                        .type((getValue(data, DIGITAL_DOMICILE_TYPE.key) == null ? null : NotificationDigitalAddress.TypeEnum.PEC))
//                        .address(getValue(data, DIGITAL_DOMICILE_ADDRESS.key)))
//                )
//                .physicalAddress(getValue(data, PHYSICAL_ADDRES.key) == null ? null : new NotificationPhysicalAddress()
//                        .address(getValue(data, PHYSICAL_ADDRESS_ADDRESS.key))
//                        .addressDetails(getValue(data, PHYSICAL_ADDRESS_DETAILS.key))
//                        .municipality(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITY.key))
//                        .at(getValue(data, PHYSICAL_ADDRESS_AT.key))
//                        .municipalityDetails(getValue(data, PHYSICAL_ADDRESS_MUNICIPALITYDETAILS.key))
//                        .province(getValue(data, PHYSICAL_ADDRESS_PROVINCE.key))
//                        .foreignState(getValue(data, PHYSICAL_ADDRESS_STATE.key))
//                        .zip(getValue(data, PHYSICAL_ADDRESS_ZIP.key))
//                )
//                .recipientType((getValue(data, RECIPIENT_TYPE.key) == null ? null :
//                        (getValue(data, RECIPIENT_TYPE.key).equalsIgnoreCase("PF") ?
//                                NotificationRecipientV24.RecipientTypeEnum.PF : NotificationRecipientV24.RecipientTypeEnum.PG)))
//                //GESTIONE ISTANZE DI PAGAMENTI
//        );
//        //N PAGAMENTI
//        if (getValue(data, PAYMENT.key) != null && getValue(data, PAYMENT_MULTY_NUMBER.key) != null && !getValue(data, PAYMENT_MULTY_NUMBER.key).isEmpty()) {
//            listPayment = new ArrayList<>();
//            for (int i = 0; i < Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key)); i++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException exc) {
//                    throw new RuntimeException(exc);
//                }
//                NotificationPaymentItem addPaymentsItem = new NotificationPaymentItem();
//                addPaymentsItem.pagoPa(getValue(data, PAYMENT_PAGOPA_FORM.key) == null ? null :
//                        (getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NO") ?
//                                null :
//                                new PagoPaPayment()
//                                        .creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key) == null ? null : getValue(data, PAYMENT_CREDITOR_TAX_ID.key))
//                                        .noticeCode(getValue(data, PAYMENT_NOTICE_CODE.key) == null ? null : getValue(data, PAYMENT_NOTICE_CODE.key))
//                                        .applyCost(getValue(data, PAYMENT_APPLY_COST_PAGOPA.key) == null ? null :
//                                                getValue(data, PAYMENT_APPLY_COST_PAGOPA.key).equalsIgnoreCase("SI"))
//                                        .attachment(getValue(data, PAYMENT_PAGOPA_FORM.key).equalsIgnoreCase("NOALLEGATO") ?
//                                                null : sharedSteps.getB2bUtils().newAttachment(getDefaultValue(PAYMENT_PAGOPA_FORM.key)))));
//
//                //LOAD METADATI F24
//                if (getValue(data, PAYMENT_F24.key) != null && !getValue(data, PAYMENT_F24.key).isEmpty()) {
//                    sharedSteps.getDataTableTypeUtil().setMetadatiF24(data, addPaymentsItem, i);
//
//                } else if (getValue(data, PAYMENT_F24_X.key) != null && !getValue(data, PAYMENT_F24_X.key).isEmpty()) {
//                    sharedSteps.getDataTableTypeUtil().setMetadatiF24(data, addPaymentsItem, i);
//                }
//
//                listPayment.add(addPaymentsItem);
//            }
//            notificationRecipient.setPayments(listPayment);
//        }
//        try {
//            Thread.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return notificationRecipient;
//    }
//
//    private NewNotificationRequestV25 addDocument(NewNotificationRequestV25 notificationRequest, Map<String, String> data) {
//        String documentsToAdd = getValue(data, DOCUMENT.key);
//        if (documentsToAdd == null) {
//            return notificationRequest.addDocumentsItem(null);
//        }
//        if (documentsToAdd.contains(";")) {
//            for (String documentElem : documentsToAdd.split(";")) {
//                notificationRequest = notificationRequest.addDocumentsItem(sharedSteps.getDataTableTypeUtil().getNotificationDocument(documentElem));
//            }
//        } else {
//            notificationRequest = notificationRequest.addDocumentsItem(sharedSteps.getDataTableTypeUtil().getNotificationDocument(documentsToAdd));
//        }
//        return notificationRequest;
//    }
//
//    @Override
//    public void performPriceVerification(String price, String date, Integer destinatario) {
//        String iun = sharedSteps.getNotificationIun();
//        FullSentNotificationV27 fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV27(iun);
//        List<NotificationPaymentItem> listNotificationPaymentItem = fullSentNotification.getRecipients().get(destinatario).getPayments();
//        if (listNotificationPaymentItem != null) {
//            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
//                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice =
//                        sharedSteps.getB2bClient().getNotificationPrice(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
//                try {
//                    Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
//                    if (price != null) {
//                        log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
//                        Assertions.assertEquals(Integer.parseInt(price), notificationPrice.getAmount());
//                    }
//                    if (notificationPrice.getRefinementDate() != null) {
//                        Assertions.assertEquals(OffsetDateTime.now().toLocalDate(), notificationPrice.getRefinementDate().toLocalDate());
//                    }
//                } catch (AssertionFailedError assertionFailedError) {
//                    sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//                }
//            }
//        }
//    }
//
//    private FullSentNotificationV27 waitForRequestAccepted(NewNotificationResponse response, String pollingStrategy) {
//        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
//        PnPollingResponseV27 pollingResponse = (PnPollingResponseV27) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
////        return pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();//TODO V28
//        return null;
//    }
//
//    private String waitForRequestRefused(NewNotificationResponse response, String pollingStrategy) {
//        log.info("Request status for " + response.getNotificationRequestId());
//        long startTime = System.currentTimeMillis();
//
//        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
//        PnPollingResponseV27 pollingResponse = (PnPollingResponseV27) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
//
//        long endTime = System.currentTimeMillis();
//        log.info("Execution time {}ms", (endTime - startTime));
//
//        StringBuilder error = new StringBuilder();
//        if (pollingResponse.getStatusResponse() != null
//                && pollingResponse.getStatusResponse().getErrors() != null
//                && !pollingResponse.getStatusResponse().getErrors().isEmpty()) {
//            for (ProblemError err : pollingResponse.getStatusResponse().getErrors()) {
//                error.append(" ").append(err.getDetail());
//            }
//        }
//        log.info("Detail status {}", error);
//        return error.toString();
//    }
//
//    private boolean waitForRequestNotRefused(NewNotificationResponse response, String pollingStrategy) {
//        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
//        PnPollingResponseV27 pollingResponse = (PnPollingResponseV27) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
//        return pollingResponse.getResult();
//    }
//
//    private String getPollingStrategy(String pollingStrategy) {
//        //TODO V28
//        return null;
////        return switch (pollingStrategy) {
////            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V28;
////            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V28;
////            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V28;
////            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V28;
////            case TIMELINE_SLOW_E2E -> PnPollingStrategy.TIMELINE_SLOW_E2E_V28;
////            case TIMELINE_EXTRA_RAPID -> PnPollingStrategy.TIMELINE_EXTRA_RAPID_V28;
////            case STATUS_EXTRA_RAPID -> PnPollingStrategy.STATUS_EXTRA_RAPID_V28;
////            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V28;
////            case VALIDATION_STATUS_ACCEPTATION_SHORT -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_SHORT_V28;
////            case VALIDATION_STATUS_EXTRA_RAPID -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_EXTRA_RAPID_V28;
////            case VALIDATION_STATUS_NO_ACCEPTATION -> PnPollingStrategy.VALIDATION_STATUS_NO_ACCEPTATION_V28;
////            case WEBHOOK -> PnPollingStrategy.WEBHOOK_V28;
////            default ->
////                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione V28: " + pollingStrategy);
////        };
//    }
//
//    private NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
//        PnPaB2bUtils.Pair<String, String> preloadDocument = sharedSteps.getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
//        documentSetKey(document, preloadDocument.getValue1());
//        documentSetVersionToken(document, "v1");
//        documentSetDigests(document, preloadDocument.getValue2());
//        return document;
//    }
//
//    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
//        if (attachment != null) {
//            Pair<String, String> preloadAttachment = sharedSteps.getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
//            attachmentSetKey(attachment, preloadAttachment.getValue1());
//            attachmentSetVersionToken(attachment, "v1");
//            attachmentSetDigests(attachment, preloadAttachment.getValue2());
//            return attachment;
//        }
//        return null;
//    }
//
//    public void documentSetKey(NotificationDocument notificationDocument, String key) {
//        notificationDocument.getRef().setKey(key);
//    }
//
//    public void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
//        notificationDocument.getRef().setVersionToken(version);
//    }
//
//    public void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
//        notificationDocument.digests(new NotificationAttachmentDigests().sha256(sha256));
//    }
//
//    private void attachmentSetKey(NotificationPaymentAttachment notificationPaymentAttachment, String key) {
//        notificationPaymentAttachment.getRef().setKey(key);
//    }
//
//    private void attachmentSetVersionToken(NotificationPaymentAttachment notificationPaymentAttachment, String version) {
//        notificationPaymentAttachment.getRef().setVersionToken(version);
//    }
//
//    private void attachmentSetDigests(NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
//        notificationPaymentAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
//    }
//
//    private NewNotificationResponse getAndCheckSendNewNotification(NewNotificationRequestV25 request) {
//        log.info(NEW_NOTIFICATION_REQUEST, request);
//        NewNotificationResponse response = sharedSteps.getB2bUtils().getClient().sendNewNotificationV25(request);
//        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
//        if (response != null) {
//            try {
//                log.info(NEW_NOTIFICATION_IUN, new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
//            } catch (Exception e) {
//                throw new PnB2bException(e.getMessage());
//            }
//        }
//        notificationResponse = response;
//        return response;
//    }
//
//    private void preloadPayDocument(NewNotificationRequestV25 request) throws IOException {
//        for (NotificationRecipientV24 recipient : request.getRecipients()) {
//            List<NotificationPaymentItem> paymentList = recipient.getPayments();
//            if (paymentList != null) {
//                setAttachmentWithSleep(paymentList);
//            }
//        }
//    }
//
//    private void setAttachmentWithSleep(List<NotificationPaymentItem> paymentList) throws IOException {
//        for (NotificationPaymentItem paymentInfo : paymentList) {
//            try {
//                Thread.sleep(sharedSteps.getB2bUtils().getRandom().nextInt(350));
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new PnB2bException(e.getMessage());
//            }
//            if (paymentInfo.getPagoPa() != null) {
//                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
//            }
//            if (paymentInfo.getF24() != null) {
//                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
//            }
//        }
//    }
//
//    private NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
//        if (attachment != null) {
//            Pair<String, String> preloadAttachment = sharedSteps.getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
//            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
//            metadataAttachmentSetVersionToken(attachment, "v1");
//            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
//            return attachment;
//        }
//        return null;
//    }
//
//    private void metadataAttachmentSetKey(NotificationMetadataAttachment notificationMetadataAttachment, String key) {
//        notificationMetadataAttachment.getRef().setKey(key);
//    }
//
//    private void metadataAttachmentSetVersionToken(NotificationMetadataAttachment notificationMetadataAttachment, String version) {
//        notificationMetadataAttachment.getRef().setVersionToken(version);
//    }
//
//    private void metadataAttachmentSetDigests(NotificationMetadataAttachment notificationMetadataAttachment, String sha256) {
//        notificationMetadataAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
//    }
//
//    @Override
//    public void uploadNotificationAllegatiUgualiPagamento() throws IOException {
//        List<NotificationDocument> newDocs = new ArrayList<>();
//        for (NotificationDocument doc : notificationRequest.getDocuments()) {
//            newDocs.add(preloadDocument(doc));
//        }
//        notificationRequest.setDocuments(newDocs);
//
//        for (NotificationRecipientV24 recipient : notificationRequest.getRecipients()) {
//            List<NotificationPaymentItem> paymentList = recipient.getPayments();
//            if (paymentList != null) {
//                for (NotificationPaymentItem paymentInfo : paymentList) {
//                    if (paymentInfo.getPagoPa() != null) {
//                        paymentInfo.getPagoPa().setAttachment(new NotificationPaymentAttachment()
//                                .ref(notificationRequest.getDocuments().get(0).getRef())
//                                .digests(notificationRequest.getDocuments().get(0).getDigests())
//                                .contentType(notificationRequest.getDocuments().get(0).getContentType()));
//                    }
//                    if (paymentInfo.getF24() != null) {
//                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
//                    }
//                }
//
//            }
//        }
//        notificationResponse = getAndCheckSendNewNotification(notificationRequest);
//    }
//
//    @Override
//    public void addIuvGdpToDestinatario(String denominazione, String iuvGdp, Integer paymentIndex) {
//        for (NotificationRecipientV24 recipient : this.notificationRequest.getRecipients()) {
//            if (recipient.getDenomination().equalsIgnoreCase(denominazione)) {
//                Objects.requireNonNull(Objects.requireNonNull(recipient.getPayments()).get(paymentIndex).getPagoPa()).setNoticeCode(iuvGdp);
//            }
//        }
//    }
//
//    @Override
//    public List<String> getDatiPagamento(String iun, Integer destinatario, Integer pagamento) {
//        FullSentNotificationV26 fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV26(iun);
//        return Arrays.asList(
//                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId(),
//                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
//    }
//
//    @Override
//    public void waitForTimelineElement(String iun, String timelineElementCategory, Integer attempts) {
//        TimelineElementV26 timelineElement = null;
//        for (int i = 0; i < attempts; i++) {
//            threadWait(sharedSteps.getWorkFlowWait());
//            FullSentNotificationV26 fsn = sharedSteps.getB2bClient().getSentNotificationV26(iun);
//            log.info("NOTIFICATION_TIMELINE: " + fsn.getTimeline());
//            timelineElement = fsn.getTimeline()
//                    .stream().filter(elem -> Objects.requireNonNull(elem.getCategory().getValue())
//                            .equals(TimelineElementCategoryV23.valueOf(timelineElementCategory).getValue()))
//                    .findAny().orElse(null);
//            if (timelineElement != null) {
//                break;
//            }
//        }
//        Assertions.assertNotNull(timelineElement);
//    }
//
//    @Override
//    public String getNotificationRequestId() {
//        return notificationResponse.getNotificationRequestId();
//    }
//
//    @Override
//    public void getNotificationRequestStatus(String requestId) {
//        try {
//            Assertions.assertDoesNotThrow(() -> sharedSteps.getB2bClient().getNotificationRequestStatusV25(requestId));
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }
//
//    @Override
//    public void checkTaxonomyCode() {
//        String iun = sharedSteps.getNotificationIun();
//        FullSentNotificationV26 fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV26(iun);
//        assertThat(fullSentNotification.getTaxonomyCode())
//                .as("Il taxonomyCode nella notifica inviata non dovrebbe essere nullo")
//                .isNotNull();
//
//        if (notificationRequest.getTaxonomyCode() != null) {
//            assertThat(notificationRequest.getTaxonomyCode())
//                    .as("Il taxonomyCode nella richiesta di notifica dovrebbe essere uguale al taxonomyCode nella notifica inviata")
//                    .isEqualTo(fullSentNotification.getTaxonomyCode());
//        }
//    }
//
//    @Override
//    public int getRecipientsSize() {
//        return notificationRequest.getRecipients().size();
//    }
//
//    @Override
//    public String getRecipientNoticeCode(int recipientIndex, int paymentIndex) {
//        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getNoticeCode();
//    }
//
//    @Override
//    public String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex) {
//        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getCreditorTaxId();
//    }
//
//    @Override
//    public void produceEvidence() {
//        assertThat(notificationResponse)
//                .as("La risposta della nuova notifica non dovrebbe essere nulla")
//                .isNotNull();
//        log.info("METADATI: " + '\n' + notificationResponse);
//        log.info("REQUEST-ID: " + '\n' + notificationResponse.getNotificationRequestId());
//    }
//
//    @Override
//    public void verifyCorrectAcquisition() {
//        assertSoftly(softly -> {
//            softly.assertThat(notificationResponse)
//                    .as("La risposta della nuova notifica non dovrebbe essere nulla")
//                    .isNotNull();
//
//            softly.assertThat(notificationResponse)
//                    .as("L'ID della richiesta di notifica non dovrebbe essere nullo")
//                    .isNotNull();
//
//            softly.assertThat(sharedSteps.getB2bClient().getNotificationRequestStatusV25(notificationResponse.getNotificationRequestId()))
//                    .as("Lo stato della richiesta di notifica non dovrebbe essere nullo.",
//                            notificationResponse.getNotificationRequestId())
//                    .isNotNull();
//        });
//    }
//
//    @Override
//    public void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken) {
//        String notificationRequestId = withNotificationRequestId ? notificationResponse.getNotificationRequestId() : null;
//        String paProtocolNumber = withPaProtocolNumber ? notificationResponse.getPaProtocolNumber() : null;
//        String idempotenceToken = withIdempotenceToken ? notificationResponse.getIdempotenceToken() : null;
//
//        NewNotificationRequestStatusResponseV23 newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
//                sharedSteps.getB2bClient().getNotificationRequestStatusAllParam(notificationRequestId, paProtocolNumber, idempotenceToken));
//        assertThat(newNotificationRequestStatusResponse.getNotificationRequestStatus())
//                .as("Lo stato della richiesta di notifica non dovrebbe essere nullo")
//                .isNotNull();
//        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
//    }
}
