package com.xyzcorp.dao;

import com.xyzcorp.domain.Student;
import com.xyzcorp.domain.StudentId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class StudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("school");
    private static PGConnectionPoolDataSource source;
    private static PostgresSQLStudentDAO postgresSQLStudentDAO;

    @SuppressWarnings("SqlNoDataSourceInspection")
    @BeforeAll
    static void setUp() {
        source = new PGConnectionPoolDataSource();
        source.setURL(postgreSQLContainer.getJdbcUrl());
        source.setUser(postgreSQLContainer.getUsername());
        source.setPassword(postgreSQLContainer.getPassword());
        source.setDatabaseName("school");


        postgresSQLStudentDAO = new PostgresSQLStudentDAO(() -> {
            try {
                return source.getConnection();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
        });


        postgresSQLStudentDAO.createTable();
    }

    @SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
    @Test
    void testPersistWithAFindByStudentId() {
        String firstName = "Esperanza";
        String lastName = "Garcia";
        StudentId studentId = new StudentId("001");
        Student student = new Student(-1L, studentId, firstName, lastName);
        assertThat(postgresSQLStudentDAO.persist(student)).isNotNull();
        postgresSQLStudentDAO
            .findByStudentId(studentId)
            .ifPresentOrElse(s -> {
                assertThat(s.id()).isNotNull();
                assertThat(s.firstName()).isEqualTo(firstName);
                assertThat(s.lastName()).isEqualTo(lastName);
                assertThat(s.studentId()).isEqualTo(studentId);
            }, () -> Assertions.fail("Did not find student"));
    }
}
