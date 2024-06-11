package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnB2bLegalFactTextTokens;
import it.pagopa.pn.client.b2b.pa.parsing.dto.*;
import it.pagopa.pn.client.b2b.pa.parsing.design.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class PnCoreTokens extends PnCorePatterns {
    private final PnB2bLegalFactTextTokens pnB2bLegalFactTextTokens;


    public PnCoreTokens(PnB2bLegalFactTextTokens pnB2bLegalFactTextTokens) {
        this.pnB2bLegalFactTextTokens = pnB2bLegalFactTextTokens;
    }

    protected IPnLegalFact getLegalFactNotificaDowntime(String pdfText) {
        return PnLegalFactNotificaDowntime.builder()
                .dataOraDecorrenza(extractDateTimePattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataOraDecorrenza()), pdfText))
                .dataOraFine(extractDateTimePattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataOraFine()), pdfText))
                .build();
    }

    protected IPnLegalFact getLegalFactNotificaDigitale(String pdfText) {
        PnDestinatarioDigitale pnDestinatarioDigitale = new PnDestinatarioDigitale(
                extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getNomeCognomeRagioneSociale()), pdfText),
                extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getCodiceFiscale()), pdfText),
                extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDomicilioDigitale()), pdfText),
                extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getTipoDomicilioDigitale(), Pattern.DOTALL), pdfText)
        );

        return PnLegalFactNotificaDigitale.builder()
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun()), pdfText))
                .pnDestinatario(pnDestinatarioDigitale)
                .dataAttestazioneOpponibile(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText))
                .build();
    }

    protected IPnLegalFact getLegalFactNotificaMancatoRecapito(String pdfText, IPnParserService.LegalFactType legalFactType) {
        List<String> date = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText);
        List<PnDestinatarioDigitale> destinatariDigitaliList = getDestinatariDigitali(pdfText, legalFactType);
        return PnLegalFactNotificaMancatoRecapito.builder()
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun()), pdfText))
                .pnDestinatario(destinatariDigitaliList.get(0))
                .secondoDestinatarioDigitale(destinatariDigitaliList.get(1))
                .primaData(date.get(0))
                .secondaData(date.get(1))
                .dataAttestazioneOpponibile(date.get(2))
                .build();
    }

    protected IPnLegalFact getLegalFactNotificaPresaInCarico(String pdfText, IPnParserService.LegalFactType legalFactType) {
        return PnLegalFactNotificaPresaInCarico.builder()
                .dataAttestazioneOpponibile(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText))
                .mittente(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getMittente()), pdfText))
                .cfMittente(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getCfMittente()), pdfText))
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun(), Pattern.DOTALL), pdfText))
                .pnDestinatario(getDestinatariAnalogici(pdfText, legalFactType).get(0))
                .build();
    }

    protected IPnLegalFact getLegalFactNotificaPresaInCaricoMultiDestinatario(String pdfText, IPnParserService.LegalFactType legalFactType) {
        PnLegalFactNotificaPresaInCaricoMultiDestinatario multiDestinatario = PnLegalFactNotificaPresaInCaricoMultiDestinatario.builder()
                .dataAttestazioneOpponibile(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText))
                .mittente(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getMittente()), pdfText))
                .cfMittente(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getCfMittente()), pdfText))
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun(), Pattern.DOTALL), pdfText))
                .pnDestinatario(new PnDestinatario())
                .destinatariAnalogici(new ArrayList<>())
                .build();

        List<PnDestinatarioAnalogico> pnDestinatariAnalogiciList = getDestinatariAnalogici(pdfText, legalFactType);
        multiDestinatario.setPnDestinatario(pnDestinatariAnalogiciList.get(0));
        pnDestinatariAnalogiciList
                .forEach(pnDestinatarioAnalogico -> multiDestinatario.getDestinatariAnalogici().add(pnDestinatarioAnalogico));
        return multiDestinatario;
    }

    protected IPnLegalFact getLegalFactNotificaAvvenutoAccesso(String pdfText) {
        return PnLegalFactNotificaAvvenutoAccesso.builder()
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun(), Pattern.DOTALL), pdfText))
                .dataAttestazioneOpponibile(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText))
                .pnDestinatario(new PnDestinatario(
                        extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getNomeCognomeRagioneSociale()), pdfText),
                        extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getCodiceFiscale()), pdfText)))
                .build();
    }

    protected IPnLegalFact getLegalFactNotificaAvvenutoAccessoDelegato(String pdfText) {
        List<PnDestinatario> destinatarioDelegato = getDestinatarioDelegato(pdfText);
        return PnLegalFactNotificaAvvenutoAccessoDelegato.builder()
                .iun(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getIun(), Pattern.DOTALL), pdfText))
                .dataAttestazioneOpponibile(extractPattern(Pattern.compile(pnB2bLegalFactTextTokens.getDataAttestazioneOpponibile()), pdfText))
                .pnDestinatario(destinatarioDelegato.get(0))
                .delegato(destinatarioDelegato.get(1))
                .build();
    }

    private List<String> getCodiciFiscaliList(String pdfText) {
        List<String> codiciFiscali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getCodiceFiscale()), pdfText);
        List<String> codiciFiscaliPartitaIva = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getCodiceFiscalePartitaIva()), pdfText);
        codiciFiscali.addAll(codiciFiscaliPartitaIva);
        return codiciFiscali;
    }

    private List<PnDestinatario> getDestinatarioDelegato(String pdfText) {
        List<PnDestinatario> pnDestinatarioList = new ArrayList<>();
        List<String> nomiCognomi = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getNomeCognomeRagioneSociale()), pdfText);
        List<String> codiciFiscali = getCodiciFiscaliList(pdfText);
        pnDestinatarioList.add(new PnDestinatario(nomiCognomi.get(0), codiciFiscali.get(0)));
        pnDestinatarioList.add(new PnDestinatario(nomiCognomi.get(1), codiciFiscali.get(1)));
        return pnDestinatarioList;
    }

    private List<PnDestinatarioDigitale> getDestinatariDigitali(String pdfText, IPnParserService.LegalFactType legalFactType) {
        List<String> nomiCognomi = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getNomeCognomeRagioneSociale()), pdfText);
        List<String> codiciFiscali = getCodiciFiscaliList(pdfText);
        List<String> domiciliDigitali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getDomicilioDigitale()), pdfText);
        List<String> tipiDomiciliDigitali;

        if(legalFactType.equals(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO)) {
            tipiDomiciliDigitali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getTipoDomicilioDigitaleMancatoRecapito()), pdfText);
        } else {
            tipiDomiciliDigitali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getTipoDomicilioDigitale()), pdfText);
        }
        return getDestinatariDigitaliList(nomiCognomi, codiciFiscali, domiciliDigitali, tipiDomiciliDigitali);
    }

    private List<PnDestinatarioDigitale> getDestinatariDigitaliList(List<String> nomiCognomi, List<String> codiciFiscali, List<String> domiciliDigitali, List<String> tipiDomiciliDigitali) {
        List<PnDestinatarioDigitale> list = new ArrayList<>();
        for(int i = 0; i < nomiCognomi.size(); i++) {
            list.add(new PnDestinatarioDigitale(
                    nomiCognomi.get(i),
                    codiciFiscali.get(i),
                    domiciliDigitali.get(i),
                    tipiDomiciliDigitali.get(i)
            ));
        }
        return list;
    }

    private List<PnDestinatarioAnalogico> getDestinatariAnalogici(String pdfText, IPnParserService.LegalFactType legalFactType) {
        List<String> nomiCognomi = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getNomeCognomeRagioneSociale()), pdfText);
        List<String> codiciFiscali = getCodiciFiscaliList(pdfText);
        List<String> domiciliDigitali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getDomicilioDigitale()), pdfText);
        List<String> tipiDomiciliDigitali = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getTipoDomicilioDigitaleNotificaPresaInCarico()), pdfText);
        List<String> indirizziFisici;

        if(legalFactType.equals(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO)) {
            indirizziFisici = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getIndirizzoFisicoNotificaPresaInCarico()), pdfText);
        } else {
            indirizziFisici = extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getIndirizzoFisicoNotificaPresaInCaricoMultiDestinatario()), pdfText);
            indirizziFisici.addAll(extractPatternList(Pattern.compile(pnB2bLegalFactTextTokens.getIndirizzoFisicoNotificaPresaInCarico()), pdfText.split(pnB2bLegalFactTextTokens.getIndirizzoFisicoNotificaPresaInCaricoMultiDestinatario())[1]));
        }

        List<PnDestinatarioDigitale> pnDestinatariDigitaliList = getDestinatariDigitaliList(nomiCognomi, codiciFiscali, domiciliDigitali, tipiDomiciliDigitali);
        List<PnDestinatarioAnalogico> pnDestinatariAnalogicList = new ArrayList<>();
        for(int i = 0; i < pnDestinatariDigitaliList.size(); i++) {
            pnDestinatariAnalogicList.add((new PnDestinatarioAnalogico(
                    pnDestinatariDigitaliList.get(i).getNomeCognomeRagioneSociale(),
                    pnDestinatariDigitaliList.get(i).getCodiceFiscale(),
                    pnDestinatariDigitaliList.get(i).getDomicilioDigitale(),
                    pnDestinatariDigitaliList.get(i).getTipoDomicilioDigitale(),
                    indirizziFisici.get(i))));
        }
        return pnDestinatariAnalogicList;
    }

    protected HashMap<String, Object> getDestinatario(IPnLegalFact pnLegalFact, boolean isDelegato) {
        HashMap<String, Object> result = new HashMap<>();
        if(pnLegalFact instanceof PnLegalFactNotificaDigitale notificaDigitale) {
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), notificaDigitale.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), notificaDigitale.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
        } else if(pnLegalFact instanceof PnLegalFactNotificaMancatoRecapito notificaMancatoRecapito) {
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), notificaMancatoRecapito.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), notificaMancatoRecapito.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
        } else if(pnLegalFact instanceof PnLegalFactNotificaAvvenutoAccessoDelegato avvenutoAccessoDelegato) {
            if(isDelegato) {
                result.put(IPnParserService.LegalFactKeyValues.DELEGATO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), avvenutoAccessoDelegato.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
                result.put(IPnParserService.LegalFactKeyValues.DELEGATO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), avvenutoAccessoDelegato.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
            } else {
                result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), avvenutoAccessoDelegato.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
                result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), avvenutoAccessoDelegato.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
            }
        } else if(pnLegalFact instanceof PnLegalFactNotificaAvvenutoAccesso avvenutoAccesso) {
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), avvenutoAccesso.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), avvenutoAccesso.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
        } else if(pnLegalFact instanceof PnLegalFactNotificaPresaInCaricoMultiDestinatario notificaPresaInCaricoMultiDestinatario) {
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), notificaPresaInCaricoMultiDestinatario.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), notificaPresaInCaricoMultiDestinatario.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
        } else if(pnLegalFact instanceof PnLegalFactNotificaPresaInCarico notificaPresaInCarico) {
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), notificaPresaInCarico.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField()));
            result.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), notificaPresaInCarico.getAllLegalFactValues().get(IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField()));
        }
        return result;
    }

    protected HashMap<String, Object> getDelegato(PnLegalFactNotificaAvvenutoAccessoDelegato avvenutoAccessoDelegato) {
        return getDestinatario(avvenutoAccessoDelegato, true);
    }

    protected HashMap<String, Object> getSecondoDestinatarioDigitale(PnLegalFactNotificaMancatoRecapito mancatoRecapito) {
        return new HashMap<>(mancatoRecapito.getSecondoDestinatarioDigitaleValues());
    }

    protected HashMap<String, Object> getDestinatariAnalogici(PnLegalFactNotificaPresaInCaricoMultiDestinatario presaInCaricoMultiDestinatario) {
        return new HashMap<>(presaInCaricoMultiDestinatario.getDestinatariAnalogiciValues());
    }
}