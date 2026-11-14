package com.wss.zeus.data.exchange.center.example;

import com.wss.zeus.data.exchange.annotation.EnableExcelFeign;
import com.wss.zeus.data.exchange.business.example.api.feign.ExportFeign;
import com.wss.zeus.data.exchange.center.example.template.NormalTemplateConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 17:12
 */
@EnableDiscoveryClient
@EnableFeignClients(clients = ExportFeign.class)
@EnableExcelFeign(template = NormalTemplateConfig.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
