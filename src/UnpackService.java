import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnpackService {

    private final ReturnEligibilityPolicy eligibilityPolicy;
    private final ExpirationMatchPolicy expirationMatchPolicy;

    public UnpackService(ReturnEligibilityPolicy eligibilityPolicy,
                         ExpirationMatchPolicy expirationMatchPolicy) {
        this.eligibilityPolicy = Objects.requireNonNull(eligibilityPolicy,"eligibilityPolicy must not be null");
        this.expirationMatchPolicy = Objects.requireNonNull(expirationMatchPolicy,"expirationMatchPolicy must not be null");
    }

    /**
     * Entry point: scan SLAM and decide flow.
     */
    public UnpackResult start(ReturnedBagManifest manifest, LocalDate today) {
        Objects.requireNonNull(manifest, "manifest must not be null");
        Objects.requireNonNull(today, "today must not be null");

        // CHILLED / FROZEN: automatic DESTROY, no confirmations
        if (manifest.getBagType() == BagType.CHILLED || manifest.getBagType() == BagType.FROZEN) {
            return UnpackResult.destroyAll(manifest);
        }

        //LIGHT_AMBIENT / HARD_AMBIENT: present manifest for selection
        return UnpackResult.awaitingSelection(manifest);
    }

    /**
     * Confirm selection of quantities to unpack (Confirmation 1).
     */
    public void confirmSelections(List<UnpackSelection> selections) {
        for (UnpackSelection s : selections) {
            s.confirm();
        }
    }

    /**
     * Build a disposition plan, validating REINTRODUCE eligibility.
     */
    public DispositionPlan buildPlan(List<UnpackSelection> selections, List<Disposition> dispositions, LocalDate today) {
        if (selections.size() != dispositions.size()) {
            throw new IllegalArgumentException("selections and dispositions must match");
        }

        DispositionPlan plan = new DispositionPlan();

        for (int i = 0; i < selections.size(); i++) {
            UnpackSelection sel = selections.get(i);
            Disposition disp = dispositions.get(i);

            if (sel.getConfirmationStatus() != ConfirmationStatus.CONFIRMED) {
                throw new IllegalStateException("All selection must be confirmed before building the plan");
            }

            ReturnedItemLine line = sel.getLine();

            if (disp == Disposition.REINTRODUCE) {
                boolean eligible = eligibilityPolicy.canReintroduce(line, today);
                if (!eligible) {
                    throw new IllegalStateException("Item is not eligible for REINTRODUCE (date too close or policy restriction)");
                }
            }

            plan.add(new PlannedDisposition(sel, disp));
        }

        return plan;
    }

    /**
    * Confirm plan (Confirmation 2) and execute it.
     */
    public ExecutionResult confirmAndExecute(DispositionPlan plan) {
        if (plan.getConfirmationStatus() == ConfirmationStatus.CONFIRMED) {
            throw new IllegalStateException("Plan already confirmed");
        }

        plan.confirm();

        List<ExecutedLine> executed = new ArrayList<>();

        for (PlannedDisposition entry : plan.getEntries()) {
            executed.add(new ExecutedLine(
                    entry.getSelection().getLine(),
                    entry.getSelection().getQuantityToUnpack(),
                    entry.getDisposition()
            ));
        }

        return new ExecutionResult(executed, true); // true => bag becomes EMPTY
    }

    /**
     * Helper to find bins for REINTRODUCE respecting expiration-date matching.
     */
    public List<BinStock> findEligibleBinsForReintroduce(ReturnedItemLine line, List<BinStock> allBins) {
        return expirationMatchPolicy.findEligibleBins(
                line.getProduct().getSku(),
                line.getExpirationDate(),
                allBins
        );
    }
}
