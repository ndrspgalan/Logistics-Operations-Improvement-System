import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class ReturnEligibilityPolicy {

    private static final long MIN_DAYS_TO_REINTRODUCE = 7;

    /**
     * Only applies to LIGHT_AMBIENT / HARD_AMBIENT flows.
     * CHILLED / FROZEN are handled earlier and never reach this policy.
     */
    public boolean canReintroduce(ReturnedItemLine line, LocalDate today) {
        Objects.requireNonNull(line, "line must not be null");
        Objects.requireNonNull(today, "today must not be null");

        long daysLeft = ChronoUnit.DAYS.between(today, line.getExpirationDate());
        return  daysLeft >= MIN_DAYS_TO_REINTRODUCE;
    }
}
