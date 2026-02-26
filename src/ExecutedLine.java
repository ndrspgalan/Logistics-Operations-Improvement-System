import java.util.Objects;

public class ExecutedLine {
    private final ReturnedItemLine line;
    private final int quantity;
    private final Disposition disposition;

    public ExecutedLine(ReturnedItemLine line, int quantity, Disposition disposition) {
        this.line = Objects.requireNonNull(line, "line must not be null");
        this.disposition = Objects.requireNonNull(disposition, "disposition must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
    }

    public ReturnedItemLine getLine() {
        return line;
    }

    public int getQuantity() {
        return quantity;
    }

    public Disposition getDisposition() {
        return disposition;
    }
}
