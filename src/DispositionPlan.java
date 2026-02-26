import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DispositionPlan {

    private final List<PlannedDisposition> entries = new ArrayList<>();
    private ConfirmationStatus confirmationStatus = ConfirmationStatus.PENDING;

    public void add(PlannedDisposition entry) {
        entries.add(Objects.requireNonNull(entry, "entry must not be null"));
    }

    public List<PlannedDisposition> getEntries() {
        return List.copyOf(entries);
    }

    public ConfirmationStatus getConfirmationStatus() {
    return confirmationStatus;
    }

    public void confirm() {
        this.confirmationStatus = ConfirmationStatus.CONFIRMED;
    }
}