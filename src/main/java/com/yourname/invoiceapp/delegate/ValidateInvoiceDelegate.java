package com.yourname.invoiceapp.delegate;

import com.yourname.invoiceapp.repository.InvoiceRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidateInvoiceDelegate implements JavaDelegate {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long invoiceId = (Long) execution.getVariable("invoiceId");
        Double amount = (Double) execution.getVariable("amount");

        if (amount != null && amount > 0) {
            execution.setVariable("validated", true);
        } else {
            execution.setVariable("validated", false);
        }

        invoiceRepository.findById(invoiceId).ifPresent(invoice -> {
            invoice.setStatus("PENDING_APPROVAL");
            invoiceRepository.save(invoice);
        });
    }
}
