package com.indikore.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Test basic arithmetic operations")
    void testBasicOperations() {
        assertEquals(8.0, calculator.add(5.0, 3.0));
        assertEquals(2.0, calculator.subtract(5.0, 3.0));
        assertEquals(15.0, calculator.multiply(5.0, 3.0));
        assertEquals(2.5, calculator.divide(5.0, 2.0));
    }

    @Test
    @DisplayName("Test division by zero throws IllegalArgumentException")
    void testDivideByZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(10.0, 0.0);
        });
        assertEquals("Cannot divide by zero.", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "2, 3, 8.0",
        "5, 0, 1.0",
        "4, 2, 16.0"
    })
    @DisplayName("Test power calculations")
    void testPower(double base, double exponent, double expected) {
        assertEquals(expected, calculator.power(base, exponent));
    }

    @Test
    @DisplayName("Test factorial calculations")
    void testFactorial() {
        assertEquals(1L, calculator.factorial(0));
        assertEquals(1L, calculator.factorial(1));
        assertEquals(120L, calculator.factorial(5));
        assertEquals(720L, calculator.factorial(6));
    }

    @Test
    @DisplayName("Test factorial with negative numbers throws exception")
    void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-1));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31})
    @DisplayName("Test prime numbers return true")
    void testPrimeNumbers(int number) {
        assertTrue(calculator.isPrime(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0, 1, 4, 6, 8, 9, 10, 15, 25})
    @DisplayName("Test non-prime numbers return false")
    void testNonPrimeNumbers(int number) {
        assertFalse(calculator.isPrime(number));
    }

    @Test
    @DisplayName("Test large prime without integer overflow")
    void testLargePrime() {
        assertTrue(calculator.isPrime(Integer.MAX_VALUE));
    }
}
