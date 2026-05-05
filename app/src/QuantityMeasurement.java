enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CM(0.393701 / 12.0);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    public double toBase(double value) {
        return value * toFeetFactor;
    }

    public double fromBase(double baseValue) {
        return baseValue / toFeetFactor;
    }
}

enum WeightUnit {
    KILOGRAM(1.0),
    GRAM(1.0 / 1000.0),
    POUND(0.453592);

    private final double toKgFactor;

    WeightUnit(double toKgFactor) {
        this.toKgFactor = toKgFactor;
    }

    public double toBase(double value) {
        return value * toKgFactor;
    }

    public double fromBase(double baseValue) {
        return baseValue / toKgFactor;
    }
}

public class QuantityMeasurement {

    // -------- LENGTH --------
    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private void validate(double value, LengthUnit unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid length value");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Length unit cannot be null");
            }
        }

        public double toBaseUnit() {
            return unit.toBase(value);
        }

        public boolean equals(QuantityLength other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        public QuantityLength convertTo(LengthUnit targetUnit) {
            double base = this.toBaseUnit();
            return new QuantityLength(round(targetUnit.fromBase(base)), targetUnit);
        }

        public QuantityLength add(QuantityLength other) {
            double baseSum = this.toBaseUnit() + other.toBaseUnit();
            return new QuantityLength(round(this.unit.fromBase(baseSum)), this.unit);
        }

        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            double baseSum = q1.toBaseUnit() + q2.toBaseUnit();
            return new QuantityLength(round(targetUnit.fromBase(baseSum)), targetUnit);
        }

        private static double round(double v) {
            return Math.round(v * 100000.0) / 100000.0;
        }

        public String toString() {
            return value + " " + unit;
        }
    }

    // -------- WEIGHT --------
    static class QuantityWeight {
        private final double value;
        private final WeightUnit unit;

        public QuantityWeight(double value, WeightUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private void validate(double value, WeightUnit unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid weight value");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Weight unit cannot be null");
            }
        }

        public double toBaseUnit() {
            return unit.toBase(value);
        }

        // Equality
        public boolean equals(QuantityWeight other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        // Conversion
        public QuantityWeight convertTo(WeightUnit targetUnit) {
            double base = this.toBaseUnit();
            return new QuantityWeight(round(targetUnit.fromBase(base)), targetUnit);
        }

        // Addition (default)
        public QuantityWeight add(QuantityWeight other) {
            double baseSum = this.toBaseUnit() + other.toBaseUnit();
            return new QuantityWeight(round(this.unit.fromBase(baseSum)), this.unit);
        }

        // Addition (explicit target)
        public static QuantityWeight add(QuantityWeight q1, QuantityWeight q2, WeightUnit targetUnit) {
            double baseSum = q1.toBaseUnit() + q2.toBaseUnit();
            return new QuantityWeight(round(targetUnit.fromBase(baseSum)), targetUnit);
        }

        private static double round(double v) {
            return Math.round(v * 100000.0) / 100000.0;
        }

        public String toString() {
            return value + " " + unit;
        }
    }

    // -------- MAIN --------
    public static void main(String[] args) {

        // Length examples
        QuantityLength l1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength l2 = new QuantityLength(12.0, LengthUnit.INCH);

        System.out.println("Length Equal: " + l1.equals(l2));
        System.out.println("Length Add (yards): " +
                QuantityLength.add(l1, l2, LengthUnit.YARD));

        // Weight examples
        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));

        System.out.println("1 kg to pounds: " +
                w1.convertTo(WeightUnit.POUND));

        QuantityWeight w3 = new QuantityWeight(2.0, WeightUnit.POUND);

        System.out.println("Add weights (kg): " +
                QuantityWeight.add(w1, w3, WeightUnit.KILOGRAM));
    }
}