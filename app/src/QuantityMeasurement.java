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

        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }
            double baseValue = this.toBaseUnit();
            double converted = targetUnit.fromBase(baseValue);
            return round(converted);
        }

        public static double convert(double value, LengthUnit source, LengthUnit target) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units must not be null");
            }
            double base = source.toBase(value);
            double result = target.fromBase(base);
            return round(result);
        }

        // UC6 (existing)
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity must not be null");
            }
            double baseSum = this.toBaseUnit() + other.toBaseUnit();
            double resultValue = this.unit.fromBase(baseSum);
            return new QuantityLength(round(resultValue), this.unit);
        }

        // UC7: Addition with explicit target unit
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Quantities must not be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit must not be null");
            }

            double baseSum = q1.toBaseUnit() + q2.toBaseUnit();
            double resultValue = targetUnit.fromBase(baseSum);

            return new QuantityLength(round(resultValue), targetUnit);
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

        // Default (UC6)
        System.out.println("Default add: " + q1.add(q2)); // 2 FEET

        // UC7: Explicit target unit (YARD)
        QuantityLength result = QuantityLength.add(q1, q2, LengthUnit.YARD);
        System.out.println("1 ft + 12 in in yards = " + result); // ~0.66667 YARD

        // Another example
        QuantityLength q3 = new QuantityLength(2.54, LengthUnit.CM);
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.INCH);

        QuantityLength result2 = QuantityLength.add(q3, q4, LengthUnit.INCH);
        System.out.println("2.54 cm + 1 in in inches = " + result2); // 2 INCH
    }
}