import java.util.Objects;
import java.util.UUID;

public class Bag {

    private final String id;
    private BagStatus status;

    //Optional: link to the previous bag if this one is a replacement.
    private final String replacedBagId;

    public Bag() {
        this.id = UUID.randomUUID().toString();
        this.status = BagStatus.OPEN;
        this.replacedBagId = null;
    }

    private Bag(String replacedBagId) {
        this.id = UUID.randomUUID().toString();
        this.status = BagStatus.OPEN;
        this.replacedBagId = replacedBagId;
    }

    public String getId() {
        return id;
    }

    public BagStatus getStatus() {
        return status;
    }

    public String getReplacedBagId() {
        return replacedBagId;
    }

    /**
     * Close the bag normally.
     */
    public void close() {
        if (status != BagStatus.OPEN) {
            throw new IllegalStateException("Only OPEN bags can be closed");
        }
        this.status = BagStatus.CLOSED;
    }

    /**
     * Discard this bag and request a new one as a compensating action.
     *
     * This is forward-only:
     * - The current bag becomes DISCARDED.
     * - A new bag is created in OPEN state.
     * - The new bag keeps a reference to the discarded one for traceability.
     */
     public Bag discardAndRequestNewBag() {
         if (status == BagStatus.CLOSED) {
             throw new IllegalStateException("Cannot discard a CLOSED bag");
         }

         if (status == BagStatus.DISCARDED) {
            throw new IllegalStateException("Bag is already discarded");
        }

        //Mark current bag as discarded
        this.status = BagStatus.DISCARDED;

         //Create a new bag as a replacement (compensation)
         Bag newBag = new Bag(this.id);
         newBag.status = BagStatus.REUSED;

         return newBag;
    }
}
