package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CertifiedAttributeSeedMapper {

    @DataTableType
    public CertifiedAttributeSeed mapCertifiedAttributeSeed(Map<String, String> entry) {
        CertifiedAttributeSeed certifiedAttributeSeed = new CertifiedAttributeSeed();

        // Recupera e imposta il nome (generato automaticamente se assente)
        String name = entry.get("name");
        String actualName = (name == null || name.isBlank())
                ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE))
                : name;
        certifiedAttributeSeed.setName(actualName);

        // Imposta la descrizione: se assente, imposta una descrizione di default
        String description = entry.get("description");
        String actualDescription = (description == null || description.isBlank())
                ? "Descrizione automatica per attributo: " + actualName
                : description;
        certifiedAttributeSeed.setDescription(actualDescription);

        // Recupera e imposta il codice (generato automaticamente se richiesto)
        String code = entry.get("code");
        String actualCode = (code == null || code.isBlank())
                ? generateUniqueAttributeCode()
                : code;
        certifiedAttributeSeed.setCode(actualCode);

        return certifiedAttributeSeed;
    }

    private String generateUniqueAttributeCode() {
        final String prefix = "unique_code";
        long timestamp = System.currentTimeMillis();
        return prefix + "_" + timestamp;
    }
}
