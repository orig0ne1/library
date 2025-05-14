package com.app.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class BeanConfig {
    @Value("${spring.datasource.url}")
    private String URL;

    @Bean
    public Connection connection() throws SQLException {
        return DatabaseConnection.getInstance(URL);
    }
    @Bean
    public DataClass dataClass(Connection connection) {
        return new DataClass(connection);
    }


}
