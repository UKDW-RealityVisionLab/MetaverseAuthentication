package org.ukdw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.ukdw.entity.StudentEntity;
import org.ukdw.exception.RequestParameterErrorException;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentEntity> getAllStudents() {
        return studentRepository.findAll();
    }

    public StudentEntity findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found by id: "+ id));
    }

    public StudentEntity save(StudentEntity studentEntity) {
        try {
            return studentRepository.save(studentEntity);
        } catch (DataIntegrityViolationException e) {
            throw new RequestParameterErrorException("A student with the same ID already exists.");
        }
    }

    public StudentEntity update(Long id, StudentEntity studentEntity) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            StudentEntity existingStudent = studentOpt.get();

            if (studentEntity.getFirstName() != null) {
                existingStudent.setFirstName(studentEntity.getFirstName());
            }
            if (studentEntity.getLastName() != null) {
                existingStudent.setLastName(studentEntity.getLastName());
            }
            if (studentEntity.getNim() != null) {
                existingStudent.setNim(studentEntity.getNim());
            }
            if (studentEntity.getPhoneNumber() != null) {
                existingStudent.setPhoneNumber(studentEntity.getPhoneNumber());
            }
            if (studentEntity.getAddress() != null) {
                existingStudent.setAddress(studentEntity.getAddress());
            }
            if (studentEntity.getCity() != null) {
                existingStudent.setCity(studentEntity.getCity());
            }
            if (studentEntity.getRegion() != null) {
                existingStudent.setRegion(studentEntity.getRegion());
            }
            if (studentEntity.getCountry() != null) {
                existingStudent.setCountry(studentEntity.getCountry());
            }
            if (studentEntity.getZipCode() != null) {
                existingStudent.setZipCode(studentEntity.getZipCode());
            }
            if (studentEntity.getGender() != null) {
                existingStudent.setGender(studentEntity.getGender());
            }

            return studentRepository.save(existingStudent);
        }
        throw new ResourceNotFoundException("Student not found by id: "+ id);
    }

}
