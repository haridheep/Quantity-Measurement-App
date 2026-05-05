// -------- COMMON INTERFACE --------
interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

// -------- LENGTH UNITS --------
enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CM(0.393701 / 12.0);

    private final double factor;

    LengthUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

// -------- WEIGHT UNITS --------
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(1.0 / 1000.0),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

// -------- VOLUME UNITS --------
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

// -------- GENERIC QUANTITY CLASS --------
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        if (unit == null) throw new IllegalArgumentException("Unit must not be null");
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
        if (targetUnit == null) throw new IllegalArgumentException("Target unit must not be null");
        double base = this.toBaseUnit();
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- CENTRALIZED OPERATION ENUM --------
    private enum Operation {
        ADD, SUBTRACT, DIVIDE
    }

    // -------- CENTRALIZED HELPER METHOD --------
    private double operate(Quantity<U> other, Operation op) {
        if (other == null) throw new IllegalArgumentException("Other must not be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Different measurement categories");

        double base1 = this.toBaseUnit();
        double base2 = other.toBaseUnit();

        return switch (op) {
            case ADD -> base1 + base2;
            case SUBTRACT -> base1 - base2;
            case DIVIDE -> {
                if (base2 == 0.0) throw new ArithmeticException("Division by zero");
                yield base1 / base2;
            }
        };
    }

    // -------- ADDITION --------
    public Quantity<U> add(Quantity<U> other) {
        double base = operate(other, Operation.ADD);
        return new Quantity<>(round(unit.convertFromBaseUnit(base)), unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (targetUnit == null) throw new IllegalArgumentException("Target unit must not be null");
        double base = operate(other, Operation.ADD);
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- SUBTRACTION --------
    public Quantity<U> subtract(Quantity<U> other) {
        double base = operate(other, Operation.SUBTRACT);
        return new Quantity<>(round(unit.convertFromBaseUnit(base)), unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        if (targetUnit == null) throw new IllegalArgumentException("Target unit must not be null");
        double base = operate(other, Operation.SUBTRACT);
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- DIVISION --------
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

// -------- APPLICATION --------
public class QuantityMeasurement {

    public static void main(String[] args) {

        // LENGTH
        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println("Add: " + l1.add(l2));
        System.out.println("Subtract: " + l1.subtract(l2));
        System.out.println("Divide: " + l1.divide(l2));

        // WEIGHT
        Quantity<WeightUnit> w1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        System.out.println("Weight Add: " + w1.add(w2));
        System.out.println("Weight Divide: " + w1.divide(w2));

        // VOLUME
        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2.0, VolumeUnit.LITRE);

        System.out.println("Volume Subtract: " + v1.subtract(v2));
    }
}