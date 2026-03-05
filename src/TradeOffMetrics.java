public class TradeOffMetrics {

    //Number of successful operations
    private int operationsAccepted;

    //Number of operations rejected due to safety or domain constraints
    private int operationsRejected;

    //Number of compensating actions triggered
    private int compensationsTriggered;

    //Number of manual decisions required from the operator
    private int manualInterventions;

    //Record a successful operation
    public void recordAccepted() {
        operationsAccepted++;
    }

    //Record a rejected operation
    public void recordRejected() {
        operationsRejected++;
    }

    //Record a compensating action
    public void recordCompensation() {
        compensationsTriggered++;
    }

    //Record a manual decision
    public void recordManualIntervention() {
        manualInterventions++;
    }

    //Percentage of rejected operations
    public double rejectionRate() {

        int total = operationsAccepted + operationsRejected;

        if (total == 0) {
            return 0.0;
        }

        return (double) compensationsTriggered / total;
    }

    //Percentage of compensating actions
    public double compensationRate() {

        int total = operationsAccepted + compensationsTriggered;

        if(total == 0) {
            return 0.0;
        }

        return (double) compensationsTriggered / total;
    }


    //Manual intervention intensity
    public double manualInterventionRate() {

        int total = operationsAccepted + manualInterventions;

        if (total == 0) {
            return 0.0;
        }

        return (double) manualInterventions / total;
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

