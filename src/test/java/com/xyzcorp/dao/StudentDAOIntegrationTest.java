package com.xyzcorp.dao;

import org.postgresql.ds.PGConnectionPoolDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class StudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withInitScript("import.sql")
            .withDatabaseName("school");
    private static PGConnectionPoolDataSource source;
    private static PostgresSQLStudentDAO postgresSQLStudentDAO;
}
