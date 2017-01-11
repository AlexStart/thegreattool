package com.sam.jcc.cloud.gallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 */
@SpringBootApplication(scanBasePackages = "com.sam.jcc.cloud")
public class GalleryStarter {
    public static void main(String[] args) {
        SpringApplication.run(GalleryStarter.class, args);
    }
}
