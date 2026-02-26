import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ReturnedBagManifest {

    private final String slamId;
    private final BagType bagType;
    private final List<ReturnedItemLine> lines;

    public ReturnedBagManifest(String slamId, BagType bagType, List<ReturnedItemLine> lines) {
        this.slamId = Objects.requireNonNull(slamId, "slamId must not be null");
        this.bagType = Objects.requireNonNull(bagType, "bagType must not be null");
        this.lines = Objects.requireNonNull(lines, "lines must not be null");
    }

    public String getSlamId() {
        return slamId;
    }

    public BagType getBagType() {
        return bagType;
    }

    public List<ReturnedItemLine> getLines() {
        return lines;
    }
}
