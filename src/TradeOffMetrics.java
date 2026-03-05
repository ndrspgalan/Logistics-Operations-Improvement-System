public class TradeOffMetrics {

    // --- Capacity metrics ---
    private int capacityAccepted;
    private int capacityRejected;

    // --- Compensation metrics ---
    private int compensationAccepted;
    private int compensationsTriggered;

    // --- Manual intervention metrics ---
    private int manualAccepted;
    private int manualInterventions;

    // ------ Record operations -------

    public void recordCapacityAccepted() {
        capacityAccepted++;
    }

    public void recordCapacityRejected() {
        capacityRejected++;
    }

    public void recordCompensationAccepted() {
        compensationAccepted++;
    }

    public void recordCompensation() {
        compensationsTriggered++;
    }

    public void recordManualAccepted() {
        manualAccepted++;
    }

    public void recordManualIntervention() {
        manualInterventions++;
    }

    // ------ Metric calculations ------

    //Percentage of rejected operations
    public double rejectionRate() {

        int total = capacityAccepted + capacityRejected;

        if (total == 0) {
            return 0.0;
        }

        return round((double) capacityRejected / total);
    }

    //Percentage of compensating actions
    public double compensationRate() {

        int total = compensationAccepted + compensationsTriggered;

        if(total == 0) {
            return 0.0;
        }

        return round((double) compensationsTriggered / total);
    }


    //Manual intervention intensity
    public double manualInterventionRate() {

        int total = manualAccepted + manualInterventions;

        if (total == 0) {
            return 0.0;
        }

        return round((double) manualInterventions / total);
    }

    // ------ Helper ------

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ------ Output ------

    public void printSummary() {

        System.out.println("\n=== Trade-Off Metrics ===");

        System.out.println(
                "Capacity rejection rate: "
                        + rejectionRate()
                        + " (" + capacityRejected + "/" + (capacityAccepted + capacityRejected) + ")"
        );

        System.out.println(
                "Compensation rate: "
                        + compensationRate()
                        + " (" + compensationsTriggered + "/" + (compensationAccepted + compensationsTriggered) + ")"
        );

        System.out.println(
                "Manual intervention rate: "
                        + manualInterventionRate()
                        + " (" + manualInterventions + "/" + (manualAccepted + manualInterventions) + ")"
        );
    }
}

/**
 * This class exposes the trade-offs of the system in measurable form.
 *
 * The goal is NOT operational monitoring, but to make explicit
 * the cost of design decisions such as:
 * - Conservative capacity rules.
 * - Compensating flows.
 * - Manual confirmation steps.
 *
 * These metrics illustrate the system philosophy:
 * safety and traceability are prioritized over maximum throughput.
 */

