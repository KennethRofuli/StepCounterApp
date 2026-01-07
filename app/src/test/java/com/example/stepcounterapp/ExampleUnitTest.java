package com.example.stepcounterapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 * 
 * This test suite runs all unit tests for the Step Counter app.
 * Individual test classes:
 * - StepCalculationsTest: Tests for distance, speed, and time calculations
 * - StepSessionTest: Tests for StepSession entity
 * - ConvertersTest: Tests for Room TypeConverters
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        StepCalculationsTest.class,
        StepSessionTest.class,
        ConvertersTest.class
})
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    
    @Test
    public void testStepLengthConstant() {
        double stepLength = 0.78;
        assertTrue(stepLength > 0);
        assertTrue(stepLength < 1.0);
    }
}