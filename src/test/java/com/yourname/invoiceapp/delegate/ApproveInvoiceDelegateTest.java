package com.yourname.invoiceapp.delegate;

import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveInvoiceDelegateTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private ApproveInvoiceDelegate approveInvoiceDelegate;

    @Test
    void testExecute_ApprovalProcess() throws Exception {
        // Act
        approveInvoiceDelegate.execute(execution);

        // Assert - Delegate executes without exception
        verify(execution, times(1)).getVariable("invoiceId");
    }

    @Test
    void testExecute_WithValidInvoiceId() throws Exception {
        // Arrange
        when(execution.getVariable("invoiceId")).thenReturn(1L);

        // Act
        approveInvoiceDelegate.execute(execution);

        // Assert
        verify(execution).getVariable("invoiceId");
    }
}
