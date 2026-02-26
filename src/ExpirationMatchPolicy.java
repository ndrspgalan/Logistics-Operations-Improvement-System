import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpirationMatchPolicy {

    /**
     * Returns only bins with SAME SKU and SAME EXPIRATION DATE.
     * If none match, returns empty list (caller shows "No products were found with equal expiration date").
     */
    public List<BinStock> findEligibleBins(String sku, LocalDate expirationDate, List<BinStock> allBins) {
        Objects.requireNonNull(sku, "sku must not be null");
        Objects.requireNonNull(expirationDate, "expirationDate must not be null");
        Objects.requireNonNull(allBins, "allBins must not be null");

        return allBins.stream()
                .filter(b -> b.getSku().equals(sku))
                .filter(b -> b.getExpirationDate().equals(expirationDate))
                .collect(Collectors.toList());
    }
}
