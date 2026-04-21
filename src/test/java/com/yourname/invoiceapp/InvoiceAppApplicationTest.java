package com.yourname.invoiceapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvoiceAppApplicationTest {

    @Test
    void testApplicationClassExists() {
        // Verify that the application class can be loaded
        Class<?> appClass = InvoiceAppApplication.class;
        assertNotNull(appClass);
    }

    @Test
    void testApplicationMainMethodExists() {
        // Verify that main method exists
        try {
            InvoiceAppApplication.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Application main method not found", e);
        }
    }
}
