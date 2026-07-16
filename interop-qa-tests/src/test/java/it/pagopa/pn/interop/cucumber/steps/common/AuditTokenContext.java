package it.pagopa.pn.interop.cucumber.steps.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditTokenContext {

    private static final String JWT_ID = "jti";

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> payload = new HashMap<>();

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addPayload(String key, String value) {
        payload.put(key, value);
    }

    public String getJwtId() {
        return payload.get(JWT_ID);
    }


    static public boolean hasField(Map<String, Object> source, String field) {
        return resolveFieldValue(source, field) != null;
    }

    static public Object resolveFieldValue(Map<String, Object> source, String field) {

        if (source == null || field == null || field.isBlank()) {
            return null;
        }
        try {
            final ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext(source);
            context.addPropertyAccessor(new MapAccessor());
            return parser.parseExpression(field).getValue(context);
        } catch (Exception e) {
            return null;
        }
    }
}