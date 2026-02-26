import java.util.List;
import java.util.Objects;

public class PickingHorizonPolicy {

    private boolean allowPickInAdvance; //manual flag

    public void setAllowPickInAdvance(boolean allowPickInAdvance) {
        this.allowPickInAdvance = allowPickInAdvance;
    }

    /**
     * Rule:
     * - While there is any unpicked work for today -> DO NOT allow tomorrow windows.
     * - If today is fully picked AND allowPickInAdvance == true -> allow tomorrow windows.
     */

    public boolean canExposeTomorrowWindows(boolean todayFullyPicked) {
        return todayFullyPicked && allowPickInAdvance;
    }

    /**
     * Selects the next window to assign new workload:
     * - Choose the closest-to-URGENT window that:
     *    - canAcceptNewOrders() == true
     */
    public PickingWindow selectWindowForNewOrder(List<PickingWindow> windows) {
        Objects.requireNonNull(windows, "windows must not be null");

        return windows.stream()
                .filter(PickingWindow::canAcceptNewOrders)
                // simplistic ordering: URGENT last, then CONFIRMED, then OPEN
                .sorted((a, b) -> a.getStatus ().compareTo(b.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No window can accept new orders"));
    }
}
