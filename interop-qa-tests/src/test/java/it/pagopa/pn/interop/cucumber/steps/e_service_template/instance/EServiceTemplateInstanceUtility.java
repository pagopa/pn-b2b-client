package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

public class EServiceTemplateInstanceUtility {

    public static String parseSuffix(String suffix) {
        String instanceLabel;
        if (suffix == null || suffix.equals("%null")) {
            instanceLabel = null;
        } else if (suffix.equals("%space")) {
            instanceLabel = " ";
        } else {
            instanceLabel = suffix;
        }
        return instanceLabel;
    }
}
