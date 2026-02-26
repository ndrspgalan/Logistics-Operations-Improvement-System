import java.util.Objects;

public class UnpackSelection {

    private final ReturnedItemLine line;
    private final int quantityToUnpack;
    private ConfirmationStatus confirmationStatus = ConfirmationStatus.PENDING;

    public UnpackSelection(ReturnedItemLine line, int quantityToUnpack) {
        Objects.requireNonNull(line, "line must not be null");
        if (quantityToUnpack <= 0 || quantityToUnpack > line.getQuantity ()) {
            throw new IllegalArgumentException("quantityToUnpack must be a within line quantity");
        }
        this.line = line;
        this.quantityToUnpack = quantityToUnpack;
    }

    public ReturnedItemLine getLine() {
        return line;
    }

    public int getQuantityToUnpack() {
        return quantityToUnpack;
    }

    public ConfirmationStatus getConfirmationStatus() {
        return confirmationStatus;
    }

    public void confirm() {
        this.confirmationStatus = ConfirmationStatus.CONFIRMED;
    }
}
