package org.ukdw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.ukdw.dto.user.TeacherDTO;
import org.ukdw.entity.TeacherEntity;
import org.ukdw.exception.RequestParameterErrorException;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.repository.TeacherRepository;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<TeacherEntity> findAll() {
        return teacherRepository.findAll();
    }

    public TeacherEntity findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found by id: "+ id));
    }

    public TeacherEntity save(TeacherEntity teacherEntity) {
        try {
            return teacherRepository.save(teacherEntity);
        } catch (DataIntegrityViolationException e) {
            throw new RequestParameterErrorException("A teacher with the same ID already exists.");
        }
    }

    public TeacherEntity update(Long id, TeacherDTO teacherEntity) {
        Optional<TeacherEntity> teacherOpt = teacherRepository.findById(id);
        if (teacherOpt.isPresent()) {
            TeacherEntity teacher = teacherOpt.get();

            // Only update fields that are not null in the request
            if (teacherEntity.getFirstName() != null) {
                teacher.setFirstName(teacherEntity.getFirstName());
            }
            if (teacherEntity.getLastName() != null) {
                teacher.setLastName(teacherEntity.getLastName());
            }
            if (teacherEntity.getNid() != null) {
                teacher.setNid(teacherEntity.getNid());
            }
            if (teacherEntity.getAddress() != null) {
                teacher.setAddress(teacherEntity.getAddress());
            }
            if (teacherEntity.getPhoneNumber() != null) {
                teacher.setPhoneNumber(teacherEntity.getPhoneNumber());
            }
            if (teacherEntity.getRegion() != null) {
                teacher.setRegion(teacherEntity.getRegion());
            }
            if (teacherEntity.getCity() != null) {
                teacher.setCity(teacherEntity.getCity());
            }
            if (teacherEntity.getCountry() != null) {
                teacher.setCountry(teacherEntity.getCountry());
            }
            if (teacherEntity.getZipCode() != null) {
                teacher.setZipCode(teacherEntity.getZipCode());
            }
            if (teacherEntity.getGender() != null) {
                teacher.setGender(teacherEntity.getGender());
            }
            if (teacherEntity.getGoogleScholar() != null) {
                teacher.setGoogleScholar(teacherEntity.getGoogleScholar());
            }
            return teacherRepository.save(teacher);
        }
        throw new ResourceNotFoundException("Teacher not found by id: "+ id);
    }

}
