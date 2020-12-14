package main.java.prototypes;

import main.java.model.helpers.DoubleCompare;

/**
 * for testing and learning only.
 */
public class DoubleEqualTest {

    public void runTest(){

        System.out.println("Attempting double equality check. Adding 0.01");

        double number1 = 0.09d;
        double number2 = 0.08d;

        number2 = number2 + 0.01d;

        if(Double.compare(number1, number2) == 0) System.out.println("number 1 equals number 2");
        else System.out.println("Double comparison - they are different");

        if(number1 == number2) System.out.println("Double equality test passed!");
        else System.out.println("Double comparison - they are different");

        System.out.println("Doing it with (int)(double*precision) : ");
        int precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) System.out.println("number1 equals number2");
        else System.out.println("Int comparison - they are different");

        System.out.println("Attempting double equality check. Substracting 0.01");

        number1 = 0.09d;
        number2 = 0.10d;

        number2 = number2 - 0.01d;

        if(Double.compare(number1, number2) == 0) System.out.println("number 1 equals number 2");
        else System.out.println("Double comparison - they are different");

        if(number1 == number2) System.out.println("Double equality test passed!");
        else System.out.println("Double comparison - they are different");

        System.out.println("Doing it with (int)(double*precision) : ");
        precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) System.out.println("number1 equals number2");
        else System.out.println("Int comparison - they are different");


        System.out.println("Attempting double equality check. Substracting 0.0100005");

        number1 = 0.09d;
        number2 = 0.10d;

        number2 = number2 - 0.0100005d;

        if(Double.compare(number1, number2) == 0) System.out.println("number 1 equals number 2");
        else System.out.println("Double comparison - they are different");

        if(number1 == number2) System.out.println("Double equality test passed!");
        else System.out.println("Double comparison - they are different");

        System.out.println("Doing it with (int)(double*precision) : ");
        precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) System.out.println("number1 equals number2");
        else System.out.println("Int comparison - they are different");

        System.out.println("\nUsing DoubleCompare: ");
        if(DoubleCompare.equals(number1, number2)) System.out.println("They are equal");
        else System.out.println("The are different");


    }
}
