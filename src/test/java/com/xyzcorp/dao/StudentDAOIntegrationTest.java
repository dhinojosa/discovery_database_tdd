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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class StudentDAOIntegrationTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("school");
    private static PGConnectionPoolDataSource source;

    @SuppressWarnings("SqlNoDataSourceInspection")
    @BeforeAll
    static void setUp() {
        source = new PGConnectionPoolDataSource();
        source.setURL(postgreSQLContainer.getJdbcUrl());
        source.setUser(postgreSQLContainer.getUsername());
        source.setPassword(postgreSQLContainer.getPassword());
        source.setDatabaseName("school");


        var createTableSQL = """
            CREATE TABLE REGISTRATION (
              ID        SERIAL      PRIMARY KEY,
              FIRSTNAME TEXT        NOT NULL,
              LASTNAME  TEXT        NOT NULL,
              STUDENTID VARCHAR(20) NOT NULL
            );
            """;

        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement(createTableSQL);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
    @Test
    void testPersistWithAFindByStudentId() {
        String firstName = "Esperanza";
        String lastName = "Garcia";
        StudentId studentId = new StudentId("001");
        Student student = new Student(-1L, studentId, firstName, lastName);
        Long generatedKey = null;

        try (Connection connection = source.getConnection();
             PreparedStatement preparedStatement =
                 connection.prepareStatement("""
                     Insert into REGISTRATION (FIRSTNAME, LASTNAME, STUDENTID) values (?, ?, ?);""", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, student.firstName());
            preparedStatement.setString(2, student.lastName());
            preparedStatement.setString(3, student.studentId().id());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            generatedKey = generatedKeys.getLong(1);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        assertThat(generatedKey).isNotNull();

        Optional<Student> optionalStudent = Optional.empty();

        try (Connection connection = source.getConnection()) {
            PreparedStatement findByStudentIDStatement =
                connection.prepareStatement("SELECT * from REGISTRATION where" +
                    " STUDENTID = ?");
            findByStudentIDStatement.setString(1, studentId.id());
            ResultSet resultSet = findByStudentIDStatement.executeQuery();
            if (resultSet.next()) {
                optionalStudent = Optional.of(new Student(
                    resultSet.getLong("ID"),
                    new StudentId(resultSet.getString("STUDENTID")),
                    resultSet.getString("FIRSTNAME"),
                    resultSet.getString("LASTNAME")));
            } else {
                optionalStudent = Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        optionalStudent.ifPresentOrElse(s -> {
            assertThat(s.id()).isNotNull();
            assertThat(s.firstName()).isEqualTo(firstName);
            assertThat(s.lastName()).isEqualTo(lastName);
            assertThat(s.studentId()).isEqualTo(studentId);
        }, () -> Assertions.fail("Did not find student"));
    }
}
