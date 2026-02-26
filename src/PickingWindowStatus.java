/**
 * Operational status of a picking window.
 *
 * OPEN:
 * - >= 3 hours to deadline.
 * - Still open to new orders.
 * - Not yet committed to picking flow.
 *
 * CONFIRMED:
 * - < 3 hours and >= 30 minutes to deadline.
 * - Already committed to the picking flow.
 * - Still MAY accept new orders IF capacity allows.
 * - Can be mixed with other CONFIRMED windows (if products overlap and not URGENT).
 *
 * URGENT:
 * - < 30 minutes to deadline.
 * - Can be picked, but MUST NOT be mixed with any other window.
 * - Still subject to capacity rules (no over-commit).
 *
 * MISSING:
 * - Operational failure state (not a time state).
 * - The window missed the batch / deadline passed without being picked.
 * - Or impacted by confirmed order cancellation (return / manual workaround).
 */
public enum PickingWindowStatus {
    OPEN,
    CONFIRMED,
    URGENT,
    MISSING
}
