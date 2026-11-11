# Generatori del flusso di lavoro QA

Questa cartella raccoglie gli strumenti che producono documenti e altri artefatti
durante il flusso di lavoro. Ogni generatore ha una directory autonoma con codice,
configurazione, dipendenze, test e istruzioni di esecuzione.

| Generatore | Quando utilizzarlo | Input | Output |
| --- | --- | --- | --- |
| [PST](pst/README.md) | Preparazione e aggiornamento dei casi di test delle release in corso | `releases/`, branch Git e scenari Gherkin | `pst/pst_<qa_epic>.md`, HTML opzionale |

I percorsi di input e output della tabella sono relativi alla radice del repository.

## Aggiungere un generatore

Creare una cartella accanto a `pst/` con un nome che descriva l'artefatto prodotto.
Includere un README con scopo, prerequisiti, comandi, input, output e comportamento
in caso di errore, insieme alle dipendenze e ai test necessari. Aggiornare la tabella.

- Mantenere nella directory del generatore la configurazione di trasformazione.
  Le informazioni sulle release e sul lavoro restano in `releases/`.
- Salvare gli artefatti prodotti nella directory di output documentata, separata
  dal codice. Usare `tests/fixtures/` per gli esempi necessari ai test.
- Esporre un comando eseguibile sia in locale sia in CI. Il workflow in
  `.github/workflows/` prepara l'ambiente, richiama il comando e gestisce i risultati.
- Dichiarare e installare le dipendenze per singolo generatore. Non è richiesto
  che tutti i generatori utilizzino lo stesso linguaggio.
- Estrarre moduli condivisi quando almeno due generatori ne hanno necessità concreta.

Il generatore PST è eseguibile in locale e tramite il workflow manuale
[`Generate PST`](../../.github/workflows/generate-pst.yml), da integrare in `develop`.
Per ora produce artifact scaricabili. La strategia concordata per PR dei risultati
verso `develop` e impronte persistenti è documentata nel
[README PST](pst/README.md#strategia-futura--da-implementare) come lavoro futuro.
