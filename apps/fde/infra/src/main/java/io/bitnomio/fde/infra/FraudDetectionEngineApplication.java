package io.bitnomio.fde.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.myinvestor")
public class FraudDetectionEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionEngineApplication.class, args);
    }
}