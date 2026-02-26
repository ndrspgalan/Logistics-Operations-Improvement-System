import java.time.LocalDate;
import java.util.Objects;

public class ReturnedItemLine {

    private final Product product;
    private final int quantity;
    private final LocalDate expirationDate;

    public ReturnedItemLine(Product product, int quantity, LocalDate expirationDate) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > zero");
        this.product = Objects.requireNonNull(product,  "Product must not be null");
        this.quantity = quantity;
        this.expirationDate = Objects.requireNonNull(expirationDate,  "Expiration Date must not be null");
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
