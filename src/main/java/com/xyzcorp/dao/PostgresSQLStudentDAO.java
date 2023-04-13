package com.xyzcorp.dao;

import com.xyzcorp.domain.Student;
import com.xyzcorp.domain.StudentDAO;
import com.xyzcorp.domain.StudentId;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PostgresSQLStudentDAO implements StudentDAO<Long> {
    private final Supplier<Connection> connectionSupplier;

    public PostgresSQLStudentDAO(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public Long persist(Student student) {
        Long generatedKey = null;
        try (Connection connection =
                 connectionSupplier.get();
             PreparedStatement preparedStatement =
                 connection.prepareStatement
                               ("INSERT INTO REGISTRATION (FIRSTNAME, " +
                                       "LASTNAME, STUDENTID) values (?,?,?);",
                                   Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, student.firstName());
            preparedStatement.setString(2, student.lastName());
            preparedStatement.setString(3, student.studentId().id());
            preparedStatement.execute();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            generatedKey = generatedKeys.getLong(1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return generatedKey;
    }

    @Override
    public List<Student> findAll() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<Student> findByFirstName(String firstName) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Optional<Student> findById(Long id) {
        try (Connection connection =
                 connectionSupplier.get();
             PreparedStatement preparedStatement =
                 connection.prepareStatement("SELECT ID, FIRSTNAME, LASTNAME," +
                     " STUDENTID FROM REGISTRATION WHERE ID = ?;")) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String studentIDString = resultSet.getString(4);
                return Optional.of(new Student(resultSet.getLong(1),
                    new StudentId(studentIDString), firstName, lastName));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                return Optional.empty();
            }
        } catch (SQLException sqlException) {
            return Optional.empty();
        }
    }

    //TODO: Get Rid of This
    public Supplier<Connection> getConnectionSupplier() {
        return connectionSupplier;
    }
}

