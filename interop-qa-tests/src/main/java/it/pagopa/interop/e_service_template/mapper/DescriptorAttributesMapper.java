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
 * https://mapstruct.org/documentation/spring-extensions/reference/html/ */
@Mapper(componentModel = "spring")
public interface DescriptorAttributesMapper {
    DescriptorAttributeSeed map(DescriptorAttribute attribute);
    List<DescriptorAttributeSeed> mapAttributeList(List<DescriptorAttribute> attributes);
    List<List<DescriptorAttributeSeed>> mapAttributeListOfList(List<List<DescriptorAttribute>> attributes);
    DescriptorAttributesSeed map(DescriptorAttributes attribute);

    DescriptorAttributeSeed map(EServiceTemplateVersionAttributeSeed seed);
    List<DescriptorAttributeSeed> mapSeedList(List<EServiceTemplateVersionAttributeSeed> attributes);
    List<List<DescriptorAttributeSeed>> mapSeedListOfList(List<List<EServiceTemplateVersionAttributeSeed>> attributes);
    DescriptorAttributesSeed map(EServiceTemplateAttributesSeed seed);

}
