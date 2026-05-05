class QuantityMeasurement {

    // Static method for Feet comparison
    public static boolean compareFeet(Double value1, Double value2) {
        Feet feet = new Feet();
        return feet.areEqual(value1, value2);
    }

    // Static method for Inches comparison
    public static boolean compareInches(Double value1, Double value2) {
        Inches inches = new Inches();
        return inches.areEqual(value1, value2);
    }

    // Inner class for Feet
    static class Feet {

        public boolean areEqual(Double value1, Double value2) {
            validate(value1, value2);
            return Double.compare(value1, value2) == 0;
        }

        private void validate(Double value1, Double value2) {
            if (value1 == null || value2 == null) {
                throw new IllegalArgumentException("Feet values must not be null");
            }
        }
    }

    // Inner class for Inches
    static class Inches {

        public boolean areEqual(Double value1, Double value2) {
            validate(value1, value2);
            return Double.compare(value1, value2) == 0;
        }

        private void validate(Double value1, Double value2) {
            if (value1 == null || value2 == null) {
                throw new IllegalArgumentException("Inch values must not be null");
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        // Hard-coded values (as per requirement)
        Double feetValue1 = 5.0;
        Double feetValue2 = 5.0;

        Double inchValue1 = 12.0;
        Double inchValue2 = 12.0;

        // Feet comparison
        boolean feetResult = compareFeet(feetValue1, feetValue2);
        System.out.println("Feet equal: " + feetResult);

        // Inches comparison
        boolean inchResult = compareInches(inchValue1, inchValue2);
        System.out.println("Inches equal: " + inchResult);
    }
}