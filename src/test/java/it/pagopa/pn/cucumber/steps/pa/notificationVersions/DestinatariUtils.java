package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class DestinatariUtils {

    @Value("${pn.bearer-token.user1.taxID}")
    public static String MARIO_CUCUMBER_TAX_ID;// = "FRMTTR76M06B715E";
    @Value("${pn.bearer-token.user2.taxID}")
    public static String MARIO_GHERKIN_TAX_ID;// = "CLMCST42R12D969Z";
    @Value("${pn.bearer-token.user4.taxID}")
    public static String GALILEO_GALILEI_TAX_ID;// = "GLLGLL64B15G702I";
    public static final String CUCUMBER_SRL_TAX_ID = "20517490320";
    public static final String GHERKIN_SRL_TAX_ID = "12666810299";
    public static final String CUCUMBER_SPA_TAX_ID = "20517490320";
    public static final String GHERKIN_SPA_TAX_ID = "12666810299";
    public static final String CUCUMBER_ANALOGIC_TAX_ID = "LBPHLS94A56C826R";
    public static final String GHERKIN_ANALOGIC_TAX_ID = "05722930657";
    public static final String CUCUMBER_SOCIETY_TAX_ID = "20517490320";
    public static final String GHERKIN_IRREPERIBILE_TAX_ID = "00749900049";


    public static final Map<String, String> MARIO_CUCUMBER = new HashMap<>() {{
        put("denomination", "Mario Cucumber");
        put("senderTaxId", MARIO_CUCUMBER_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> MARIO_GHERKIN = new HashMap<>() {{
        put("denomination", "Mario Gherkin");
        put("senderTaxId", MARIO_GHERKIN_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> CUCUMBER_SRL = new HashMap<>() {{
        put("denomination", "Cucumber Srl");
        put("senderTaxId", CUCUMBER_SRL_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> GHERKIN_SRL = new HashMap<>() {{
        put("denomination", "Gherkin Srl");
        put("senderTaxId", GHERKIN_SRL_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> CUCUMBER_SPA = new HashMap<>() {{
        put("denomination", "Cucumber Spa");
        put("senderTaxId", CUCUMBER_SPA_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> GHERKIN_SPA = new HashMap<>() {{
        put("denomination", "Gherkin Spa");
        put("senderTaxId", GHERKIN_SPA_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> CUCUMBER_SOCIETY = new HashMap<>() {{
        put("denomination", "Cucumber Society");
        put("senderTaxId", CUCUMBER_SOCIETY_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> CUCUMBER_ANALOGIC = new HashMap<>() {{
        put("denomination", "Cucumber Society");
        put("senderTaxId", CUCUMBER_SOCIETY_TAX_ID);
        put("recipientType", null);
    }};

    public static final Map<String, String> GHERKIN_ANALOGIC = new HashMap<>() {{
        put("denomination", "Gherkin Spa");
        put("senderTaxId", GHERKIN_SPA_TAX_ID);
        put("recipientType", null);
    }};


}
