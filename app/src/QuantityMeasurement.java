class QuantityMeasurement {

    enum Unit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CM(0.393701 / 12.0); // convert cm -> inches -> feet

        private final double toFeetFactor;

        Unit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }
    }

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

        public double toBaseUnit() {
            return unit.toFeet(value);
        }

        public boolean equals(QuantityLength other) {
            if (other == null) return false;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
        }
    }

    public static boolean compare(QuantityLength q1, QuantityLength q2) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities must not be null");
        }
        return q1.equals(q2);
    }

    public static void main(String[] args) {

        QuantityLength q1 = new QuantityLength(1.0, Unit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, Unit.INCH);

        QuantityLength q3 = new QuantityLength(1.0, Unit.YARD);
        QuantityLength q4 = new QuantityLength(3.0, Unit.FEET);

        QuantityLength q5 = new QuantityLength(2.54, Unit.CM);
        QuantityLength q6 = new QuantityLength(1.0, Unit.INCH);

        System.out.println(compare(q1, q2)); // true
        System.out.println(compare(q3, q4)); // true
        System.out.println(compare(q5, q6)); // true
    }
}