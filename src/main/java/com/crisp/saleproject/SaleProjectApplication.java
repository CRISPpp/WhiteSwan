package com.crisp.saleproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务支持
@EnableCaching //开启缓存
public class SaleProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleProjectApplication.class,args);
    }
}
