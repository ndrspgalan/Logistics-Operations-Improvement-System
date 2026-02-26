/**
 * Operational status of a picking bag.
 *
 * OPEN:
 * Bag is currently open and can receive items.
 *
 * CLOSED:
 * Bag was successfully closed and is ready for downstream processing.
 *
 * DISCARDED:
 * Bag became physically unusable (broken, wet, wrong size, etc.) and was discarded.
 * A replacement bag may have been created as a compensating action.
 *
 * REUSED:
 * A new bag was created as a replacement for a discarded one.
 * This is a forward-only, compensating state.
 * Keeps a logical link to the original one for traceability.
 *
 * EMPTY:
 * Bag has been fully processed by the UNPACK flow and contains no items.
 * This is a terminal state for returned bags:
 * - The original contents have been redistributed (REINTRODUCE / DONATE / DESTROY).
 * - The bag is no longer part of the operational picking flow.
 * - The state exists for traceability, auditing, and reporting purposes.
 *
 * IMPORTANT: EMPTY is not the same as DISCARDED.
 * - DISCARDED means the bag itself its unusable.
 * - EMPTY means the bag was returned, unpacked and its lifecycle is cleanly closed.
 */

public enum BagStatus {
    OPEN,
    CLOSED,
    DISCARDED,
    REUSED,
    EMPTY
}
