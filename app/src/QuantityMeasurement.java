// -------- GENERIC QUANTITY --------
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

    // -------- CENTRALIZED OPERATIONS --------
    private enum Operation {
        ADD, SUBTRACT, DIVIDE
    }

    private double operate(Quantity<U> other, Operation op) {
        if (other == null) {
            throw new IllegalArgumentException("Other must not be null");
        }
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException("Different measurement categories");
        }

        double base1 = this.toBaseUnit();
        double base2 = other.toBaseUnit();

        switch (op) {
            case ADD:
                return base1 + base2;
            case SUBTRACT:
                return base1 - base2;
            case DIVIDE:
                if (base2 == 0.0) {
                    throw new ArithmeticException("Division by zero");
                }
                return base1 / base2;
            default:
                throw new IllegalStateException("Unexpected operation");
        }
    }

    // -------- ADD --------
    public Quantity<U> add(Quantity<U> other) {
        double base = operate(other, Operation.ADD);
        return new Quantity<>(round(unit.convertFromBaseUnit(base)), unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        double base = operate(other, Operation.ADD);
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- SUBTRACT --------
    public Quantity<U> subtract(Quantity<U> other) {
        double base = operate(other, Operation.SUBTRACT);
        return new Quantity<>(round(unit.convertFromBaseUnit(base)), unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        double base = operate(other, Operation.SUBTRACT);
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- DIVIDE --------
    public double divide(Quantity<U> other) {
        return round(operate(other, Operation.DIVIDE));
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}