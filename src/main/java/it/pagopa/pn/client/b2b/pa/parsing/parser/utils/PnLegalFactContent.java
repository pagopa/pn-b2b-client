package it.pagopa.pn.client.b2b.pa.parsing.parser.utils;

import static it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnContentExtractorUtils.countDuplicates;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokenProperty;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.model.IPnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.parser.impl.PnContentExtractor;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PnLegalFactContent {
    private final PnLegalFactTokenProperty tokenProperty;
    private final PnContentExtractor contentExtractor;


    public PnLegalFactContent(PnLegalFactTokens pnLegalFactTokens) {
        this.tokenProperty = pnLegalFactTokens.getTokenProps();
        this.contentExtractor = new PnContentExtractor(pnLegalFactTokens);
    }

    public PnParserRecord.PnParserContent extractContent(byte[] source, IPnParserService.LegalFactType legalFactType) {
        return contentExtractor.extractContent(source, legalFactType);
    }

    protected IPnDestinatario getDestinatario(PnParserRecord.PnParserContent content, boolean isDestinatarioDigitale, boolean isDestinatarioAnalogico, boolean isWithNotificaDigitale, boolean isWithNotificaMultiDestinatario) {
        if(isDestinatarioDigitale) {
            return new PnDestinatarioDigitale(
                    getNomeCognomeRagioneSociale(content, true),
                    getCodiceFiscale(content, false, true),
                    getDomicilioDigitale(content),
                    isWithNotificaDigitale
                    ?
                        getTipologiaDomicilioDigitale(content, false, true)
                    :
                        getTipologiaDomicilioDigitale(content, false, false));
        } else if(isDestinatarioAnalogico) {
            return new PnDestinatarioAnalogico(
                    getNomeCognomeRagioneSociale(content, true),
                    getCodiceFiscale(content, false, true),
                    getDomicilioDigitale(content),
                    getTipologiaDomicilioDigitale(content, true, false),
                    isWithNotificaMultiDestinatario
                    ?
                            getIndirizzoFisico(content, true)
                    :
                            getIndirizzoFisico(content, false)
            );
        }
        return new PnDestinatario(getNomeCognomeRagioneSociale(content, true),
                getCodiceFiscale(content, false, false));
    }

    protected List<PnDestinatarioAnalogico> getDestinatariAnalogici(PnParserRecord.PnParserContent content) {
        List<PnDestinatarioAnalogico> pnDestinatarioAnalogicoList = new ArrayList<>();
        String text = content.text();
        List<String> valueList = content.valueList();
        int cntDestinatario = countDuplicates(text, tokenProperty.getNomeCognomeRagioneSocialeStart1());

        for(int i = 1; i <= cntDestinatario; i++) {
            String nomeCognomeRagioneSociale = getNomeCognomeRagioneSociale(content, true);
            String codiceFiscale = getCodiceFiscale(content, false, true);
            String domicilioDigitale = getDomicilioDigitale(content);
            String tipologiaDomicilio = getTipologiaDomicilioDigitale(content, true, false);
            String indirizzoFisico;
            if (i == cntDestinatario) {
                indirizzoFisico = getIndirizzoFisico(content, false);
            } else {
                indirizzoFisico = getIndirizzoFisico(content, true);
            }
            pnDestinatarioAnalogicoList.add(
                    new PnDestinatarioAnalogico(nomeCognomeRagioneSociale,
                            codiceFiscale,
                            domicilioDigitale,
                            contentExtractor.cleanUp(tipologiaDomicilio, true),
                            contentExtractor.cleanUp(indirizzoFisico, true)));

            int slidingIndex = text.indexOf(indirizzoFisico) + indirizzoFisico.length();
            text = text.substring(slidingIndex);
            for (String element : Arrays.asList(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipologiaDomicilio, indirizzoFisico)) {
                valueList.remove(element);
            }
        }
        return pnDestinatarioAnalogicoList;
    }

    protected IPnDestinatario getDestinatarioOrDelegato(PnParserRecord.PnParserContent content, boolean isDestinatario) {
        if(isDestinatario) {
            return new PnDestinatario(getNomeCognomeRagioneSociale(content, true),
                    getCodiceFiscale(content, true, false));
        }
        return new PnDestinatario(getNomeCognomeRagioneSociale(content, false),
                getCodiceFiscale(content, false, false));
    }

    protected String getIun(PnParserRecord.PnParserContent content, boolean isWithNotificaPresaInCarico) {
        if (isWithNotificaPresaInCarico) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getIunStart())
                    .tokenEnd(tokenProperty.getIunEnd2())
                    .build(), content.valueList()), false);
        }
        return contentExtractor.cleanUp(
                contentExtractor.getField(PnTextSlidingWindow.builder()
                .originalText(content.text())
                .slidedText(content.text())
                .tokenStart(tokenProperty.getIunStart())
                .tokenEnd(tokenProperty.getIunEnd1())
                .build(), content.valueList()), false);
    }

    protected String getNomeCognomeRagioneSociale(PnParserRecord.PnParserContent content, boolean isDestinatario) {
        if(isDestinatario) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getNomeCognomeRagioneSocialeStart1())
                    .tokenEnd(tokenProperty.getNomeCognomeRagioneSocialeEnd())
                    .build(), content.valueList()), true);
        } else {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getNomeCognomeRagioneSocialeStart2())
                    .tokenEnd(tokenProperty.getNomeCognomeRagioneSocialeEnd())
                    .build(), content.valueList()), true);
        }
    }

    protected String getDataAttestazioneOpponibile(PnParserRecord.PnParserContent content,
                                                 boolean isWithNotificaAvvenutoAccesso,
                                                 boolean isWithNotificaAvvenutoAccessoDelegato,
                                                 boolean isWithNotificaPresaInCaricoAndMultiDestinatario,
                                                 boolean isWithNotificaDigitale,
                                                 boolean isWithNotificaMancatoRecapito) {
        if(isWithNotificaAvvenutoAccesso) {
            return cleanDataAttestazioneOpponibile(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataAttestazioneOpponibileStart())
                    .tokenEnd(tokenProperty.getDataAttestazioneOpponibileEnd1())
                    .build(), content.valueList()));
        } else if(isWithNotificaAvvenutoAccessoDelegato) {
            return cleanDataAttestazioneOpponibile(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataAttestazioneOpponibileStart())
                    .tokenEnd(tokenProperty.getDataAttestazioneOpponibileEnd2())
                    .build(), content.valueList()));
        } else if(isWithNotificaPresaInCaricoAndMultiDestinatario) {
            return cleanDataAttestazioneOpponibile(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataAttestazioneOpponibileStart())
                    .tokenEnd(tokenProperty.getDataAttestazioneOpponibileEnd3())
                    .build(), content.valueList()));
        } else if(isWithNotificaDigitale) {
            return cleanDataAttestazioneOpponibile(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataAttestazioneOpponibileStart())
                    .tokenEnd(tokenProperty.getDataAttestazioneOpponibileEnd4())
                    .build(), content.valueList()));
        } else if(isWithNotificaMancatoRecapito){
            return cleanDataAttestazioneOpponibile(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataAttestazioneOpponibileStart())
                    .tokenEnd(tokenProperty.getDataAttestazioneOpponibileEnd5())
                    .build(), content.valueList()));
        }
        return null;
    }

    protected String getCodiceFiscale(PnParserRecord.PnParserContent content, boolean isDestinatario, boolean isDestinatarioDigitaleOrAnalogico) {
        if(isDestinatario) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getCodiceFiscaleStart())
                    .tokenEnd(tokenProperty.getCodiceFiscaleEnd2())
                    .build(), content.valueList()), false);
        } else if(isDestinatarioDigitaleOrAnalogico) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getCodiceFiscaleStart())
                    .tokenEnd(tokenProperty.getCodiceFiscaleEnd3())
                    .build(), content.valueList()), false);
        } else {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getCodiceFiscaleStart())
                    .tokenEnd(tokenProperty.getCodiceFiscaleEnd1())
                    .build(), content.valueList()), false);
        }
    }

    protected String getDomicilioDigitale(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                contentExtractor.getField(PnTextSlidingWindow.builder()
                .originalText(content.text())
                .slidedText(content.text())
                .tokenStart(tokenProperty.getDomicilioDigitaleStart())
                .tokenEnd(tokenProperty.getDomicilioDigitaleEnd())
                .build(), content.valueList()), false);
    }

    protected String getTipologiaDomicilioDigitale(PnParserRecord.PnParserContent content, boolean isDestinatarioAnalogico, boolean isDestinatarioDigitale) {
        if(isDestinatarioAnalogico) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getTipologiaDomicilioDigitaleStart())
                    .tokenEnd(tokenProperty.getTipologiaDomicilioDigitaleEnd1())
                    .build(), content.valueList()), true);
        } else if(isDestinatarioDigitale) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getTipologiaDomicilioDigitaleStart())
                    .tokenEnd(tokenProperty.getTipologiaDomicilioDigitaleEnd2())
                    .build(), content.valueList()), true);
        } else {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getTipologiaDomicilioDigitaleStart())
                    .tokenEnd(tokenProperty.getTipologiaDomicilioDigitaleEnd3())
                    .build(), content.valueList()), true);
        }
    }

    protected String getIndirizzoFisico(PnParserRecord.PnParserContent content, boolean isDestinatarioAnalogico) {
        if(isDestinatarioAnalogico) {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getIndirizzoFisicoStart())
                    .tokenEnd(tokenProperty.getIndirizzoFisicoEnd1())
                    .build(), content.valueList()), true);
        } else {
            return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getIndirizzoFisicoStart())
                    .tokenEnd(tokenProperty.getIndirizzoFisicoEnd2())
                    .build(), content.valueList()), true);
        }
    }

    protected String getDataOraDecorrenza(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                        .originalText(content.text())
                        .slidedText(content.text())
                        .tokenStart(tokenProperty.getDataDecorrenzaStart())
                        .tokenEnd(tokenProperty.getDataDecorrenzaEnd())
                        .build(), content.valueList()), true)
                        .concat(" ")
                        .concat(contentExtractor.cleanUp(
                                    contentExtractor.getField(PnTextSlidingWindow.builder()
                                    .originalText(content.text())
                                    .slidedText(content.text())
                                    .tokenStart(tokenProperty.getOraDecorrenzaStart())
                                    .tokenEnd(tokenProperty.getOraDecorrenzaEnd())
                                    .build(), content.valueList()), true));
    }

    protected String getDataOraFineDecorrenza(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getDataFineDecorrenzaStart())
                    .tokenEnd(tokenProperty.getDataFineDecorrenzaEnd())
                    .build(), content.valueList()), true)
                    .concat(" ")
                    .concat(contentExtractor.cleanUp(
                                contentExtractor.getField(PnTextSlidingWindow.builder()
                                .originalText(content.text())
                                .slidedText(content.text())
                                .tokenStart(tokenProperty.getOraFineDecorrenzaStart())
                                .tokenEnd(tokenProperty.getOraFineDecorrenzaEnd())
                                .build(), content.valueList()), true));
    }

    protected String getMittente(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getMittenteStart())
                    .tokenEnd(tokenProperty.getMittenteEnd())
                    .build(), content.valueList()), true);
    }

    protected String getCfMittente(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getCfMittenteStart())
                    .tokenEnd(tokenProperty.getCfMittenteEnd())
                    .build(), content.valueList()), false);
    }

    protected String getPrimaData(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getPrimaDataStart())
                    .tokenEnd(tokenProperty.getPrimaDataEnd())
                    .discardValue(getDataAttestazioneOpponibile(content, false, false, false, false, true))
                    .build(), content.valueList()), true);
    }

    protected String getSecondaData(PnParserRecord.PnParserContent content) {
        return contentExtractor.cleanUp(
                    contentExtractor.getField(PnTextSlidingWindow.builder()
                    .originalText(content.text())
                    .slidedText(content.text())
                    .tokenStart(tokenProperty.getSecondaDataStart())
                    .tokenEnd(tokenProperty.getSecondaDataEnd())
                    .build(), content.valueList()), true);
    }

    protected String cleanDataAttestazioneOpponibile(String dataAttestazioneOpponibile) {
        String[] splitted = dataAttestazioneOpponibile.split(" ");
        StringBuilder cleaned = new StringBuilder();
        for (String value: splitted) {
            cleaned.append(contentExtractor.cleanUp(value, false));
            cleaned.append(" ");
        }
        return contentExtractor.cleanUp(cleaned.toString().trim(), true);
    }
}