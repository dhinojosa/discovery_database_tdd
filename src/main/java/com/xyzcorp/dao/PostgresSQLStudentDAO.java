package com.xyzcorp.dao;

import com.xyzcorp.domain.Student;
import com.xyzcorp.domain.StudentDAO;
import com.xyzcorp.domain.StudentId;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PostgresSQLStudentDAO implements StudentDAO<Long> {

    private final Supplier<Connection> supplier;

    public PostgresSQLStudentDAO(Supplier<Connection> supplier) {
        this.supplier = supplier;
    }

    void createTable() {
        try (Connection connection =
                 supplier.get()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement("""
            CREATE TABLE REGISTRATION (
              ID        SERIAL      PRIMARY KEY,
              FIRSTNAME TEXT        NOT NULL,
              LASTNAME  TEXT        NOT NULL,
              STUDENTID VARCHAR(20) NOT NULL
            );
            """);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Get rid of this!

    @Override
    public Long persist(Student student) {
        Long generatedKey;
        try (Connection connection = supplier.get();
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
        return generatedKey;
    }

    @Override
    public List<Student> findAll() {
        return null;
    }

    @Override
    public List<Student> findByFirstName(String firstName) {
        return null;
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return null;
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        Optional<Student> optionalStudent;
        try (Connection connection = supplier.get()) {
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
        return optionalStudent;
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.empty();
    }
}
