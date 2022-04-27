package com.crisp.saleproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SaleProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleProjectApplication.class,args);
    }
}
