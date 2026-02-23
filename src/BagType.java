public enum BagType {
    LIGHT_AMBIENT,  // Cardboard bag, volume limit applies
    HARD_AMBIENT,   // No bag: one product per unit, SLAM attached directly
    CHILLED,        // Special inner ice bag + flexible outer bag
    FROZEN,         // Same as chilled, but with more ice
}
