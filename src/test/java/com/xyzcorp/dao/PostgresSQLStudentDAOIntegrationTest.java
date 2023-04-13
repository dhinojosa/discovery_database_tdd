package com.xyzcorp.dao;

import com.xyzcorp.domain.Student;
import com.xyzcorp.domain.StudentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class PostgresSQLStudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withInitScript("import.sql")
            .withDatabaseName("school");
    private static PGConnectionPoolDataSource source;
    private Supplier<Connection> connectionSupplier;

    @BeforeEach
    void setUp() {
        source = new PGConnectionPoolDataSource();
        source.setURL(postgreSQLContainer.getJdbcUrl());
        source.setUser(postgreSQLContainer.getUsername());
        source.setPassword(postgreSQLContainer.getPassword());
        source.setDatabaseName("school");
        connectionSupplier = () -> {
            try {
                return source.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Test
    void testCreateStudent() {
        Student student = new Student(-1L, new StudentId("3012"), "Robert", "Hernandez");
        PostgresSQLStudentDAO postgresSQLStudentDAO = new PostgresSQLStudentDAO(connectionSupplier);
        Long generatedKey = postgresSQLStudentDAO.persist(student);
        assertThat(generatedKey).isNotNull().isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCreateStudentAndFindByIdSuccess() {
        Student student = new Student(-1L, new StudentId("3012"), "Robert", "Hernandez");
        PostgresSQLStudentDAO postgresSQLStudentDAO = new PostgresSQLStudentDAO(connectionSupplier);
        Long generatedKey = postgresSQLStudentDAO.persist(student);
        Optional<Student> result = postgresSQLStudentDAO.findById(generatedKey);
        assertThat(result).map(Student::firstName).contains("Robert");
        assertThat(result).map(Student::lastName).contains("Hernandez");
    }
}
