package com.yourname.invoiceapp;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("invoice-app")
public class InvoiceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvoiceAppApplication.class, args);
    }
}
