public class QuantityMeasurement {

    // Method to check equality of two values in feet
    public boolean areEqual(Double value1, Double value2) {

        // Validate inputs
        if (value1 == null || value2 == null) {
            throw new IllegalArgumentException("Input values must not be null");
        }

        // Compare values
        return Double.compare(value1, value2) == 0;
    }

    // Optional: main method for testing
    public static void main(String[] args) {
        QuantityMeasurement quantityMeasurement = new QuantityMeasurement();

        Double value1 = 5.0;
        Double value2 = 5.0;

        boolean result = quantityMeasurement.areEqual(value1, value2);

        System.out.println("Are values equal? " + result);
    }
}