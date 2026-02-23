package it.pagopa.pn.interop.cucumber.utility.property_resolver;

import static java.util.Objects.requireNonNull;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;

/* 09/01/2026 La classe al momento è in grado di risolvere un ristretto subset di properties.
 * Una strategia alternativa al mapping esplicito proposto sotto consisterebbe in un parsing
 * dinamico del path in input. */
@Component
@ScenarioScope
@AllArgsConstructor
public class PropertyResolver {
    private static final Map<String, String> propertiesMap = Map.of(
        "e-service:id", "EServicesCommonContext.eserviceId" // 09/01/2026 non chiaro perché PropertyUtils lo inquadri con la maiuscola iniziale
    );

    private final SharedStepsContext rootContext;

    public String getContextProperty(String propertyPath) {
        try {
            String effectivePropertyPath = requireNonNull(
                propertiesMap.get(propertyPath),
                "Property path non supportato. Considerare di aggiungerlo alla configurazione di questa classe.");
            Object property = PropertyUtils.getProperty(rootContext, effectivePropertyPath);
            return property.toString();
        } catch (Exception e) {
            throw new PropertyResolvingException("Errore nella risoluzione della context property", e);
        }
    }
}
