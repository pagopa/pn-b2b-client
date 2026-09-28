package it.pagopa.pn.cucumber.streamversion.isolation;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionResolver;
import org.junit.jupiter.api.Assertions;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Verifica tecnica del collegamento suite -> hook di versione -> StreamVersionContext, senza chiamate remote.
 */
public class StreamVersionIsolationSteps {

    // Deve coincidere con il numero di Examples dei feature in streamversion-isolation
    private static final int CONCURRENT_SCENARIOS = 4;
    private static final CyclicBarrier CONCURRENCY_BARRIER = new CyclicBarrier(CONCURRENT_SCENARIOS);
    private static final Set<String> CONTEXT_INSTANCE_IDS = ConcurrentHashMap.newKeySet();

    private final StreamVersionContext streamVersionContext;
    private StreamVersion versionSeenByDefaultOrderHook;
    private String instanceIdAtScenarioStart;

    public StreamVersionIsolationSteps(StreamVersionContext streamVersionContext) {
        this.streamVersionContext = streamVersionContext;
    }

    @Before
    public void captureContextBeforeSteps() {
        versionSeenByDefaultOrderHook = streamVersionContext.isInitialized()
                ? streamVersionContext.getRequiredStreamVersion()
                : null;
        instanceIdAtScenarioStart = streamVersionContext.getInstanceId();
    }

    @Given("gli scenari di verifica della versione dello stream sono in esecuzione contemporaneamente")
    public void scenariosAreRunningConcurrently() throws InterruptedException, BrokenBarrierException {
        try {
            CONCURRENCY_BARRIER.await(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Assertions.fail("Gli scenari non sono stati eseguiti in parallelo: attesi " + CONCURRENT_SCENARIOS + " scenari concorrenti");
        }
    }

    @Then("l'hook di versione ha inizializzato il contesto prima degli hook con ordine predefinito")
    public void versionHookRunsFirst() {
        Assertions.assertNotNull(versionSeenByDefaultOrderHook,
                "Il contesto non era inizializzato quando è stato eseguito un hook con ordine predefinito");
    }

    @And("la versione dello stream definita dalla suite è {string}")
    public void suiteStreamVersionIs(String expectedVersion) {
        Assertions.assertEquals(StreamVersionResolver.parse(expectedVersion), StreamVersionResolver.resolve(null, streamVersionContext));
        Assertions.assertEquals(StreamVersionResolver.parse(expectedVersion), versionSeenByDefaultOrderHook);
    }

    @And("il contesto della versione è un'istanza propria dello scenario")
    public void contextInstanceBelongsToScenario() {
        String instanceId = streamVersionContext.getInstanceId();
        Assertions.assertEquals(instanceIdAtScenarioStart, instanceId,
                "L'istanza del contesto è cambiata durante lo scenario");
        Assertions.assertTrue(CONTEXT_INSTANCE_IDS.add(instanceId),
                "Istanza del contesto condivisa con un altro scenario: " + instanceId);
    }
}
