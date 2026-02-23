import java.util.Objects;

public class Product {

    private final String sku;

    public Product(String sku) {
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
    }

    public String getSku() {
        return sku;
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
