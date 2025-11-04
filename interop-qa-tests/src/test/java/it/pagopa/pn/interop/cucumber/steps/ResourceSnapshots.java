package it.pagopa.pn.interop.cucumber.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/* Questa classe è usata per tenere traccia della "situazione attuale" di un certo insieme di
 * risorse. Esempio: se voglio assicurarmi che un'operazione sugli attributi certificati non
 * abbia compromesso gli attributi creati in precedenza, posso usare questa classe per memorizzare
 * la situazione precedente l'operazione, e poi quella successiva, così da fare il confronto.
 * Trattandosi di un insieme di risorse, è comune che il tipo T corrisponda a una Collection.  */
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ResourceSnapshots<T> {
    private List<T> snapshots = new ArrayList<>();

    public ResourceSnapshots(T snapshot) {
        this.addSnapshot(snapshot);
    }

    public void addSnapshot(T snapshot) {
        this.snapshots.add(snapshot);
    }

    public T getActualSnapshot() {
        return getSnapshot(snapshots.size() - 1);
    }

    public T getPreviousSnapshot() {
        return getSnapshot(snapshots.size() - 2);
    }

    public T getSnapshot(int index) {
        if(snapshots.size() < index + 1) {
            throw new NoSuchElementException("Non è presente una snapshot di indice %d".formatted(index));
        }

        return snapshots.get(index);
    }
}
