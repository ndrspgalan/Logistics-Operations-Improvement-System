import java.util.List;
import java.util.Objects;

public class UnpackResult {

    private final ReturnedBagManifest manifest;
    private final boolean autoDestroyed;

    private UnpackResult(ReturnedBagManifest manifest, boolean autoDestroyed) {
        this.manifest = Objects.requireNonNull(manifest, "manifest must not be null");
        this.autoDestroyed = autoDestroyed;
    }

    public static UnpackResult destroyAll(ReturnedBagManifest manifest) {
        return new UnpackResult(manifest, true);
    }

    public static UnpackResult awaitingSelection(ReturnedBagManifest manifest) {
        return new UnpackResult(manifest, false);
    }

    public boolean isAutoDestroyed() {
        return autoDestroyed;
    }

    public ReturnedBagManifest getManifest() {
        return manifest;
    }
}
