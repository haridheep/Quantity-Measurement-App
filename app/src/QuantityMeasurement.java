class QuantityMeasurement {

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
                throw new IllegalArgumentException("Value must be a finite number");
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

        // UC5: explicit conversion
        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }

            double baseValue = this.toBaseUnit();
            double converted = targetUnit.fromBase(baseValue);

            // optional precision handling
            return Math.round(converted * 100000.0) / 100000.0;
        }

        // Static conversion API
        public static double convert(double value, LengthUnit source, LengthUnit target) {
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

    public static void main(String[] args) {

        QuantityLength feet = new QuantityLength(1.0, LengthUnit.FEET);
        System.out.println("1 feet to inches: " +
                feet.convertTo(LengthUnit.INCH));

        System.out.println("1 yard to feet: " +
                QuantityLength.convert(1.0, LengthUnit.YARD, LengthUnit.FEET));

        System.out.println("2 cm to inches: " +
                QuantityLength.convert(2.54, LengthUnit.CM, LengthUnit.INCH));
    }
}