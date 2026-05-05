public class QuantityMeasurement {

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

        // ✅ Conversion
        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }
            return round(targetUnit.fromBase(this.toBaseUnit()));
        }

        public static double convert(double value, LengthUnit source, LengthUnit target) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units must not be null");
            }
            return round(target.fromBase(source.toBase(value)));
        }

        // ✅ UC6: Default add (result in first unit)
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity must not be null");
            }

            double baseSum = this.toBaseUnit() + other.toBaseUnit();
            return new QuantityLength(round(this.unit.fromBase(baseSum)), this.unit);
        }

        // ✅ UC7: Add with target unit
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Quantities must not be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }

            double baseSum = q1.toBaseUnit() + q2.toBaseUnit();
            return new QuantityLength(round(targetUnit.fromBase(baseSum)), targetUnit);
        }

        private static double round(double value) {
            return Math.round(value * 100000.0) / 100000.0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ✅ From dev branch
    public static boolean compare(QuantityLength q1, QuantityLength q2) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities must not be null");
        }
        return q1.equals(q2);
    }

    public static void main(String[] args) {

        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        // UC6
        System.out.println("Default add: " + q1.add(q2)); // 2 FEET

        // UC7
        QuantityLength result = QuantityLength.add(q1, q2, LengthUnit.YARD);
        System.out.println("1 ft + 12 in in yards = " + result);

        // Comparison
        System.out.println("Are equal: " + compare(q1, q2));

        // Conversion
        System.out.println("1 ft to inches: " + q1.convertTo(LengthUnit.INCH));

        QuantityLength q3 = new QuantityLength(2.54, LengthUnit.CM);
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.INCH);

        System.out.println("2.54 cm + 1 in in inches = " +
                QuantityLength.add(q3, q4, LengthUnit.INCH));
    }
}