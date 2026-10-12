package it.pagopa.pn.cucumber.streamversion.context;

import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import org.junit.jupiter.api.Test;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.MOST_RECENT;
import static org.junit.jupiter.api.Assertions.*;

class StreamVersionResolverTest {

    @Test
    void explicitVersionIsUsedEvenWhenSuiteVersionIsDefined() {
        StreamVersionContext context = new StreamVersionContext();
        context.initialize(StreamVersion.V29);

        assertEquals(StreamVersion.V10, StreamVersionResolver.resolve("V10", context));
        assertEquals(StreamVersion.V28, StreamVersionResolver.resolve(" v28 ", context));
    }

    @Test
    void explicitVersionDoesNotRequireSuiteVersion() {
        assertEquals(StreamVersion.V23, StreamVersionResolver.resolve("V23", new StreamVersionContext()));
    }

    @Test
    void mostRecentIsResolvedToLatestVersion() {
        assertEquals(StreamVersionResolver.MOST_RECENT_VERSION, StreamVersionResolver.parse(MOST_RECENT));
        assertEquals(StreamVersion.V29, StreamVersionResolver.MOST_RECENT_VERSION);
    }

    @Test
    void missingVersionIsReadFromSuiteContext() {
        StreamVersionContext context = new StreamVersionContext();
        context.initialize(StreamVersion.V28);

        assertEquals(StreamVersion.V28, StreamVersionResolver.resolve(null, context));
    }

    @Test
    void missingVersionWithoutSuiteContextFails() {
        StreamVersionContext context = new StreamVersionContext();

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> StreamVersionResolver.resolve(null, context));
        assertTrue(e.getMessage().contains("non definita dalla suite"));
    }

    @Test
    void invalidVersionFailsWithExplicitMessage() {
        StreamVersionContext context = new StreamVersionContext();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> StreamVersionResolver.resolve("V99", context));
        assertTrue(e.getMessage().contains("V99"));
        assertThrows(IllegalArgumentException.class, () -> StreamVersionResolver.resolve(" ", context));
    }

    @Test
    void contextCanBeInitializedOnlyOnce() {
        StreamVersionContext context = new StreamVersionContext();
        context.initialize(StreamVersion.V28);

        assertThrows(IllegalStateException.class, () -> context.initialize(StreamVersion.V29));
        assertThrows(IllegalStateException.class, () -> context.initialize(StreamVersion.V28));
        assertEquals(StreamVersion.V28, context.getRequiredStreamVersion());
    }

    @Test
    void contextRejectsNullVersion() {
        StreamVersionContext context = new StreamVersionContext();

        assertThrows(IllegalArgumentException.class, () -> context.initialize(null));
        assertFalse(context.isInitialized());
    }

    @Test
    void eachContextHasItsOwnInstanceId() {
        assertNotEquals(new StreamVersionContext().getInstanceId(), new StreamVersionContext().getInstanceId());
    }
}
