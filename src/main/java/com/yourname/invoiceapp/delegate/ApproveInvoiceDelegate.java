package com.yourname.invoiceapp.delegate;

import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApproveInvoiceDelegate implements JavaDelegate {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long invoiceId = (Long) execution.getVariable("invoiceId");

        invoiceRepository.findById(invoiceId).ifPresent(invoice -> {
            invoice.setStatus("APPROVED");
            invoiceRepository.save(invoice);
        });
    }
}
