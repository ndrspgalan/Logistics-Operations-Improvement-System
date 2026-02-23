/**
 * Operational status of a picking window.
 *
 * OPEN:
 * - >= 3 hours to deadline.
 * - Still open to new orders, not planned yet.
 *
 * CONFIRMED:
 * - < 3 hours and >= 30 minutes to deadline.
 * - Closed to changes, can be planned and can be mixed (if products overlap)
 * - Covers both "other" (~2h) and "now" (~1h).
 *
 * URGENT:
 * - < 30 minutes to deadline.
 * - Can be picked, but MUST NOT be mixed with any other window.
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
