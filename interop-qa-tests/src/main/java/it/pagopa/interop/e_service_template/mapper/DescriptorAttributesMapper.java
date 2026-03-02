package it.pagopa.interop.e_service_template.mapper;

import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import java.util.List;
import org.mapstruct.Mapper;

/* TODO sarebbe il caso di usare direttamente l'estensione per Spring
 *  https://mapstruct.org/documentation/spring-extensions/reference/html/ */
/* TODO ogni mapper, per configurazione, dovrebbe fallire la generazione in caso di campi non
*   mappati (invece di restituire un warning */
@Mapper(componentModel = "spring")
public interface DescriptorAttributesMapper {
    /* TODO non è utile che i metodi necessari ai passaggi intermedi - come mapSeedListOfList - siano pubblici.
     *   Riformulare interfaccia come classe astratta con metodi privati. */

    DescriptorAttributeSeed map(DescriptorAttribute attribute);
    List<DescriptorAttributeSeed> mapAttributeList(List<DescriptorAttribute> attributes);
    List<List<DescriptorAttributeSeed>> mapAttributeListOfList(List<List<DescriptorAttribute>> attributes);
    DescriptorAttributesSeed map(DescriptorAttributes attribute);

    DescriptorAttributeSeed mapSeedToSeed(EServiceTemplateVersionAttributeSeed seed);
    List<DescriptorAttributeSeed> mapListSeedToSeed(List<EServiceTemplateVersionAttributeSeed> seed);
    List<List<DescriptorAttributeSeed>> mapListOfListSeedToSeed(List<List<EServiceTemplateVersionAttributeSeed>> seed);
    DescriptorAttributesSeed mapSeedsToSeeds(EServiceTemplateAttributesSeed seed);

    EServiceTemplateVersionAttributeSeed mapAttributeToSeed(DescriptorAttribute seed);
    List<EServiceTemplateVersionAttributeSeed> mapListAttributeToSeed(List<DescriptorAttribute> seed);
    List<List<EServiceTemplateVersionAttributeSeed>> mapListOfListAttributeToSeed(List<List<DescriptorAttribute>> seed);
    EServiceTemplateAttributesSeed mapAttributesToSeeds(DescriptorAttributes seed);
}
