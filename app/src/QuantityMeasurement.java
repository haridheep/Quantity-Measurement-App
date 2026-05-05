public class QuantityMeasurement {

    enum Unit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CM(0.393701 / 12.0); // cm → inches → feet

        private final double toFeetFactor;

        Unit(double toFeetFactor) {
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
        private final Unit unit;

        public QuantityLength(double value, Unit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private void validate(double value, Unit unit) {
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

        // ✅ Convert this quantity to another unit
        public double convertTo(Unit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }

            double baseValue = this.toBaseUnit();
            double converted = targetUnit.fromBase(baseValue);

            return Math.round(converted * 100000.0) / 100000.0;
        }

        // ✅ Static conversion method
        public static double convert(double value, Unit source, Unit target) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units must not be null");
            }

            double base = source.toBase(value);
            double result = target.fromBase(base);

            return Math.round(result * 100000.0) / 100000.0;
        }
    }

    // ✅ Comparison API (from dev branch)
    public static boolean compare(QuantityLength q1, QuantityLength q2) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities must not be null");
        }
        return q1.equals(q2);
    }

    public static void main(String[] args) {

        // 🔹 Equality checks
        QuantityLength q1 = new QuantityLength(1.0, Unit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, Unit.INCH);

        QuantityLength q3 = new QuantityLength(1.0, Unit.YARD);
        QuantityLength q4 = new QuantityLength(3.0, Unit.FEET);

        QuantityLength q5 = new QuantityLength(2.54, Unit.CM);
        QuantityLength q6 = new QuantityLength(1.0, Unit.INCH);

        System.out.println(compare(q1, q2)); // true
        System.out.println(compare(q3, q4)); // true
        System.out.println(compare(q5, q6)); // true

        // 🔹 Conversion tests
        System.out.println("1 feet to inches: " +
                q1.convertTo(Unit.INCH));

        System.out.println("1 yard to feet: " +
                QuantityLength.convert(1.0, Unit.YARD, Unit.FEET));

        System.out.println("2.54 cm to inches: " +
                QuantityLength.convert(2.54, Unit.CM, Unit.INCH));
    }
}