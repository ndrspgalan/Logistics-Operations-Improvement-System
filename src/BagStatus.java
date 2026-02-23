/**
 * Operational status of a picking bag.
 *
 * OPEN:
 * - Bag is currently being filled.
 *
 * CLOSED:
 * - Bag was successfully closed and is ready for downstream processing.
 *
 * DISCARDED:
 * - Bag became unusable (broken, wet, wrong size, etc.).
 * - It must not be used anymore.
 *
 * REUSED:
 * - A new bag was created as a replacement for a discarded one.
 * - This is a forward-only, compensating state.
 */

public enum BagStatus {
    OPEN,
    CLOSED,
    DISCARDED,
    REUSED
}
