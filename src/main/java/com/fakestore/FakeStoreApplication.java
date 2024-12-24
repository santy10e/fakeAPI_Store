package com.fakestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fakestore")
public class FakeStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(FakeStoreApplication.class, args);
    }
}
