package com.xyzcorp.dao;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class StudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("school");


}
