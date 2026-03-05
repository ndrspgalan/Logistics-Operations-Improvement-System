import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class PickingWindow {
    private final String id;
    private final Instant deadline;
    private final Set<Product> products;

    private PickingWindowStatus status;

    // New: dynamic capacity
    private int maxCapacity;    // can be adjusted in real time
    private int usedCapacity;   // current assigned workload

    public PickingWindow(String id, Instant deadline, Set<Product> products, int maxCapacity) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.deadline = Objects.requireNonNull(deadline, "deadline must not be null");
        this.products = Objects.requireNonNull(products, "products must not be null");

        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be > 0");
        }

        this.maxCapacity = maxCapacity;
        this.usedCapacity = 0;
        this.status = PickingWindowStatus.OPEN;
    }

    public String getId() {
        return id;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public PickingWindowStatus getStatus() {
        return status;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    /**
     * Capacity can change in real time due to staffing, incidents, etc.
     */
    public void updateMaxCapacity(int newMaxCapacity) {
        if (newMaxCapacity <= 0) throw new IllegalArgumentException("newMaxCapacity must be > 0");
        this.maxCapacity = newMaxCapacity;
    }

    public boolean hasFreeCapacity() {
        return usedCapacity < maxCapacity;
    }

    /**
     * A window can still accept new orders unless it is URGENT or MISSING,
     * and only if it has free capacity.
     */
    public boolean canAcceptNewOrders() {
        if (status == PickingWindowStatus.MISSING || status == PickingWindowStatus.URGENT) {
            return false;
        }
        return hasFreeCapacity();
    }

    /**
     * Recomputes the operational status based on time.
     * Rules:
     * - deadline passed            -> MISSING.
     * - < 30 minutes remaining     -> URGENT.
     * - < 3 hours remaining        -> CONFIRMED.
     * - otherwise                  -> OPEN.
     */
    public void refreshStatus(Instant now) {
        if (status == PickingWindowStatus.MISSING) {
            return; // terminal from planning perspective.
        }

        Duration timeLeft = Duration.between(now, deadline);

        if (timeLeft.isZero() || timeLeft.isNegative()) {
            status = PickingWindowStatus.MISSING;
            return;
        }

        if (timeLeft.toMinutes() < 30) {
            status = PickingWindowStatus.URGENT;
        } else if (timeLeft.toHours() < 3)  {
            status = PickingWindowStatus.CONFIRMED;
        } else {
            status = PickingWindowStatus.OPEN;
        }
    }

    /**
     * A window can be picked only if it is CONFIRMED or URGENT.
     */
    public boolean canBePickedNow() {
        return status == PickingWindowStatus.CONFIRMED || status == PickingWindowStatus.URGENT;
    }

    /**
     * Mixing rules:
     * - If either window is URGENT -> no mixing.
     * - Only CONFIRMED windows can be mixed.
     * - They must share at least one product.
     */
    public boolean canMixWith(PickingWindow other) {
        Objects.requireNonNull(other, "other window must not be null");

        if (this.status == PickingWindowStatus.URGENT || other.status == PickingWindowStatus.URGENT) {
            return false;
        }

        if (this.status != PickingWindowStatus.CONFIRMED || other.status != PickingWindowStatus.CONFIRMED) {
            return false;
        }

        return sharesAtLeastOneProductWith(other);
    }

    private boolean sharesAtLeastOneProductWith(PickingWindow other) {
        for (Product product : this.products) {
            if (other.products.contains(product)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Business event:
     * A confirmed order is canceled (<= 2h window).
     * The affected products must go to a manual/return flow.
     * We model this by marking the window as MISSING.
     */
    public void cancelConfirmedOrder(Set<Product> cancelledProducts) {

        /**
         * Idempotency guard.
         *
         * Cancellation events may be delivered more than once
         * in distributed system or retry scenarios.
         *
         * If the window is already marked as MISSING,
         * the operation has already been processed.
         */
        if (this.status == PickingWindowStatus.MISSING) {
            return;
        }

        if (this.status == PickingWindowStatus.CONFIRMED
                || this.status == PickingWindowStatus.URGENT) {

            this.products.removeAll(cancelledProducts);
            this.status = PickingWindowStatus.MISSING;
        }
    }
}