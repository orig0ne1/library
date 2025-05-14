package com.app.library;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
public class LibraryApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(LibraryApplication.class, args);

    }

    @Bean
    CommandLineRunner run(Page page) {
        return args -> {
            try {
                SwingUtilities.invokeLater(() -> page.setVisible(true));
            } catch (Exception e) {
                throw new RuntimeException("UI initialization failed", e);
            }
        };
    }

}