package com.yourname.invoiceapp.delegate;

import com.yourname.invoiceapp.entity.Invoice;
import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateInvoiceDelegateTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private ValidateInvoiceDelegate validateInvoiceDelegate;

    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setId(1L);
        testInvoice.setInvoiceNumber("INV-001");
        testInvoice.setVendorName("Test Vendor");
        testInvoice.setAmount(1000.0);
        testInvoice.setStatus("NEW");
    }

    @Test
    void testExecute_ValidInvoice_WithPositiveAmount() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);
        when(execution.getVariable("amount")).thenReturn(1000.0);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).setVariable("validated", true);
        verify(execution).getVariable("invoiceId");
        verify(execution).getVariable("amount");
        verify(invoiceRepository).findById(1L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testExecute_InvalidInvoice_WithZeroAmount() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);
        when(execution.getVariable("amount")).thenReturn(0.0);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).setVariable("validated", false);
    }

    @Test
    void testExecute_InvalidInvoice_WithNegativeAmount() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);
        when(execution.getVariable("amount")).thenReturn(-500.0);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).setVariable("validated", false);
    }

    @Test
    void testExecute_InvalidInvoice_WithNullAmount() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);
        when(execution.getVariable("amount")).thenReturn(null);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).setVariable("validated", false);
    }

    @Test
    void testExecute_UpdatesInvoiceStatusToPendingApproval() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);
        when(execution.getVariable("amount")).thenReturn(1500.0);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(invoiceRepository).save(argThat(invoice ->
                "PENDING_APPROVAL".equals(invoice.getStatus())
        ));
    }

    @Test
    void testExecute_InvoiceNotFound() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(9999L);
        when(execution.getVariable("amount")).thenReturn(1000.0);
        when(invoiceRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act
        validateInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).setVariable("validated", true);
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }
}
