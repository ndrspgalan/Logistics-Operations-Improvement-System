import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The ScenarioRunner also acts as a small operational simulator.
 *
 * It allows the model to surface trade-offs such as:
 * - Rejected loads.
 * - Compensating actions.
 * - Manual interventions.
 *
 * Metrics are scenario-driven rather than statistically modeled.
 */

public class ScenarioRunner {

    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println(" Logistics Operations System Simulation ");
        System.out.println("=======================================\n");

        Instant simulationStart = Instant.now();

        //Trade-off metrics collector
        TradeOffMetrics metrics = new TradeOffMetrics();

        runKnownRisksScenario(new TradeOffMetrics());
        runScaleX10Scenario(new TradeOffMetrics());

        simulateOperationalMetrics(metrics);

        metrics.printSummary();

        Duration simulationDuration = Duration.between(simulationStart,Instant.now());

        System.out.println("\nSimulation runtime: " + simulationDuration.toMillis() + " ms");

        System.out.println("\n=== Simulation Completed ===");
    }

    /**
     * Domain invariant validation helper.
     *
     * ScenarioRunner is not a unit-test suite.
     * Instead, it validates that important operational
     * invariants remain true under simulated scenarios.
     *
     * If an invariant fails, the scenario is considered invalid.
     */
    private static void assertInvariant(boolean condition, String message) {

        if (!condition) {
            throw new IllegalStateException(
                    "Invariant violated: " + message
            );
        }
    }

    private static void runKnownRisksScenario(TradeOffMetrics metrics) {
        System.out.println("=== Scenario 1: Known Risks ===");

        try {
            riskLateCancellation(metrics);
        } catch (Exception e) {
            System.out.println("Scenario 1.1 failed with exception: " + e.getMessage());
        }

        try {
            riskCapacityOverflow(metrics);
        } catch (Exception e) {
            System.out.println("Scenario 1.2 failed with exception: " + e.getMessage());
        }

        System.out.println("=== End of Scenario 1 ===");
        System.out.println("Scenario 1 Summary: Operational failure states are explicitly surfaced. \n");
    }

    /**
     * Risk case:
     * A late cancellation happens on a confirmed window.
     * The window must move to MISSING and require manual handling.
     */
    private static void riskLateCancellation(TradeOffMetrics metrics) {
        System.out.println("Late cancellation on confirmed window");

        Product apple = new Product("APPLE", 0.2, 2, false);
        Product banana = new Product("BANANA", 0.3, 3, false);

        Set<Product> products = new HashSet<>();
        products.add(apple);
        products.add(banana);

        PickingWindow window = new PickingWindow(
                "W-1",
                Instant.now().plus(Duration.ofHours(1)), // < 3h => CONFIRMED
                products,
                100 // maxCapacity > 0
        );

        window.refreshStatus(Instant.now());

        System.out.println("Initial status: " + window.getStatus());


        window.cancelConfirmedOrder(Set.of(apple));

        System.out.println("Status after cancellation: " + window.getStatus());

        assertInvariant(
                window.getStatus() == PickingWindowStatus.MISSING,
                "Confirmed window cancellation must transition to MISSING"
        );

        System.out.println("Risk: Late cancellations create operational exceptions that require manual workflows.");
    }

    /** Risk case:
     * Capacity policy rejects a load that does not fit.
     * This simulates misconfiguration or sudden load spikes.
     */
    private static void riskCapacityOverflow(TradeOffMetrics metrics) {
        System.out.println("Capacity overflow on window planning");

        Product water = new Product("WATER", 1.0, 1, false);

        CapacityPolicy capacityPolicy = new EffectiveCapacityPolicy();

        // First: small load, should fit
        boolean fitsSmall = capacityPolicy.fits(
                BagType.LIGHT_AMBIENT,
                List.of(water),
                20.0
        );

        System.out.println("Small load fits: " + fitsSmall);

        //Now simulate a much bigger load (scale up the same product)
        List<Product> bigLoad = List.of (
                water, water, water, water, water,
                water, water, water, water, water,
                water, water, water, water, water,
                water, water, water, water, water
        );

        boolean fitsBig = capacityPolicy.fits(
                BagType.LIGHT_AMBIENT,
                bigLoad,
                20
        );

        if (!fitsBig) {

            metrics.recordCapacityRejected();

            System.out.println("Capacity overflow detected by policy");
            assertInvariant(
                    !fitsBig,
                    "Oversized load must be rejected by capacity policy"
            );
            System.out.println("Risk: Capacity misconfiguration or sudden load spikes can block planning.");
        } else {

            metrics.recordCapacityAccepted();

            System.out.println("Unexpected: big load still fits.");
        }
    }

    private static void runScaleX10Scenario(TradeOffMetrics metrics) {
        System.out.println("\n=== Scenario 2: Scale X 10 ===");

        try {
            scaleWindowSelection(metrics);
        } catch (Exception e) {
            System.out.println("Scenario 2.1 failed with exception: " + e.getMessage());
        }

        try {
            scaleExpirationMatching(metrics);
        } catch (Exception e) {
            System.out.println("Scenario 2.2 failed with exception: " + e.getMessage());
        }

        try {
            scaleCapacityChecks(metrics);
        } catch (Exception e) {
            System.out.println("Scenario 2.3 failed with exception: " + e.getMessage());
        }

        System.out.println("=== End of Scenario 2 ===");
        System.out.println("Scenario 2 Summary: Structural behavior remains deterministic under increased scale.\n");
    }

    private static void scaleWindowSelection(TradeOffMetrics metrics) {
        System.out.println("Window selection at scale");

        PickingHorizonPolicy  horizonPolicy = new PickingHorizonPolicy();

        // We simulate that pick in advance is allowed because today's picking it has ended
        horizonPolicy.setAllowPickInAdvance(true);

        Set<Product> dummyProducts = Set.of(new Product("SKU-1", 1.0, 100, false));

        List<PickingWindow> windows = new java.util.ArrayList<>();

        Instant now = Instant.now();

        // We create more windows with different deadlines
        for (int i = 0; i < 50; i++) {
            Instant deadline = now.plus(Duration.ofHours(5 + i)); // we escalate it in the future
            PickingWindow w = new PickingWindow("W-" + i, deadline, new java.util.HashSet<>(dummyProducts), 100);
            w.refreshStatus(now);
            windows.add(w);
        }

        // We force some windows to no accept NewOrder simulating their capacity at 0.
        windows.get(0).updateMaxCapacity(1);
        windows.get(0).updateMaxCapacity(0); // now it has not free capacity

        PickingWindow selected = horizonPolicy.selectWindowForNewOrder(windows);

        System.out.println("Selected window: " + selected.getId() + " with status " + selected.getStatus());
        System.out.println("Scale note: At x10 scale, window selection becomes a hot path and should be indexed / pre-sorted.");
    }

    private static void scaleExpirationMatching(TradeOffMetrics metrics) {
        System.out.println("Expiration-date matching at scale");

        ExpirationMatchPolicy policy = new ExpirationMatchPolicy();

        String sku = "MILK_1L";
        LocalDate targetDate = LocalDate.now().plusDays(10);

        List<BinStock> allBins = new java.util.ArrayList<>();

        // We create many bins with different dates
        for (int i = 0; i < 100; i++) {
            LocalDate date = LocalDate.now().plusDays(i % 15); //15 different dates
            allBins.add(new BinStock("BIN-" + i, sku, date));
        }

        List<BinStock> matches = policy.findEligibleBins(sku, targetDate, allBins);

        System.out.println("Total bins scanned: " + allBins.size());
        System.out.println("Matching bins found: " + matches.size());
        assertInvariant(
                matches.stream().allMatch(b -> b.getExpirationDate().equals(targetDate)),
                "Expiration matching must not suggest bins with different expiration dates"
        );
        System.out.println("Scale note: At x10 scale, this O(n) scan should be optimized by grouping by SKU+date.");
    }

    private static void scaleCapacityChecks(TradeOffMetrics metrics) {
        System.out.println("Capacity checks at scale");

        CapacityPolicy capacityPolicy = new EffectiveCapacityPolicy();

        Product item = new Product("WATER", 1.0, 1000, false);

        // Small load
        List<Product> smallLoad = List.of(item, item, item);

        boolean fitsSmall = capacityPolicy.fits(BagType.LIGHT_AMBIENT, smallLoad, 20);
        System.out.println("Small load fits: " + fitsSmall);

        if (fitsSmall) {
            metrics.recordCapacityAccepted();
        }

        // Big load (x10)
        List<Product> bigLoad = new java.util.ArrayList<>();
        for (int i = 0; i < 30; i++) {
            bigLoad.add(item);
        }

        boolean fitsBig = capacityPolicy.fits(BagType.LIGHT_AMBIENT, bigLoad, 20);

        if (!fitsBig) {
            metrics.recordCapacityRejected();
        }

        System.out.println("Big load fits: " + fitsBig);

        System.out.println("At x10 scale, capacity evaluation becomes critical for planning throughput.");
    }

    private static void simulateOperationalMetrics(TradeOffMetrics metrics) {

        //capacity decisions (10 operations total)
        for (int i = 0; i < 8; i++) metrics.recordCapacityAccepted();
        for (int i = 0; i < 2; i++) metrics.recordCapacityRejected();

        //bag compensation (100 total)
        for (int i = 0; i < 3; i++) metrics.recordCompensation();
        for (int i = 0; i < 97; i++) metrics.recordCompensationAccepted();

        //manual interventions (100 total)
        for (int i = 0; i < 42; i++) metrics.recordManualIntervention();
        for (int i = 0; i < 58; i++) metrics.recordManualAccepted();
    }
        /**
         * These values are scenario-driven and illustrate the trade-offs
         * introduced by the model.
         *
         * They are not intended to represent real operational statistics,
         * but to demonstrate how system rules influence operational outcomes.
         */
    private static void scenario(String title) {
        System.out.println("\n===" + title + "===");
    }

    private static void risk(String message) {
        System.out.println("\n[risk]" + message);
    }

    private static void scale(String message) {
        System.out.println("\n[scale]" + message);
    }

    private static void note(String message) {
        System.out.println("\n[Note]" + message);
    }

    private static void Summary(String message) {
        System.out.println("\n[Summary]" + message);
    }
}
