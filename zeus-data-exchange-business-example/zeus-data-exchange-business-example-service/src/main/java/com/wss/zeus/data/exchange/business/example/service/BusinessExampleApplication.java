package com.wss.zeus.data.exchange.business.example.service;

import com.wss.zeus.data.exchange.business.example.api.feign.ExportFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 18:25
 */
@EnableDiscoveryClient
@EnableFeignClients(clients = ExportFeign.class)
@SpringBootApplication
public class BusinessExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessExampleApplication.class, args);
    }
}
