package com.indikore.app;

/**
 * Main command line application entry point.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      IndiKore Calculator & Text Toolkit v1.0     ");
        System.out.println("   CI/CD Pipeline Demo Project for Jenkins Tour   ");
        System.out.println("==================================================");

        Calculator calc = new Calculator();
        TextToolkit toolkit = new TextToolkit();

        // Sample calculations
        System.out.println("\n--- Sample Math Operations ---");
        System.out.println("5 + 3 = " + calc.add(5, 3));
        System.out.println("10 - 4 = " + calc.subtract(10, 4));
        System.out.println("6 * 7 = " + calc.multiply(6, 7));
        System.out.println("20 / 4 = " + calc.divide(20, 4));
        System.out.println("2^5 = " + calc.power(2, 5));
        System.out.println("5! = " + calc.factorial(5));
        System.out.println("Is 29 prime? " + calc.isPrime(29));

        // Sample text operations
        System.out.println("\n--- Sample Text Operations ---");
        String sampleText = "jenkins continuous integration and delivery";
        System.out.println("Original: " + sampleText);
        System.out.println("Title Case: " + toolkit.toTitleCase(sampleText));
        System.out.println("Reversed: " + toolkit.reverse("IndiKore"));
        System.out.println("Word Count: " + toolkit.countWords(sampleText));
        System.out.println("Is 'racecar' a palindrome? " + toolkit.isPalindrome("racecar"));

        boolean cliOnly = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) {
                cliOnly = true;
                break;
            }
        }

        if (cliOnly) {
            System.out.println("\n[CLI Mode] Verification completed successfully.");
            return;
        }

        int port = Integer.parseInt(
        System.getenv().getOrDefault("APP_PORT", "8080"));
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }

        try {
            WebServer webServer = new WebServer(port);
            webServer.start();
            System.out.println("\n==================================================");
            System.out.println("   >>> Web Dashboard active and listening! <<<     ");
            System.out.println("   Open your browser at: http://localhost:" + port + "/");
            System.out.println("   Health Check API:     http://localhost:" + port + "/api/health");
            System.out.println("==================================================");
            System.out.println("Press Ctrl+C in terminal to stop server.");

            // Keep main thread alive
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Server shutting down...");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
