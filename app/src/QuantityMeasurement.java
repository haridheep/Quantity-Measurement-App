enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CM(0.393701 / 12.0);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    // Convert given value to base unit (feet)
    public double toBase(double value) {
        return value * toFeetFactor;
    }

    // Convert from base unit (feet) to this unit
    public double fromBase(double baseValue) {
        return baseValue / toFeetFactor;
    }
}

public class QuantityMeasurement {

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
                throw new IllegalArgumentException("Value must be finite");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit must not be null");
            }
        }

        public double toBaseUnit() {
            return unit.toBase(value);
        }

        public boolean equals(QuantityLength other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }

        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }
            double base = this.toBaseUnit();
            return round(targetUnit.fromBase(base));
        }

        public static double convert(double value, LengthUnit source, LengthUnit target) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units must not be null");
            }
            double base = source.toBase(value);
            return round(target.fromBase(base));
        }

        // UC6: add (default to first operand unit)
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other must not be null");
            }
            double baseSum = this.toBaseUnit() + other.toBaseUnit();
            double result = this.unit.fromBase(baseSum);
            return new QuantityLength(round(result), this.unit);
        }

        // UC7: add with explicit target unit
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Quantities must not be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }
            double baseSum = q1.toBaseUnit() + q2.toBaseUnit();
            double result = targetUnit.fromBase(baseSum);
            return new QuantityLength(round(result), targetUnit);
        }

        private static double round(double value) {
            return Math.round(value * 100000.0) / 100000.0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static void main(String[] args) {

        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        // Equality
        System.out.println("Equal: " + q1.equals(q2));

        // Conversion
        System.out.println("1 ft to inches: " + q1.convertTo(LengthUnit.INCH));

        // Addition (default)
        System.out.println("Add (default): " + q1.add(q2));

        // Addition with target unit
        System.out.println("Add in yards: " +
                QuantityLength.add(q1, q2, LengthUnit.YARD));
    }
}