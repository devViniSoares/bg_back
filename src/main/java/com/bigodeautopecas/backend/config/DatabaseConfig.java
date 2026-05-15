package com.bigodeautopecas.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void testarConexao() {
        try (Connection conn = getConexao()) {
            log.info("Conexão com o banco de dados estabelecida com sucesso. URL: {}", url);
        } catch (SQLException e) {
            log.error("Falha ao conectar ao banco de dados. URL: {} | Erro: {}", url, e.getMessage());
        }
    }

    public Connection getConexao() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
