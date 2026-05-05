class QuantityMeasurement {

    // Enum for supported units
    enum Unit {
        FEET(1.0),
        INCH(1.0 / 12.0); // 1 inch = 1/12 feet

        private final double toFeetFactor;

        Unit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }
    }

    // Quantity Length class
    static class QuantityLength {
        private final Double value;
        private final Unit unit;

        public QuantityLength(Double value, Unit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private void validate(Double value, Unit unit) {
            if (value == null) {
                throw new IllegalArgumentException("Value must not be null");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit must not be null");
            }
        }

        // Convert to base unit (feet)
        public double toBaseUnit() {
            return unit.toFeet(value);
        }

        // Equality check
        public boolean equals(QuantityLength other) {
            if (other == null) return false;

            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }
    }

    // Static method for comparison
    public static boolean compare(QuantityLength q1, QuantityLength q2) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities must not be null");
        }
        return q1.equals(q2);
    }

    // Main method
    public static void main(String[] args) {

        // Example inputs
        QuantityLength length1 = new QuantityLength(1.0, Unit.FEET);
        QuantityLength length2 = new QuantityLength(12.0, Unit.INCH);

        boolean result = compare(length1, length2);

        System.out.println("Are lengths equal? " + result);
    }
}