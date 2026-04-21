package com.yourname.invoiceapp.controller;

import com.yourname.invoiceapp.entity.Invoice;
import com.yourname.invoiceapp.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

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
    void testCreateInvoice_Success() {
        // Arrange
        Invoice newInvoice = new Invoice();
        newInvoice.setInvoiceNumber("INV-002");
        newInvoice.setVendorName("New Vendor");
        newInvoice.setAmount(2000.0);

        Invoice createdInvoice = new Invoice();
        createdInvoice.setId(2L);
        createdInvoice.setInvoiceNumber("INV-002");
        createdInvoice.setVendorName("New Vendor");
        createdInvoice.setAmount(2000.0);
        createdInvoice.setStatus("NEW");

        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        // Act
        var result = invoiceController.createInvoice(newInvoice);

        // Assert
        assertNotNull(result);
        assertEquals(201, result.getStatusCodeValue());
        assertEquals(createdInvoice, result.getBody());
        verify(invoiceService, times(1)).createInvoice(any(Invoice.class));
    }

    @Test
    void testGetAllInvoices_Success() {
        // Arrange
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(testInvoice);
        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setInvoiceNumber("INV-002");
        invoice2.setVendorName("Another Vendor");
        invoice2.setAmount(3000.0);
        invoice2.setStatus("APPROVED");
        invoiceList.add(invoice2);

        when(invoiceService.getAllInvoices()).thenReturn(invoiceList);

        // Act
        var result = invoiceController.getAllInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    void testGetAllInvoices_Empty() {
        // Arrange
        when(invoiceService.getAllInvoices()).thenReturn(new ArrayList<>());

        // Act
        var result = invoiceController.getAllInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testGetInvoice_Success() {
        // Arrange
        when(invoiceService.getInvoice(1L)).thenReturn(testInvoice);

        // Act
        var result = invoiceController.getInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(testInvoice, result.getBody());
        verify(invoiceService, times(1)).getInvoice(1L);
    }

    @Test
    void testGetInvoice_NotFound() {
        // Arrange
        when(invoiceService.getInvoice(9999L))
                .thenThrow(new RuntimeException("Invoice not found with id: 9999"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            invoiceController.getInvoice(9999L);
        });
        assertEquals("Invoice not found with id: 9999", exception.getMessage());
        verify(invoiceService, times(1)).getInvoice(9999L);
    }

    @Test
    void testApproveInvoice_Success() {
        // Arrange
        Invoice approvedInvoice = new Invoice();
        approvedInvoice.setId(1L);
        approvedInvoice.setInvoiceNumber("INV-001");
        approvedInvoice.setVendorName("Test Vendor");
        approvedInvoice.setAmount(1000.0);
        approvedInvoice.setStatus("APPROVED");

        when(invoiceService.approveInvoice(1L)).thenReturn(approvedInvoice);

        // Act
        var result = invoiceController.approveInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("APPROVED", result.getBody().getStatus());
        verify(invoiceService, times(1)).approveInvoice(1L);
    }

    @Test
    void testRejectInvoice_Success() {
        // Arrange
        Invoice rejectedInvoice = new Invoice();
        rejectedInvoice.setId(1L);
        rejectedInvoice.setInvoiceNumber("INV-001");
        rejectedInvoice.setVendorName("Test Vendor");
        rejectedInvoice.setAmount(1000.0);
        rejectedInvoice.setStatus("REJECTED");

        when(invoiceService.rejectInvoice(1L)).thenReturn(rejectedInvoice);

        // Act
        var result = invoiceController.rejectInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("REJECTED", result.getBody().getStatus());
        verify(invoiceService, times(1)).rejectInvoice(1L);
    }
}

