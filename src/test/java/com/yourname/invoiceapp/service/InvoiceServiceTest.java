package com.yourname.invoiceapp.service;

import com.yourname.invoiceapp.entity.Invoice;
import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private ProcessInstance processInstance;

    @InjectMocks
    private InvoiceService invoiceService;

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
        Invoice invoiceToCreate = new Invoice();
        invoiceToCreate.setInvoiceNumber("INV-002");
        invoiceToCreate.setVendorName("New Vendor");
        invoiceToCreate.setAmount(2000.0);

        Invoice savedInvoice = new Invoice();
        savedInvoice.setId(2L);
        savedInvoice.setInvoiceNumber("INV-002");
        savedInvoice.setVendorName("New Vendor");
        savedInvoice.setAmount(2000.0);
        savedInvoice.setStatus("NEW");

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);
        when(runtimeService.startProcessInstanceByKey(eq("invoice-approval"), anyMap())).thenReturn(processInstance);
        when(processInstance.getId()).thenReturn("process-123");

        // Act
        Invoice result = invoiceService.createInvoice(invoiceToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("INV-002", result.getInvoiceNumber());
        assertEquals("NEW", result.getStatus());
        verify(invoiceRepository, times(2)).save(any(Invoice.class));
        verify(runtimeService, times(1)).startProcessInstanceByKey(eq("invoice-approval"), anyMap());
    }

    @Test
    void testCreateInvoice_SetsStatusToNew() {
        // Arrange
        Invoice invoiceToCreate = new Invoice();
        invoiceToCreate.setInvoiceNumber("INV-003");
        invoiceToCreate.setVendorName("Vendor");
        invoiceToCreate.setAmount(500.0);

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(runtimeService.startProcessInstanceByKey(eq("invoice-approval"), anyMap())).thenReturn(processInstance);
        when(processInstance.getId()).thenReturn("process-456");

        // Act
        Invoice result = invoiceService.createInvoice(invoiceToCreate);

        // Assert
        assertEquals("NEW", result.getStatus());
    }

    @Test
    void testApproveInvoice_Success() {
        // Arrange
        Invoice invoiceToApprove = new Invoice();
        invoiceToApprove.setId(1L);
        invoiceToApprove.setInvoiceNumber("INV-001");
        invoiceToApprove.setVendorName("Test Vendor");
        invoiceToApprove.setAmount(1000.0);
        invoiceToApprove.setStatus("PENDING_APPROVAL");

        Invoice approvedInvoice = new Invoice();
        approvedInvoice.setId(1L);
        approvedInvoice.setInvoiceNumber("INV-001");
        approvedInvoice.setVendorName("Test Vendor");
        approvedInvoice.setAmount(1000.0);
        approvedInvoice.setStatus("APPROVED");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoiceToApprove));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(approvedInvoice);

        // Act
        Invoice result = invoiceService.approveInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(invoiceRepository).findById(1L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testRejectInvoice_Success() {
        // Arrange
        Invoice invoiceToReject = new Invoice();
        invoiceToReject.setId(1L);
        invoiceToReject.setInvoiceNumber("INV-001");
        invoiceToReject.setVendorName("Test Vendor");
        invoiceToReject.setAmount(1000.0);
        invoiceToReject.setStatus("PENDING_APPROVAL");

        Invoice rejectedInvoice = new Invoice();
        rejectedInvoice.setId(1L);
        rejectedInvoice.setInvoiceNumber("INV-001");
        rejectedInvoice.setVendorName("Test Vendor");
        rejectedInvoice.setAmount(1000.0);
        rejectedInvoice.setStatus("REJECTED");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoiceToReject));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(rejectedInvoice);

        // Act
        Invoice result = invoiceService.rejectInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(invoiceRepository).findById(1L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testGetInvoice_Success() {
        // Arrange
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

        // Act
        Invoice result = invoiceService.getInvoice(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("INV-001", result.getInvoiceNumber());
        verify(invoiceRepository).findById(1L);
    }

    @Test
    void testGetInvoice_NotFound() {
        // Arrange
        when(invoiceRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            invoiceService.getInvoice(9999L);
        });
        assertEquals("Invoice not found with id: 9999", exception.getMessage());
    }

    @Test
    void testGetAllInvoices_Success() {
        // Arrange
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(testInvoice);
        Invoice invoice2 = new Invoice();
        invoice2.setId(3L);
        invoice2.setInvoiceNumber("INV-003");
        invoice2.setVendorName("Another Vendor");
        invoice2.setAmount(3000.0);
        invoice2.setStatus("APPROVED");
        invoiceList.add(invoice2);

        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        // Act
        List<Invoice> result = invoiceService.getAllInvoices();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(invoiceRepository).findAll();
    }

    @Test
    void testGetAllInvoices_Empty() {
        // Arrange
        when(invoiceRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Invoice> result = invoiceService.getAllInvoices();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
