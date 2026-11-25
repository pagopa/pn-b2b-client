package it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils;


import io.cucumber.datatable.DataTable;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableEService;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResourceRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplateLinkEServiceTemplate;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.LinkableResourcesContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LinkableResourcesHelper {

    public record LinkParameters (
            UUID purposeTemplateId,
            LinkableResourceRequest resourceRequest,
            PurposeTemplateLinkEServiceTemplate eServiceTemplateLink
    ) {
        public UUID resourceId() {
            return this.resourceRequest.getEserviceId() != null ? this.resourceRequest.getEserviceId() : this.resourceRequest.getEserviceTemplateId();
        }

        public String resourceKind() {
            return this.resourceRequest.getEserviceId() != null ? "ESERVICE" : "ESERVICE_TEMPLATE";
        }
    }

    static public LinkParameters getLinkParametersFromDataTable(
            DataTable dataTable,
            SharedStepsContext sharedStepsContext,
            LinkableResourcesContext linkableResourcesContext
    ) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(
                resolveDynamicData(data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext)
        );
        String eServiceIdValue = data.getOrDefault("id_e_service", "");
        LinkableResourceRequest resourceRequest = new LinkableResourceRequest();
        PurposeTemplateLinkEServiceTemplate eServiceTemplateLink = new PurposeTemplateLinkEServiceTemplate();
        if (!eServiceIdValue.isEmpty()) {
            resourceRequest.setResourceKind(LinkableResourceRequest.ResourceKindEnum.fromValue("ESERVICE"));
            UUID eServiceId = UUID.fromString(resolveDynamicData(
                    eServiceIdValue, sharedStepsContext, linkableResourcesContext)
            );
            resourceRequest.setEserviceId(eServiceId);
        } else {
            resourceRequest.setResourceKind(LinkableResourceRequest.ResourceKindEnum.fromValue("ESERVICE_TEMPLATE"));
            UUID eServiceTemplateId = UUID.fromString(resolveDynamicData(
                    data.get("id_e_service_template"), sharedStepsContext, linkableResourcesContext
            ));
            resourceRequest.setEserviceTemplateId(eServiceTemplateId);
            eServiceTemplateLink.setEserviceTemplateId(eServiceTemplateId);
        }
        return new LinkParameters(purposeTemplateId, resourceRequest, eServiceTemplateLink);
    }

    public static boolean foundResourceKindInLinkableResources(String eServiceKindName, List<LinkableResource> resources) {
        eServiceKindName = (eServiceKindName.equals("e-service template")) ? "ESERVICE_TEMPLATE" : "ESERVICE";
        boolean foundRequestedResourceKind = false;
        for (int i = 0; i < resources.size(); i++) {
            if (getResourceKind(resources.get(i)).equals(eServiceKindName)) {
                foundRequestedResourceKind = true;
                break;
            }
        }
        return foundRequestedResourceKind;
    }

    static public boolean doLinkableResourcesMatch(LinkableResource resource1, LinkableResource resource2) {
        String resource1Kind = getResourceKind(resource1);
        String resource2Kind = getResourceKind(resource2);
        if (resource1Kind.equals(resource2Kind)) {
            if ("ESERVICE_TEMPLATE".equals(resource1Kind)) {
                LinkableEServiceTemplate eserviceTemplate1 = (LinkableEServiceTemplate)resource1;
                LinkableEServiceTemplate eserviceTemplate2 = (LinkableEServiceTemplate)resource2;
                return eserviceTemplate1.getPurposeTemplateId().equals(eserviceTemplate2.getPurposeTemplateId()) &&
                        eserviceTemplate1.getCreatedAt().equals(eserviceTemplate2.getCreatedAt());
            } else {
                LinkableEService eservice1 = (LinkableEService)resource1;
                LinkableEService eservice2 = (LinkableEService)resource2;
                return eservice1.getEservice().getId().equals(eservice2.getEservice().getId()) &&
                        eservice1.getCreatedAt().equals(eservice2.getCreatedAt());
            }
        } else {
            return false;
        }
    }

    static public boolean doLinkableEServiceTemplatesMatch(EServiceTemplate resource1, EServiceTemplate resource2) {
        return resource1.getId().equals(resource2.getId()) && resource1.getName().equals(resource2.getName());
    }

    static public void assertLinkableResourcesMatch(boolean difference, LinkableResource resource1, LinkableResource resource2) {
        Assertions.assertNotNull(resource1);
        Assertions.assertNotNull(resource2);
        Assertions.assertFalse(
                difference,
                "Resource " + getResourceKind(resource2) + " " + resource2.getPurposeTemplateId() +
                " does not match to resource " + getResourceKind(resource1) + " " + resource1.getPurposeTemplateId());
    }

    static public void assertLinkableEServiceTemplatesMatch(boolean difference, EServiceTemplate resource1, EServiceTemplate resource2) {
        Assertions.assertNotNull(resource1);
        Assertions.assertNotNull(resource2);
        Assertions.assertFalse(
                difference,
                "Resource E-SERVICE TEMPLATE " + resource2.getId() +
                " does not match to resource E-SERVICE TEMPLATE " + resource1.getId());
    }

    public static String getResourceKind(LinkableResource resource) {
        return (resource instanceof LinkableEServiceTemplate) ? "ESERVICE_TEMPLATE" : "ESERVICE";
    }

    public static String resolveDynamicData(
            String value,
            SharedStepsContext sharedStepsContext,
            LinkableResourcesContext linkableResourcesContext
    ) {
        // Gestire il caso in cui il valore sia una lista di valori separati da virgola
        String[] values = value.split(",");
        for (int i = 0; i < values.length; i++) {
            if (values[i].startsWith("$DA_CONTESTO(")) {
                values[i] = values[i].substring(13, values[i].length() - 1);

                switch (values[i]) {
                    case "purposeTemplateId":
                        return sharedStepsContext.getPurposeTemplateContext().getPurposeTemplateId().toString();
                    case "eServiceId":
                        return sharedStepsContext.getEServicesCommonContext().getEserviceId().toString();
                    case "eServiceTemplateId":
                        return sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId().toString();
                    default:
                        String methodName = "get" + values[i].substring(0, 1).toUpperCase() + values[i].substring(1);
                        try {
                            Method getterMethod = LinkableResourcesContext.class.getMethod(methodName);
                            values[i] = (String)getterMethod.invoke(linkableResourcesContext);

                        } catch (NoSuchMethodException e) {
                            final Pattern itemListSuffix = Pattern.compile("_\\d+$");
                            Matcher matcher = itemListSuffix.matcher(methodName);
                            if (matcher.find()) {
                                methodName = methodName.substring(0, methodName.length() - matcher.group(0).length()) + "s";
                                try {
                                    Method getterMethod = LinkableResourcesContext.class.getMethod(methodName);
                                    List<String> valueList = (List<String>) getterMethod.invoke(linkableResourcesContext);
                                    int itemIndex = Integer.parseInt(matcher.group(0).substring(1)) - 1;
                                    Object item = valueList.get(itemIndex);

                                    if (item instanceof String) {
                                        values[i] = (String) item;

                                    } else if (item instanceof UUID) {
                                        values[i] = item.toString();
                                    }
                                } catch (NoSuchMethodException e2) {
                                    log.error("Specified method for list " + methodName + " does not exist in LinkableResourcesContext.");

                                } catch (Exception e2) {
                                    e.printStackTrace();
                                }
                            } else {
                                log.error("Specified method " + methodName + " does not exist in LinkableResourcesContext.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        }
        return String.join(",", values);
    }
}
