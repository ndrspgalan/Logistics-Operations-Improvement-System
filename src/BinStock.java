import java.time.LocalDate;
import java.util.Objects;

public class BinStock {

    private final String binId;
    private final String sku;
    private final LocalDate expirationDate;

    public BinStock(String binId, String sku, LocalDate expirationDate) {
        this.binId = Objects.requireNonNull(binId, "binId must not be null");
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
        this.expirationDate = Objects.requireNonNull(expirationDate, "expirationDate must not be null");
    }

    public String getBinId() {
        return binId;
    }

    public String getSku() {
        return sku;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
