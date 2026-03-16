import java.util.List;
import java.util.Objects;

public class EffectiveCapacityPolicy implements CapacityPolicy {

    //Weight limits
    private static final double MAX_WEIGHT_LIGHT_AMBIENT_KG = 12.0; // cardboard bag safety
    private static final double MAX_WEIGHT_COLD_KG = 15;            // chilled/frozen safety

    //Volume limits
    private static final int MAX_VOLUME_CM3 = 15_000;
    private static final int LIGHT_AMBIENT_OPERATIVE_VOLUME_CM3 = 12_000;

    //Fragile penalty
    private static final double FRAGILE_VOLUME_MULTIPLIER = 1.20; // +20%

    //Ice assumptions
    private static final double ICE_BLOCK_WEIGHT_KG = 0.4; // 400G
    private static final int ICE_BLOCK_VOLUME_CM3 = 400;   // rough approximation

    //Temperature range
    private static final double MIN_TEMP_C = 10.0;
    private static final double MAX_TEMP_C = 40.0;

    @Override
    public boolean fits(BagType bagType, List<Product> products, double outsideTempCelsius) {
        Objects.requireNonNull(bagType, "bagType must not be null");
        Objects.requireNonNull(products, "products must not be null");

        //HARD_AMBIENT: no bag, one product per unit (SLAM directly attached)
        if (bagType == BagType.HARD_AMBIENT) {
            return products.size() == 1;
        }

        double totalWeight = 0.0;
        int totalVolume = 0;

        for (Product p : products) {
            totalWeight += p.getWeightKg();

            int productVolume = p.getVolumeCm3();

            //Fragile products pack worse: apply artificial volume penalty
            if (p.isFragile()) {
                productVolume = (int) Math.ceil(productVolume * FRAGILE_VOLUME_MULTIPLIER);
            }

            totalVolume += productVolume;
        }

        //Add ice for cold bags
        if (bagType == BagType.CHILLED || bagType == BagType.FROZEN) {
            int iceBlocks = calculateIceBlocks(bagType, outsideTempCelsius);

            totalWeight += iceBlocks * ICE_BLOCK_WEIGHT_KG;
            totalVolume += iceBlocks * ICE_BLOCK_VOLUME_CM3;
        }

        //Determine limits by bag type
        if (bagType == BagType.LIGHT_AMBIENT) {
            //Cardboard bag: strict volume and lower weight limit
            if (totalWeight > MAX_WEIGHT_LIGHT_AMBIENT_KG) {
                return false;
            }
            if (totalVolume > LIGHT_AMBIENT_OPERATIVE_VOLUME_CM3) {
                return false;
            }
            return true;
        }

        if (bagType == BagType.CHILLED || bagType == BagType.FROZEN) {
            //Special cold bags: no volume constraint due to flexible inner bag,
            //but higher weight limit applies
            if (totalWeight > MAX_WEIGHT_COLD_KG) {
                return false;
            }
            //No volume check here by design
            return true;
        }

        //Fallback (should not happen)
        if (totalWeight > MAX_WEIGHT_LIGHT_AMBIENT_KG) {
            return false;
        }
        if (totalVolume > MAX_VOLUME_CM3) {
            return false;
        }
        return true;
    }

    private int calculateIceBlocks(BagType bagType, double outsideTempCelsius) {
        double clampedTemp = clamp(outsideTempCelsius, MIN_TEMP_C, MAX_TEMP_C);
        double ratio = (clampedTemp - MIN_TEMP_C) / (MAX_TEMP_C - MIN_TEMP_C); //0.0 -> 1.0

        int minBlocks;
        int maxBlocks;

        if (bagType == BagType.CHILLED) {
            minBlocks = 3;
            maxBlocks = 7;
        } else if (bagType == BagType.FROZEN) {
            minBlocks = 7;
            maxBlocks = 12;
        } else {
            return 0;
        }

        double interpolared = minBlocks + ratio * (maxBlocks - minBlocks);
        return (int) Math.ceil(interpolared);
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
