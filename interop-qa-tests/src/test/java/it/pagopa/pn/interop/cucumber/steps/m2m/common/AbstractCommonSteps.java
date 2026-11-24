package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import static org.assertj.core.api.Assertions.assertThat;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public abstract class AbstractCommonSteps<T, K> implements ICommonSteps {


    protected final List<T> expectedEntities = new ArrayList<>();
    protected final List<T> actualEntities = new ArrayList<>();
    protected final List<T> unexpectedEntities = new ArrayList<>();

    private final String parameterTypeName;
    protected final IClient<T, K> client;
    private final SharedStepsContext context;

    protected AbstractCommonSteps(String parameterTypeName, IClient<T, K> client, SharedStepsContext context) {
        this.parameterTypeName = parameterTypeName;
        this.client = client;
        this.context = context;
    }

    public void verifyByHttpStatus(int expectedStatusCode) {
        int actualStatusCode = context.getHttpCallExecutor().getResponseStatus().value();

        boolean isMatch = actualStatusCode == expectedStatusCode;

        if (isMatch) {
            assertThat(context.getHttpCallExecutor().getResponse())
                    .as("Il body della response dovrebbe essere valorizzato per status code %s".formatted(expectedStatusCode))
                    .isNotNull();
        }

        assertExpectedEntity(isMatch ? AssertCheckType.PRESENT : AssertCheckType.NONE);
    }

    public void getBy(Map<String, String> filters) {
        getAll();
        List<T> filtered = actualEntities.stream()
                .filter(entity -> matchesFilters(entity, filters))
                .collect(Collectors.toList());
        setActualEntities(filtered);
    }

    public void getAll() {
        setActualEntities(client.getAll());
    }

    public void getPage(int page, int size) {
        setActualEntities(client.getPage(page, size));
    }

    public void getByFirstExpectedId() {
        getByIdType(null);
    }

    public void getByIdType(EntityIdType entityIdType) {
        K id = generateId(entityIdType);
        T result = client.get(id);
        setActualEntities(result != null ? List.of(result) : List.of());
    }

    public K generateId(EntityIdType entityIdType) {
        if (entityIdType != null) return client.generateId(entityIdType);

        updateExpected();
        assertThat(expectedEntities)
                .as("Expected entities should contain exactly one element to extract the ID")
                .hasSize(1);
        return client.getId(expectedEntities.get(0));
    }

    public void exsist(String presence) {
        AssertCheckType mode = switch (presence.toLowerCase()) {
            case "viene" -> AssertCheckType.PRESENT;
            case "non" -> AssertCheckType.NONE;
            case "match" -> AssertCheckType.PRESENT_AND_MATCHING;
            default -> throw new IllegalArgumentException("Unsupported presence: " + presence);
        };

        assertExpectedEntity(mode);
    }

    // --- Support methods ---
    private void assertExpectedEntity(AssertCheckType mode) {
        updateExpected();

        switch (mode) {
            case NONE -> assertThat(actualEntities)
                    .as("La lista dei %s dovrebbe essere vuota".formatted(parameterTypeName))
                    .isNullOrEmpty();

            case PRESENT -> assertThat(actualEntities)
                    .as("La lista dei %s dovrebbe essere presente".formatted(parameterTypeName))
                    .isNotEmpty();

            case PRESENT_AND_MATCHING -> {

                assertThat(actualEntities)
                        .as("La lista dei %s dovrebbe essere presente".formatted(parameterTypeName))
                        .isNotEmpty();

                boolean allMatched = expectedEntities.stream()
                        .allMatch(expected -> actualEntities.stream()
                                .anyMatch(actual -> areEqual(expected, actual)));

                assertThat(allMatched)
                        .as("I %s restituiti dovrebbero corrispondere a quelli pubblicati (con confronto personalizzato)".formatted(parameterTypeName))
                        .isTrue();
            }

            case EXPECTED_NOT_PRESENT -> {
                updateUnexpected();

                assertThat(actualEntities)
                        .as("La lista actual dei %s dovrebbe essere presente".formatted(parameterTypeName))
                        .isNotNull();

                assertThat(unexpectedEntities)
                        .as("La lista unexpected dei %s dovrebbe essere presente".formatted(parameterTypeName))
                        .isNotEmpty();

                boolean noneMatched = unexpectedEntities.stream()
                        .noneMatch(unexpected -> actualEntities.stream()
                                .anyMatch(actual -> areEqual(unexpected, actual)));

                assertThat(noneMatched)
                        .as("Nessuno dei %s attesi dovrebbe essere presente tra gli attuali".formatted(parameterTypeName))
                        .isTrue();
            }
        }
    }

    private boolean matchesFilters(T entity, Map<String, String> filters) {
        Class<?> clazz = entity.getClass();

        for (var entry : filters.entrySet()) {
            try {
                Field field = clazz.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                Object value = field.get(entity);

                if (value == null || !value.toString().equals(entry.getValue())) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return false;
            }
        }

        return true;
    }

    private boolean areEqual(T expected, T actual) {
        try {
            return isEqual(expected, actual);
        } catch (UnsupportedOperationException e) {
            return expected.equals(actual);
        }
    }

    protected boolean isEqual(T a, T b) {
        throw new UnsupportedOperationException("Confronto custom non implementato");
    }

    private void updateExpected() {
        expectedEntities.clear();
        expectedEntities.addAll(bindExpected(context));
    }

    private void updateUnexpected() {
        unexpectedEntities.clear();
        unexpectedEntities.addAll(bindUnexpected(context));
    }

    // --- Utility setters ---
    private void setActualEntities(List<T> list) {
        actualEntities.clear();
        if (list != null && !list.isEmpty()) {
            actualEntities.addAll(list);
        }
        bindActual(context, actualEntities);
    }

    // --- Abstract methods to be implemented ---
    public abstract void bindActual(SharedStepsContext context, List<T> actualEntities);

    public abstract List<T> bindExpected(SharedStepsContext context);

    public List<T> bindUnexpected(SharedStepsContext context) {
        throw new UnsupportedOperationException("Bind con unexpectedEntitis non implementato");
    }
}
