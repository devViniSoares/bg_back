package com.bigodeautopecas.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String url;

    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void testarConexao() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("Conexão com o banco de dados estabelecida com sucesso. URL: {}", url);
        } catch (SQLException e) {
            log.error("Falha ao conectar ao banco de dados. URL: {} | Erro: {}", url, e.getMessage());
        }
    }
}