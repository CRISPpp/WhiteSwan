package com.crisp.saleproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j//提供log
@SpringBootApplication
@ServletComponentScan
public class SaleProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleProjectApplication.class,args);
        log.info("项目跑起来了");
    }
}
