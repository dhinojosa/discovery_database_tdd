package com.xyzcorp.dao;

import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

@Testcontainers
public class StudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("school");
    private static PGConnectionPoolDataSource source;

    @BeforeAll
    static void setUp() {
        source = new PGConnectionPoolDataSource();
        source.setURL(postgreSQLContainer.getJdbcUrl());
        source.setUser(postgreSQLContainer.getUsername());
        source.setPassword(postgreSQLContainer.getPassword());
        source.setDatabaseName("school");
    }




}
