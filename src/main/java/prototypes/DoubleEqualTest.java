package main.java.prototypes;

import main.java.model.MikeSimLogger;
import main.java.model.helpers.DoubleCompare;

/**
 * for testing and learning only.
 */
public class DoubleEqualTest {

    public void runTest(){

        MikeSimLogger.addLogEvent("Attempting double equality check. Adding 0.01");

        double number1 = 0.09d;
        double number2 = 0.08d;

        number2 = number2 + 0.01d;

        if(Double.compare(number1, number2) == 0) MikeSimLogger.addLogEvent("number 1 equals number 2");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        if(number1 == number2) MikeSimLogger.addLogEvent("Double equality test passed!");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        MikeSimLogger.addLogEvent("Doing it with (int)(double*precision) : ");
        int precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) MikeSimLogger.addLogEvent("number1 equals number2");
        else MikeSimLogger.addLogEvent("Int comparison - they are different");

        MikeSimLogger.addLogEvent("Attempting double equality check. Substracting 0.01");

        number1 = 0.09d;
        number2 = 0.10d;

        number2 = number2 - 0.01d;

        if(Double.compare(number1, number2) == 0) MikeSimLogger.addLogEvent("number 1 equals number 2");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        if(number1 == number2) MikeSimLogger.addLogEvent("Double equality test passed!");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        MikeSimLogger.addLogEvent("Doing it with (int)(double*precision) : ");
        precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) MikeSimLogger.addLogEvent("number1 equals number2");
        else MikeSimLogger.addLogEvent("Int comparison - they are different");


        MikeSimLogger.addLogEvent("Attempting double equality check. Substracting 0.0100005");

        number1 = 0.09d;
        number2 = 0.10d;

        number2 = number2 - 0.0100005d;

        if(Double.compare(number1, number2) == 0) MikeSimLogger.addLogEvent("number 1 equals number 2");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        if(number1 == number2) MikeSimLogger.addLogEvent("Double equality test passed!");
        else MikeSimLogger.addLogEvent("Double comparison - they are different");

        MikeSimLogger.addLogEvent("Doing it with (int)(double*precision) : ");
        precision = 100;
        if (((int)(number1*precision)) == ((int)(number2*precision))) MikeSimLogger.addLogEvent("number1 equals number2");
        else MikeSimLogger.addLogEvent("Int comparison - they are different");

        MikeSimLogger.addLogEvent("\nUsing DoubleCompare: ");
        if(DoubleCompare.equals(number1, number2)) MikeSimLogger.addLogEvent("They are equal");
        else MikeSimLogger.addLogEvent("The are different");


    }
}
