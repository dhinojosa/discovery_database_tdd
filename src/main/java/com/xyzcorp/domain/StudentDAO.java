package com.xyzcorp.domain;

import java.util.List;
import java.util.Optional;

public interface StudentDAO<A> {
    A persist(Student student);
    List<Student> findAll();
    List<Student> findByFirstName(String firstName);
    List<Student> findByLastName(String lastName);
    Optional<Student> findByStudentId(StudentId studentId);
    Optional<Student> findById(A id);
}
