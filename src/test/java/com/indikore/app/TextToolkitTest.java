package com.indikore.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TextToolkitTest {

    private TextToolkit toolkit;

    @BeforeEach
    void setUp() {
        toolkit = new TextToolkit();
    }

    @Test
    @DisplayName("Test string reversal")
    void testReverse() {
        assertEquals("eroKidnI", toolkit.reverse("IndiKore"));
        assertEquals("", toolkit.reverse(""));
        assertNull(toolkit.reverse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"racecar", "madam", "A man a plan a canal Panama", "12321"})
    @DisplayName("Test valid palindromes return true")
    void testValidPalindromes(String input) {
        assertTrue(toolkit.isPalindrome(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "jenkins", "pipeline", "java"})
    @DisplayName("Test invalid palindromes return false")
    void testInvalidPalindromes(String input) {
        assertFalse(toolkit.isPalindrome(input));
    }

    @Test
    @DisplayName("Test word count functionality")
    void testCountWords() {
        assertEquals(4, toolkit.countWords("Jenkins CI CD Pipeline"));
        assertEquals(1, toolkit.countWords("Maven"));
        assertEquals(0, toolkit.countWords(""));
        assertEquals(0, toolkit.countWords("   "));
        assertEquals(0, toolkit.countWords(null));
    }

    @Test
    @DisplayName("Test title case conversion")
    void testToTitleCase() {
        assertEquals("Hello World From Jenkins", toolkit.toTitleCase("hello world from jenkins"));
        assertEquals("Java", toolkit.toTitleCase("java"));
        assertNull(toolkit.toTitleCase(null));
    }
}
