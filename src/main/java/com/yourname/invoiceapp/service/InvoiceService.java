package com.yourname.invoiceapp.service;

import com.yourname.invoiceapp.entity.Invoice;
import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final RuntimeService runtimeService;

    public InvoiceService(InvoiceRepository invoiceRepository, RuntimeService runtimeService) {
        this.invoiceRepository = invoiceRepository;
        this.runtimeService = runtimeService;
    }

    public Invoice createInvoice(Invoice invoice) {
        invoice.setStatus("NEW");
        Invoice saved = invoiceRepository.save(invoice);

        Map<String, Object> variables = new HashMap<>();
        variables.put("invoiceId", saved.getId());
        variables.put("invoiceNumber", saved.getInvoiceNumber());
        variables.put("vendorName", saved.getVendorName());
        variables.put("amount", saved.getAmount());

        String processInstanceId = runtimeService
                .startProcessInstanceByKey("invoice-approval", variables)
                .getId();

        saved.setProcessInstanceId(processInstanceId);
        return invoiceRepository.save(saved);
    }

    public Invoice approveInvoice(Long id) {
        Invoice invoice = getInvoice(id);
        invoice.setStatus("APPROVED");
        return invoiceRepository.save(invoice);
    }

    public Invoice rejectInvoice(Long id) {
        Invoice invoice = getInvoice(id);
        invoice.setStatus("REJECTED");
        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}
