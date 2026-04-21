package com.yourname.invoiceapp.repository;

import com.yourname.invoiceapp.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByStatus(String status);

    Optional<Invoice> findByProcessInstanceId(String processInstanceId);
}
