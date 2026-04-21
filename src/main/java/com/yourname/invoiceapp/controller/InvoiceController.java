package com.yourname.invoiceapp.controller;

import com.yourname.invoiceapp.entity.Invoice;
import com.yourname.invoiceapp.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Invoice> approveInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.approveInvoice(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Invoice> rejectInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.rejectInvoice(id));
    }
}
