package it.pagopa.pn.interop.cucumber.steps.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditTokenContext {

    private static final String JWT_ID = "jti";

    public enum TokenType { CLIENT_ASSERTION, VOUCHER_REQUEST, DPOP_PROOF}
    private Map<TokenType, JWTUtils.JWTPojo> accessTokens2 = new HashMap<>();

    public JWTUtils.JWTPojo getDecodedToken(TokenType key) {
        return accessTokens2.get(key);
    }

    public void setToken(TokenType tokenType, String accessToken) {
        if(accessToken != null && !accessToken.isBlank()) {
            this.accessTokens2.put(tokenType, JWTUtils.decodeJwt(accessToken));
        }
    }

    public String getJwtId() {
        return this.getDecodedToken(TokenType.VOUCHER_REQUEST).getPayload().get(JWT_ID).toString();
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

    static public Object resolveFieldValue(LinkedHashMap<String, Object> source, String field) {
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

    static public Object resolveFieldValue(JsonNode source, String field) {
        if (source == null || field == null || field.isBlank()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> convertedSource = mapper.convertValue(source, Map.class);
            return resolveFieldValue(convertedSource, field);
        } catch (Exception e) {
            return null;
        }
    }
}