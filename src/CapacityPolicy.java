import java.util.List;

public interface CapacityPolicy {

    /**
     * @param bagType Type of bag / flow
     * @param products Products to be packed
     * @param outsideTempCelsius Outside temperature (for ice calculation)
     * @return true if the products fit according to the policy
     */
    boolean fits (BagType bagType, List<Product> products, double outsideTempCelsius);
}
