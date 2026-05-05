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

    LengthUnit(double factor) {
        this.factor = factor;
    }

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

    WeightUnit(double factor) {
        this.factor = factor;
    }

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

    VolumeUnit(double factor) {
        this.factor = factor;
    }

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
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    // -------- ADDITION --------
    public Quantity<U> add(Quantity<U> other) {
        validateSameCategory(other);
        double baseSum = this.toBaseUnit() + other.toBaseUnit();
        return new Quantity<>(round(unit.convertFromBaseUnit(baseSum)), unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateSameCategory(other);
        double baseSum = this.toBaseUnit() + other.toBaseUnit();
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(baseSum)), targetUnit);
    }

    // -------- SUBTRACTION (UC12) --------
    public Quantity<U> subtract(Quantity<U> other) {
        validateSameCategory(other);
        double baseDiff = this.toBaseUnit() - other.toBaseUnit();
        return new Quantity<>(round(unit.convertFromBaseUnit(baseDiff)), unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateSameCategory(other);
        double baseDiff = this.toBaseUnit() - other.toBaseUnit();
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(baseDiff)), targetUnit);
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

// -------- APPLICATION --------
public class QuantityMeasurement {

    public static void main(String[] args) {

        // -------- LENGTH --------
        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println("Subtract Length: " + l1.subtract(l2)); // ~9.5 FEET
        System.out.println("Divide Length: " + l1.divide(l2));

        // -------- WEIGHT --------
        Quantity<WeightUnit> w1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        System.out.println("Subtract Weight: " + w1.subtract(w2));
        System.out.println("Divide Weight: " + w1.divide(w2)); // 2.0

        // -------- VOLUME --------
        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2.0, VolumeUnit.LITRE);

        System.out.println("Subtract Volume: " + v1.subtract(v2));
        System.out.println("Divide Volume: " + v1.divide(v2)); // 2.5
    }
}