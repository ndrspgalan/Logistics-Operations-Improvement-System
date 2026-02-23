import java.util.Objects;

public class Product {

    private final String sku;
    private final double weightKg;
    private final int volumeCm3;
    private final boolean fragile;

    public Product(String sku, double weightKg, int volumeCm3, boolean fragile) {
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
        this.weightKg = weightKg;
        this.volumeCm3 = volumeCm3;
        this.fragile = fragile;
    }

    public String getSku() {
        return sku;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public int getVolumeCm3() {
        return volumeCm3;
    }

    public boolean isFragile() {
        return fragile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return sku.equals(product.sku);
    }

    @Override
    public int hashCode() {
        return sku.hashCode();
    }
}
