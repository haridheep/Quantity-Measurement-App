// -------- GENERIC QUANTITY CLASS --------
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public double toBaseUnit() {
        return unit.convertToBaseUnit(value);
    }

    // -------- EQUALITY --------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?> other)) return false;

        if (this.unit.getClass() != other.unit.getClass()) return false;

        return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toBaseUnit());
    }

    // -------- CONVERSION --------
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        double base = this.toBaseUnit();
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(converted), targetUnit);
    }

    // -------- ADDITION --------
    public Quantity<U> add(Quantity<U> other) {
        validateSameCategory(other);
        double baseSum = this.toBaseUnit() + other.toBaseUnit();
        double result = unit.convertFromBaseUnit(baseSum);
        return new Quantity<>(round(result), unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateSameCategory(other);
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        double baseSum = this.toBaseUnit() + other.toBaseUnit();
        double result = targetUnit.convertFromBaseUnit(baseSum);
        return new Quantity<>(round(result), targetUnit);
    }

    // -------- SUBTRACTION (UC12) --------
    public Quantity<U> subtract(Quantity<U> other) {
        validateSameCategory(other);
        double baseDiff = this.toBaseUnit() - other.toBaseUnit();
        double result = unit.convertFromBaseUnit(baseDiff);
        return new Quantity<>(round(result), unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateSameCategory(other);
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        double baseDiff = this.toBaseUnit() - other.toBaseUnit();
        double result = targetUnit.convertFromBaseUnit(baseDiff);
        return new Quantity<>(round(result), targetUnit);
    }

    // -------- DIVISION (UC12) --------
    public double divide(Quantity<U> other) {
        validateSameCategory(other);
        double divisor = other.toBaseUnit();

        if (divisor == 0.0) {
            throw new ArithmeticException("Division by zero");
        }

        return round(this.toBaseUnit() / divisor);
    }

    // -------- VALIDATION --------
    private void validateSameCategory(Quantity<U> other) {
        if (other == null) {
            throw new IllegalArgumentException("Other must not be null");
        }
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException("Different measurement categories");
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}