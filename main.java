import java.util.Scanner;

public class Main {

    // Static inner class
    static class Calculator {
        private int base;

        // Constructor requires something (a base number)
        public Calculator(int base) {
            this.base = base;
        }

        // Method does something (adds base to input and returns result)
        public int addToBase(int value) {
            return base + value;
        }

        // Another example method (multiplies base with input)
        public int multiplyWithBase(int value) {
            return base * value;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for base number
        System.out.print("Enter a base number: ");
        int base = scanner.nextInt();

        // Create instance of static inner class
        Calculator calc = new Calculator(base);

        // Ask for another number
        System.out.print("Enter another number: ");
        int num = scanner.nextInt();

        // Use inner class methods
        int sum = calc.addToBase(num);
        int product = calc.multiplyWithBase(num);

        System.out.println("Result of base + number = " + sum);
        System.out.println("Result of base * number = " + product);

        scanner.close();
    }
}
