package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AttributeSeedMapper<T> {
    private Function<AttributePrototype, T> prototypeMapper;

    public T mapAttributeSeed(Map<String, String> entry) {
        // Recupera e imposta il nome (generato automaticamente se assente)
        String name = entry.get("name");
        String actualName = (name == null || name.isBlank())
                ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                : name;

        // Imposta la descrizione: se assente, imposta una descrizione di default
        String description = entry.get("description");
        String actualDescription = (description == null || description.isBlank())
                ? "Descrizione automatica per attributo: " + actualName
                : description;

        // Recupera e imposta il codice (generato automaticamente se richiesto)
        String code = entry.get("code");
        String actualCode = (code == null || code.isBlank())
                ? generateUniqueAttributeCode()
                : code;
        AttributePrototype attributePrototype = AttributePrototype.of(actualName, actualDescription,
            actualCode);
        return this.prototypeMapper.apply(attributePrototype);
    }

    private String generateUniqueAttributeCode() {
        final String prefix = "unique_code";
        long timestamp = System.currentTimeMillis();
        return prefix + "_" + timestamp;
    }
}
