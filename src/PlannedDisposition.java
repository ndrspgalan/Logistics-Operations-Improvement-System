import java.util.Objects;

public class PlannedDisposition {

    private final UnpackSelection selection;
    private final Disposition disposition;

    public PlannedDisposition(UnpackSelection selection, Disposition disposition) {
        this.selection = Objects.requireNonNull(selection, "selection must not be null");
        this.disposition = Objects.requireNonNull(disposition, "disposition must not be null");
    }

    public UnpackSelection getSelection() {
        return selection;
    }

    public Disposition getDisposition() {
        return disposition;
    }
}
